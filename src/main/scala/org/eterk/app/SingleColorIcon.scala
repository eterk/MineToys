package org.eterk.app


import org.eterk.util.{ICON, Util}
import org.eterk.util.Util.write

import java.io.File

object SingleColorIcon extends App {


  // 定义一个函数，用于给图标文件上色
  def fillSingle(icon: String, color: String, name: String): String = {
    val i1 = ICON.create(icon, name)
    import i1._
    // 将颜色字符串转换为整数值，表示ARGB格式的颜色
    val colorValue = Integer.parseInt(color, 16)

    val img = newImage()
    // 创建一个新的缓冲图像对象，用于存储修改后的图像

    // 遍历图标的每个像素
    for (x <- 0 until width; y <- 0 until height) {
      // 获取当前像素的颜色值
      val pixel = iconImage.getRGB(x, y)
      // 判断当前像素是否为空（透明度为0）
      if ((pixel >>> 24) == 0) {
        // 如果为空，保持原样
        img.setRGB(x, y, pixel)
      } else {
        // 如果不为空，将其填充为指定的颜色，保持原有的透明度
        img.setRGB(x, y, (pixel & 0xFF000000) | (colorValue & 0x00FFFFFF))
      }
    }

    write(img, outputPath)
    outputPath
  }

  override def appKey: String = "sci"

  override def paramTypeSeq: Seq[String] = Seq("FILE_DIR:ico", "TEXT", "TEXT")

  override def execute(params: String*): Unit = {


    val color = params(1).split(",")

    val name = params(2).split(",")

    val seq: Seq[String] = Util.filterFiles(params(0), x => Util.isIconFile(new File(x)), recursive = false)


    if (color.length != name.length) {
      msg(s"color.size!=name.size ${color.mkString(",")} get ${color.size}, but ${name.mkString(",")} get ${name.size}")
    }

    val colorName = color.zip(name)

    seq.foreach(f => {
      colorName.foreach {
        case (c, n) => fillSingle(f, c, n)
      }
    })

  }
}
