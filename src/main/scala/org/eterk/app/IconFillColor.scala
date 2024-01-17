package org.eterk.app

import org.apache.commons.imaging.Imaging
import org.eterk.util.Util._

import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Color, GradientPaint}
import java.io.File

object IconFillColor extends App {


  object ICON {
    def create(path: String, postfix: String): ICON = {
      val outputPath: String = path.substring(0, path.lastIndexOf(".")) + "_" + postfix + ".ico"
      new ICON(path, outputPath)
    }
  }

  case class ICON(path: String, outputPath: String) {
    // 读取图标文件，得到一个缓冲图像对象
    val iconImage: BufferedImage = Imaging.getBufferedImage(new File(path))
    // 获取图像的宽度和高度
    val width: Int = iconImage.getWidth
    val height: Int = iconImage.getHeight

    def newImage() = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

  }


  // 定义一个函数，用于给图标文件上色
  def fillSingle(icon: String, color: String): String = {
    val i1 = ICON.create(icon, "single")
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




  def createMany(picDir: String, iconDir: String, colNums: Seq[Int], degrees: Seq[Int]) = {
    listFiles(iconDir, "ico", recursive = false)
      .foreach {
        icon =>
          listFiles(picDir, "jpg", recursive = false)
            .zipWithIndex
            .foreach {
              case (jpg, index) =>
                val colors = getDominantColor(jpg)
                val jpgName = getFileName(jpg)
                colNums
                  .foreach {
                    num =>
                      degrees
                        .foreach {
                          degree =>
                            fillGradient(icon, colors.take(num), degree, Seq(jpgName, num, degree).mkString("_"))
                        }
                  }


            }

      }
  }


  override def appName: String = "fill_icon"

  override def paramSeq: Seq[String] = Seq("icon_path", "color_path", "color_num", "degree")

  override def paramDescription: Seq[String] = Seq("图标路径", "颜色图标路径", "渐变数量", "渐变角度")

  override def appDescription: String = "给图标非透明部分填色"

  override def execute(params: String*): Unit = createMany(params(0), params(1), params(2).split(",").map(_.toInt), params(3).split(",").map(_.toInt))
}

