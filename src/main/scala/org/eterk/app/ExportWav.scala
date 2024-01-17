package org.eterk.app

import org.eterk.util.Util

import java.io.File

object ExportWav extends App {


  import ws.schild.jave._

  def audio(): AudioAttributes = {
    val audio = new AudioAttributes()
    audio.setCodec("pcm_s16le")
    audio.setSamplingRate(44100)
    audio.setChannels(2)
    audio.setBitRate(16)
    audio
  }

  /**
   * 我查阅了一下 funasr 的官网，发现它对音频文件的音质有以下的要求：
   * 采样率：建议使用 16 kHz 或 8 kHz 的采样率，不支持超过 48 kHz 的采样率。
   * 声道数：建议使用单声道的音频文件，如果使用双声道或多声道的音频文件，会自动转换为单声道，可能会导致音质下降。
   * 比特率：建议使用 16 bit 的比特率，不支持低于 8 bit 或高于 32 bit 的比特率。
   * 编码格式：建议使用 PCM，MP3，M4A，FLAC 等无损或有损压缩的编码格式，不支持 AMR，WMA，OGG 等其他编码格式。
   *
   * @return
   */
  def funasrRecommend(): AudioAttributes = {
    // 创建一个 AudioAttributes 对象
    val audio = new AudioAttributes()
    // 设置音频的编码格式为 PCM
    audio.setCodec("pcm_s16le")
    // 设置音频的采样率为 16 kHz
    audio.setSamplingRate(16000)
    // 设置音频的声道数为 1
    audio.setChannels(1)
    // 设置音频的比特率为 16 bit
    audio.setBitRate(16)
    // 返回 AudioAttributes 对象
    audio
  }

  def get(media: MultimediaObject) = {
    // 获取 MultimediaObject 的信息，返回一个 MultimediaInfo 对象
    val multimediaInfo = media.getInfo
    // 获取 MultimediaInfo 的音频信息，返回一个 AudioInfo 对象
    val audioInfo = multimediaInfo.getAudio
    // 打印音频的参数
    println("音频的采样率：" + audioInfo.getSamplingRate + " Hz")
    println("音频的声道数：" + audioInfo.getChannels + " 个")
    println("音频的比特率：" + audioInfo.getBitRate + " bit")
    println("音频的编码格式：" + audioInfo.getDecoder)
  }

  // 定义一个函数，接受一个mp4文件名，返回一个wav文件名
  def mp4ToWav(mp4FileName: String): String = {
    println(mp4FileName)
    // 创建一个源文件对象
    val source: File = new File(mp4FileName)
    // 创建一个目标文件对象，使用相同的文件名，但是扩展名为wav
    val target: File = new File(mp4FileName.substring(0, mp4FileName.lastIndexOf(".")) + ".wav")
    // 创建一个音频属性对象，设置编码格式为PCM，采样率为44100Hz，声道数为2，比特率为16

    // 创建一个编码属性对象，设置格式为wav，设置音频属性
    val attrs = new EncodingAttributes()
    attrs.setFormat("wav")
    attrs.setAudioAttributes(funasrRecommend())
    // 创建一个编码器对象
    val encoder = new Encoder()
    // 调用编码器的encode方法，将源文件转换为目标文件，使用编码属性
    val media = new MultimediaObject(source)

    get(media)
    encoder.encode(media, target, attrs)

    // 返回目标文件的文件名
    target.getName
  }


  override def appName: String = "EXPORT WAV"

  override def paramSeq: Seq[String] = Seq("input file")

  override def paramDescription: Seq[String] = Seq("输入文件")

  override def appDescription: String = "将mp4 导出wav 文件"

  override def execute(params: String*): Unit = {

    Util.filterFiles(params.head, _.endsWith(".mp4"), recursive = false).foreach(mp4ToWav)


  }

}
