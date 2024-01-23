package org.eterk.app


import org.eterk.util.{ICON, Util}
import org.eterk.util.Util.{getCoordinate, getDominantColor, getDominantColorMap, getFileName, listFiles, write}

import java.awt.{AlphaComposite, Color, GradientPaint}
import java.awt.image.BufferedImage
import java.io.File

object GradientIcon extends App {

  // 定义一个函数，创建一个新的图标，使用指定的颜色和渐变角度
  def fillGradient(icon: String, colorsSrc: Seq[Color], degree: Int, postFix: String): String = {
    val i1 = ICON.create(icon, postFix)
    val colors = colorsSrc match {
      case single :: Nil => Seq(single)
      case other => other
    }
    import i1._

    // 获取新图像的绘图对象
    val square = newImage()
    val g = square.createGraphics()

    val (x1, y1, x2, y2) = getCoordinate(degree, width, height)

    // 创建一个渐变画笔对象，使用指定的颜色和坐标
    // 这里使用了一个循环，每次使用两个相邻的颜色，分别作为起点和终点的颜色
    // 这样可以实现多个颜色的渐变效果
    for (i <- 0 until colors.length - 1) {
      // 获取第i个和第i+1个颜色
      val color1 = colors(i)
      val color2 = colors(i + 1)

      // 创建一个渐变画笔，使用这两个颜色
      val paint = new GradientPaint(x1, y1, color1, x2, y2, color2)

      // 设置绘图对象的画笔为渐变画笔
      g.setPaint(paint)

      // 绘制一个矩形，覆盖整个图像区域
      // 这里使用了一个透明度参数，根据颜色的数量，动态调整透明度
      // 这样可以实现渐变的层次感
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f / (colors.length - 1)))
      g.fillRect(0, 0, width, height)
    }

    // 设置绘图对象的混合模式为源在目标上，这样可以保留图标的透明部分，而修改不透明部分
    g.setComposite(AlphaComposite.SrcAtop)


    // 绘制图标图像，覆盖在渐变的矩形上
    //    g.drawImage(iconImage, 0, 0, null)


    // 假设你有两个BufferedImage对象，分别命名为A和B，它们的长宽一样
    // 创建一个新的BufferedImage对象，用于存储修改后的图像，它的长宽和A、B一样，类型为ARGB
    val img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    // 遍历A的每个像素
    for (x <- 0 until width; y <- 0 until height) {
      // 获取A的当前像素的颜色值
      val pixelA = iconImage.getRGB(x, y)
      // 判断A的当前像素是否为空（透明度为0）
      if ((pixelA >>> 24) == 0) {
        // 如果为空，将C的对应像素设为A的像素
        img.setRGB(x, y, pixelA)
      } else {
        // 如果不为空，将C的对应像素设为B的对应像素
        val pixelB = square.getRGB(x, y)
        img.setRGB(x, y, pixelB)
      }
    }

    // 释放绘图对象的资源
    // 创建一个新的文件对象，用于存储修改后的图像
    write(img, outputPath)
    // 返回文件的路径
    outputPath
  }


  def createMany(nameColors: Map[String, Seq[Color]],
                 iconDir: String,
                 colorNums: Seq[Int],
                 degrees: Seq[Int]): Unit = {
    listFiles(iconDir, "ico", recursive = false)
      .foreach {
        icon =>
          println(icon)
          nameColors.foreach {
            case (jpgName, colors) =>
              colorNums
                .filter(x => x <= colors.size)
                .foreach {
                  num =>
                    degrees
                      .foreach {
                        degree =>
                          val name = Seq(jpgName, num, degree).mkString("_")
                          fillGradient(icon, colors.take(num), degree, name)
                      }
                }

          }


      }
  }




  override def appKey: String = "fi"
  override def appName: String = "fill_icon"
  override def paramSeq: Seq[String] = Seq("icon_path", "color_path", "color_num", "degree")

  override def appDescription: String = "给图标非透明部分填色"

  override def execute(params: String*): Unit = {

    val nameColors: Map[String, Seq[Color]] = params(1) match {
      case dirOrImg if new File(dirOrImg).isDirectory || Util.isImage(dirOrImg) => getDominantColorMap(dirOrImg)
      case txt => DominantColor.jsonToMap(Util.concatFiles(txt :: Nil))
    }


    createMany(nameColors, params(0), params(2).split(",").map(_.toInt), params(3).split(",").map(_.toInt))
  }
}
