package org.eterk.app

import org.eterk.util.{AudioAttribute, Util}

import scala.language.implicitConversions

import java.io.File

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


  // 定义一个函数，接受一个mp4文件名，返回一个wav文件名
  private def mp4ToWav(mp4FileName: String): String = {
    msg(mp4FileName)
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

  override def paramTypeSeq: Seq[String] = Seq("FILE:mp4,wmv", "INT")

  override def execute(params: String*): Unit = {

    val res: Seq[String] =
      Util
        .filterFiles(params.head, x => x.endsWith(".mp4") || x.endsWith("wmv"), recursive = false)
        .map(mp4ToWav)


    if (params(1) != "_") {
      res.foreach {
        path =>
          SplitWav.execute(path, params(1))
      }
    }
  }

}
