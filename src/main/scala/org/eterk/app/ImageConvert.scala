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

  override def appKey: String = "ic"

  override def paramTypeSeq: Seq[String] = Seq("RADIO:bmp,png", "FILE:svg", "TEXT")

  override def execute(params: String*): Unit = {
    val func =
      params.head match {
        case isBMP if isBMP.toLowerCase == "bmp" => svgToBmp _
        case isPNG if isPNG.toLowerCase == "png" => svgToPNG _
      }

    require(params(1).endsWith(".svg"))
    val input = params(1)

    val output = {
      val name =
        if (params(2) == "") {
          System.currentTimeMillis()
        } else {
          params(2)
        }

      new File(input).getParent + "/" + name + ".png"
    }


    func(input, output)


  }

}
