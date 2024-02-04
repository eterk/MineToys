package org.eterk.app


import org.eterk.util.{AudioAttribute, Util}
import ws.schild.jave.MultimediaObject

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.ByteBuffer
import scala.language.implicitConversions

object SplitWav extends App {

  //一个将字符串转换成16进制字符串

  // 定义一个函数，接受一个音频文件的路径，一个分割的时间间隔（以秒为单位），以及音频的采样率，声道数，比特率和编码格式作为参数
  def splitAudioFile(filePath: String,
                     interval: Int,
                     audioAttribute: AudioAttribute): Unit = {
    // 打开文件输入流和通道
    val file = new File(filePath)
    val fis = new FileInputStream(file)
    val fc = fis.getChannel


    msg(audioAttribute.toString)
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

  implicit def stringToMutimediaObj(path: String): MultimediaObject = new MultimediaObject(new File(path))

  def get(media: MultimediaObject): AudioAttribute = {
    // 获取 MultimediaObject 的信息，返回一个 MultimediaInfo 对象
    val audioInfo = media.getInfo.getAudio

    AudioAttribute(audioInfo.getSamplingRate, audioInfo.getChannels, audioInfo.getBitRate / 8, audioInfo.getDecoder)
  }

  override def appKey: String = "sw"

  override def paramTypeSeq: Seq[String] = Seq("DIR:wav", "INT")

  override def execute(params: String*): Unit = {

    val path = params(0)


    val secondInterval: Int = params(1).toInt * 60


    splitAudioFile(path, secondInterval, get(path))


  }
}
