package org.eterk.app

import org.eterk.util.{AudioAttribute, Util}
import ws.schild.jave.encode.{AudioAttributes, EncodingAttributes}

import scala.language.implicitConversions
import java.io.File

object ExportWav extends TypedApp[Seq[String]] {


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
  import ws.schild.jave.process.ProcessLocator
  import ws.schild.jave.process.ProcessWrapper

  // 没有起作用
  class CustomFFMPEGLocator extends ProcessLocator {
    override def getExecutablePath: String = {
      "C:\\Users\\Administrator\\AppData\\Local\\Microsoft\\WinGet\\Packages\\Gyan.FFmpeg.Essentials_Microsoft.Winget.Source_8wekyb3d8bbwe\\ffmpeg-7.0.2-essentials_build\\bin\\ffmpeg.exe"
    }

    override def createExecutor(): ProcessWrapper = {
      new ProcessWrapper(getExecutablePath)
    }
  }


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
//    attrs.setFormat("wav")
// 手动添加需要的同名的fmpeg .exe 到指定目录
    attrs.setOutputFormat("wav")

    attrs.setAudioAttributes(funasr.create())
    // 创建一个编码器对象

    // 调用编码器的encode方法，将源文件转换为目标文件，使用编码属性
    val media = new MultimediaObject(source)

    val encoder = new Encoder(new CustomFFMPEGLocator())

    encoder.encode(media, target, attrs)

    // 返回目标文件的文件名
    target.getAbsolutePath
  }


  override def appKey: String = "mew"

  override def paramTypeSeq: Seq[String] = Seq("FILE_DIR:mp4,wmv", "INT")

  override def execute(params: String*): Seq[String] = {

    val res: Seq[String] =
      Util
        .filterFiles(params.head, x => x.endsWith(".mp4") || x.endsWith("wmv"), recursive = false)
        .map(mp4ToWav)

    val splitPart =
      if (params(1) != "_") {
        res.map {
          path =>
            SplitWav.execute(path, params(1))
        }
      } else {
        Nil
      }


    if (res.size != splitPart.size) {
      res
    } else {

      res.zip(splitPart)
        .map {
          case (wav, dir) =>
            val dirFile = new File(dir)
            if (dirFile.listFiles().length == 1) {
              msg(s"${dirFile.getAbsolutePath} delete:   ${dirFile.delete()}")
              wav
            } else {
              val wavFile = new File(wav)
              msg(s"${wavFile.getAbsolutePath} delete:   ${wavFile.delete()}")
              dir
            }
        }

    }

  }

}
