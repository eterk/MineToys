package org.eterk.app

import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder

import java.io.FileOutputStream


object ImageConvert extends App {

  // 导入需要的库

  import org.apache.batik.apps.rasterizer.SVGConverter

  import java.io.File

  // 定义一个函数，接受两个参数，一个是输入的本地文件路径，一个是输出的ico文件路径
  def svgToPNG(input: String, output: String): Unit = {
    val tempFile = new File(output)
    val converter = new SVGConverter()
    converter.setSources(Array(input))
    converter.setDst(tempFile)
    converter.execute()

  }

  // 导入java.awt.image.BufferedImage类和javax.imageio.ImageIO类

  import org.apache.batik.transcoder.TranscoderInput

  def svgToBmp(svg: String, bmp: String): Unit = {
    val output = new FileOutputStream(bmp)
    val doc = new SAXSVGDocumentFactory(null)
      .createDocument(new File(svg).toURI.toString)

    val transcode =
      new JPEGTranscoder()

    transcode.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 1.0f)


    transcode.transcode(new TranscoderInput(doc), new TranscoderOutput(output))
    output.flush()
    output.close()


  }


  // 定义一个函数，判断一个文件是否是一个图像文件，根据文件的扩展名
  def isImageFile(file: File): Boolean = {
    // 获取文件的扩展名
    val extension = file.getName.split("\\.").last.toLowerCase

    // 判断扩展名是否是常见的图像格式
    extension match {
      case "jpg" | "jpeg" | "png" | "gif" | "bmp" | "svg" => true
      case _ => false
    }
  }


  override def appName: String = "image to icon"

  override def paramSeq: Seq[String] = Seq("input", "output")

  override def paramDescription: Seq[String] = Seq("输入文件地址", "输出文件地址")

  override def appDescription: String = ""

  override def execute(params: String*): Unit = {
    svgToPNG(params.head, params(1))
  }

}
