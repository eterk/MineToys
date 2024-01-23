package org.eterk.app

import org.eterk.util.Util

import java.io.File
import scala.language.implicitConversions

object ExportWav extends App {


  import ws.schild.jave._

  def audio(): AudioAttributes = {
    AudioAttribute(44100, 2, 16, "pcm_s16le").create()
  }


  /**
   * 我查阅了一下 funasr 的官网，发现它对音频文件的音质有以下的要求：
   * 采样率：建议使用 16 kHz 或 8 kHz 的采样率，不支持超过 48 kHz 的采样率。
   * 声道数：建议使用单声道的音频文件，如果使用双声道或多声道的音频文件，会自动转换为单声道，可能会导致音质下降。
   * 比特率：建议使用 16 bit 的比特率，不支持低于 8 bit 或高于 32 bit 的比特率。
   * 编码格式：建议使用 PCM，MP3，M4A，FLAC 等无损或有损压缩的编码格式，不支持 AMR，WMA，OGG 等其他编码格式。
   */
  private val funasr: AudioAttribute = AudioAttribute(16000, 1, 16, "pcm_s16le")

  implicit def stringToMutimediaObj(path: String): MultimediaObject = new MultimediaObject(new File(path))

  def get(media: MultimediaObject): AudioAttribute = {
    // 获取 MultimediaObject 的信息，返回一个 MultimediaInfo 对象
    val audioInfo = media.getInfo.getAudio

    AudioAttribute(audioInfo.getSamplingRate, audioInfo.getChannels, audioInfo.getBitRate / 8, audioInfo.getDecoder)
  }

  // 导入需要的库

  import java.io.{File, FileInputStream, FileOutputStream}
  import java.nio.ByteBuffer

  case class AudioAttribute(sampleRate: Int,
                            channels: Int,
                            bitRate: Int,
                            codec: String) {

    override def toString: String = {
      s"""
         |音频的采样率:$sampleRate Hz;
         |音频的声道数:$channels;
         |音频的比特率:$bitRate ;
         |音频的编码格式:$codec;
         |""".stripMargin

    }

    def samplesPerByte: Double = (sampleRate * channels) / bitRate

    def bytesPerSample: Int = bitRate / (sampleRate * channels)


    // 计算每个分割文件的字节数（不包括头部）
    def bytesPerFile(interval: Int): Int = sampleRate * channels * bytesPerSample * interval

    def create(): AudioAttributes = {
      // 创建一个 AudioAttributes 对象
      val audio = new AudioAttributes()
      // 设置音频的编码格式为 PCM
      audio.setCodec(codec)
      // 设置音频的采样率为 16 kHz
      audio.setSamplingRate(sampleRate)
      // 设置音频的声道数为 1
      audio.setChannels(channels)
      // 设置音频的比特率为 16 bit
      audio.setBitRate(bitRate)
      // 返回 AudioAttributes 对象
      audio
    }
  }


  // 定义一个函数，接受一个音频文件的路径，一个分割的时间间隔（以秒为单位），以及音频的采样率，声道数，比特率和编码格式作为参数
  def splitAudioFile(filePath: String,
                     interval: Int,
                     audioAttribute: AudioAttribute): Unit = {
    // 打开文件输入流和通道
    val file = new File(filePath)
    val fis = new FileInputStream(file)
    val fc = fis.getChannel


    println(audioAttribute)
    // 根据不同的编码格式，确定文件头部的字节数
    // 这里只考虑了aac和mp3两种格式，其他格式可能需要另外处理
    val headerSize = audioAttribute.codec match {
      case aac if aac.contains("aac") => 56 // aac文件的头部一般为56个字节
      case "mp3" => 0 // mp3文件没有固定的头部，需要动态解析
      case "pcm_s16le ([1][0][0][0] / 0x0001)" => 78
      case pcm_s16le if pcm_s16le.contains("pcm_s16le") =>
        filePath match {
          case wav if wav.endsWith(".wav") => 44
          case wav if wav.endsWith(".aiff") => 54
          case wav if wav.endsWith(".au") => 24
          case _ => throw new IllegalArgumentException(s"Unsupported codec: ${audioAttribute.codec}")
        }

      case _ => throw new IllegalArgumentException(s"Unsupported codec: ${audioAttribute.codec}") // 不支持的格式，抛出异常
    }

    //    // 读取文件头部的字节数，存储到一个字节数组中
    val header = new Array[Byte](headerSize)
    fis.read(header)

    val name: String => String = Util.appendSuffix(file, _)

    val bytesPerFile = audioAttribute.bytesPerFile(interval)

    var fileIndex = 1
    Range(0, fc.size.toInt, bytesPerFile)
      .foreach { _ =>
        // 创建一个新的文件输出流和通道，用来写入分割文件
        val outputPath = name(fileIndex.toString)

        val fos = new FileOutputStream(outputPath)
        val fco = fos.getChannel

        println(header.size)

        // 写入头部信息
        fco.write(ByteBuffer.wrap(header))

        // 创建一个缓冲区，用来存储分割文件的数据
        val buffer = ByteBuffer.allocate(bytesPerFile)

        // 从文件输入通道中读取数据，写入缓冲区
        fc.read(buffer)

        // 翻转缓冲区，准备写入文件输出通道
        buffer.flip()

        // 写入文件输出通道
        fco.write(buffer)

        // 关闭文件输出流和通道
        fos.close()
        fco.close()

        // 序号加一
        fileIndex += 1
      }

    // 关闭文件输入流和通道
    fis.close()
    fc.close()
  }

  // 定义一个函数，接受一个mp4文件名，返回一个wav文件名
  private def mp4ToWav(mp4FileName: String): String = {
    println(mp4FileName)
    // 创建一个源文件对象
    val source: File = new File(mp4FileName)
    // 创建一个目标文件对象，使用相同的文件名，但是扩展名为wav
    val target: File = new File(mp4FileName.substring(0, mp4FileName.lastIndexOf(".")) + ".wav")
    // 创建一个音频属性对象，设置编码格式为PCM，采样率为44100Hz，声道数为2，比特率为16

    // 创建一个编码属性对象，设置格式为wav，设置音频属性
    val attrs = new EncodingAttributes()
    attrs.setFormat("wav")

    attrs.setAudioAttributes(funasr.create())
    // 创建一个编码器对象
    val encoder = new Encoder()
    // 调用编码器的encode方法，将源文件转换为目标文件，使用编码属性
    val media = new MultimediaObject(source)


    encoder.encode(media, target, attrs)

    // 返回目标文件的文件名
    target.getAbsolutePath
  }


  override def appKey: String = "mew"



  override def execute(params: String*): Unit = {

    val res =
      Util
        .filterFiles(params.head, _.endsWith(".mp4"), recursive = false)
        .map(mp4ToWav)

    if (params(1) != "_") {
      val secondInterval: Int = params(1).toInt * 60
      res.foreach {
        path =>
          println(path)
          splitAudioFile(path, secondInterval, get(path))
      }
    }

  }

}
