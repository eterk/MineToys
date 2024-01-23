package org.eterk.util

import org.apache.commons.imaging.{ImageFormats, Imaging}
import org.soualid.colorthief.MMCQ

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.{File, FileOutputStream}
import java.util

object Util {

  def write(img: BufferedImage, outputPath: String): Unit = {
    val out = new FileOutputStream(outputPath)
    // 将新图像写入文件，使用PNG格式
    Imaging.writeImage(img, out, ImageFormats.ICO, null)
    out.close()
  }

  def getCoordinate(degree: Int, width: Int, height: Int): (Int, Int, Int, Int) = {
    // 计算渐变的起点和终点的坐标，根据渐变角度的不同，有不同的计算方式
    degree match {
      case 0 => // 如果渐变角度为0，表示从左到右渐变，起点在左上角，终点在右上角
        (0, 0, width, 0)
      case 45 => // 如果渐变角度为45，表示从左上到右下渐变，起点在左上角，终点在右下角
        (0, 0, width, height)
      case 90 => // 如果渐变角度为90，表示从上到下渐变，起点在左上角，终点在左下角
        (0, 0, 0, height)
      case 135 => // 如果渐变角度为135，表示从右上到左下渐变，起点在右上角，终点在左下角
        (width, 0, 0, height)
      case 180 => // 如果渐变角度为180，表示从右到左渐变，起点在右上角，终点在左上角
        (width, 0, 0, 0)
      case 225 => // 如果渐变角度为225，表示从右下到左上渐变，起点在右下角，终点在左上角
        (width, height, 0, 0)
      case 270 => // 如果渐变角度为270，表示从下到上渐变，起点在左下角，终点在左上角
        (0, height, 0, 0)
      case 315 => // 如果渐变角度为315，表示从左下到右上渐变，起点在左下角，终点在右上角
        (0, height, width, 0)
      case _ => // 如果渐变角度不在以上的范围内，抛出一个异常
        throw new IllegalArgumentException("Invalid degree: " + degree)
    }
  }

  // 定义一个函数，接受一个jpg图片的路径，返回一个整数，表示图片的色调
  def extractHue(jpg: String): Int = {

    val color = getDominantColor(jpg).head
    val hsl = RGBtoHSL(color)
    // 返回色调的值，它是一个整数，范围是0到360，代表色彩环上的角度
    Math.round(hsl.h * 360).toInt
  }

  def isImage(x: String): Boolean = {
    x.toLowerCase().endsWith("png") || x.toLowerCase().endsWith("jpg")

  }

  def getDominantColorMap(dirOrImg: String): Map[String, Seq[Color]] = {
    Util.filterFiles(dirOrImg, Util.isImage, recursive = false)
      .map(p => {
        val colors = getDominantColor(p)
        val jpgName = getFileName(p)
        (jpgName, colors)
      }).toMap
  }

  def getDominantColor(jpg: String): Seq[Color] = {
    // 读取jpg图片，将其转换为BufferedImage对象
    val image = Imaging.getBufferedImage(new File(jpg))
    // 使用Color Thief的getDominantColor方法，从图像中提取主要的颜色，得到一个颜色数组

    val dominantColor: util.List[Array[Int]] = MMCQ.compute(image, 10) // 10 is the number of dominant colors to find
    // 将颜色列表转换为Scala的数组
    dominantColor.toArray(new Array[Array[Int]](0))
      .map {
        case Array(r, g, b) => new Color(r, g, b)
      }.toSeq

  }

  // 定义一个函数，将RGB颜色转换为HSL颜色
  def RGBtoHSL(color: Color): HSL = {
    // 将RGB分量转换为0到1之间的小数
    val r = color.getRed / 255.0
    val g = color.getGreen / 255.0
    val b = color.getBlue / 255.0

    // 计算RGB颜色的最大值和最小值
    val max = math.max(r, math.max(g, b))
    val min = math.min(r, math.min(g, b))

    // 计算亮度
    val l = (max + min) / 2.0

    // 如果最大值和最小值相等，说明颜色是灰色的，没有色相和饱和度
    if (max == min) {
      HSL(0, 0, l)
    } else {
      // 计算色相
      val h = if (max == r) {
        // 如果最大值是红色，根据绿色和蓝色的差值计算色相
        (g - b) / (max - min) * 60
      } else if (max == g) {
        // 如果最大值是绿色，根据蓝色和红色的差值计算色相，并加上120度的偏移
        (b - r) / (max - min) * 60 + 120
      } else {
        // 如果最大值是蓝色，根据红色和绿色的差值计算色相，并加上240度的偏移
        (r - g) / (max - min) * 60 + 240
      }

      // 将色相转换为0到360之间的角度
      val h360 = if (h < 0) h + 360 else h

      // 计算饱和度
      val s = if (l < 0.5) {
        // 如果亮度小于0.5，饱和度等于最大值和最小值的差值除以最大值和最小值的和
        (max - min) / (max + min)
      } else {
        // 如果亮度大于等于0.5，饱和度等于最大值和最小值的差值除以2减去最大值和最小值的和
        (max - min) / (2 - max - min)
      }

      // 返回HSL颜色
      HSL(h360, s, l)
    }
  }

  // 定义一个函数，将HSL颜色转换为RGB颜色
  def HSLtoRGB(hsl: HSL): Color = {
    // 将色相转换为0到1之间的小数
    val h = hsl.h / 360.0

    // 如果饱和度为0，说明颜色是灰色的，没有红、绿、蓝三个分量
    if (hsl.s == 0) {
      // 将亮度转换为0到255之间的整数
      val l = (hsl.l * 255).toInt

      // 返回RGB颜色，红、绿、蓝三个分量都等于亮度
      new Color(l, l, l)
    } else {
      // 定义一个辅助函数，用于计算RGB颜色的临时分量
      def hueToRGB(p: Double, q: Double, t: Double): Double = {
        // 如果t小于0，将t加上1
        val t1 = if (t < 0) t + 1 else t

        // 如果t大于1，将t减去1
        val t2 = if (t > 1) t - 1 else t

        // 根据t的值，返回不同的结果
        if (t1 < 1.0 / 6) {
          // 如果t小于1/6，返回p + (q - p) * 6 * t
          p + (q - p) * 6 * t1
        } else if (t1 < 1.0 / 2) {
          // 如果t小于1/2，返回q
          q
        } else if (t1 < 2.0 / 3) {
          // 如果t小于2/3，返回p + (q - p) * (2/3 - t) * 6
          p + (q - p) * (2.0 / 3 - t1) * 6
        } else {
          // 否则，返回p
          p
        }
      }

      // 计算RGB颜色的临时分量p和q
      val p = if (hsl.l < 0.5) {
        // 如果亮度小于0.5，p等于亮度乘以（1 + 饱和度）
        hsl.l * (1 + hsl.s)
      } else {
        // 如果亮度大于等于0.5，p等于亮度加上饱和度减去亮度乘以饱和度
        hsl.l + hsl.s - hsl.l * hsl.s
      }

      val q = 2 * hsl.l - p

      // 计算RGB颜色的红、绿、蓝三个分量
      val r = hueToRGB(q, p, h + 1.0 / 3)
      val g = hueToRGB(q, p, h)
      val b = hueToRGB(q, p, h - 1.0 / 3)

      // 将RGB颜色的分量转换为0到255之间的整数
      val r255 = (r * 255).toInt
      val g255 = (g * 255).toInt
      val b255 = (b * 255).toInt

      // 返回RGB颜色
      new Color(r255, g255, b255)
    }
  }
  // 导入java.io.File类，用于操作文件和文件夹

  import java.io.File

  // 定义一个函数，接受一个文件夹路径和一个文件后缀作为参数，返回一个字符串列表，表示所有符合条件的文件的完全路径
  def listFiles(folder: String, suffix: String, recursive: Boolean): Seq[String] = {
    // 创建一个File对象，表示给定的文件夹路径
    val dir = new File(folder)
    // 判断文件夹是否存在，如果不存在，返回一个空列表
    if (!dir.exists() || !dir.isDirectory) {
      List()
    } else {
      // 获取文件夹下的所有文件和子文件夹，返回一个数组
      val files = dir.listFiles()
      // 遍历数组，对每个元素进行处理
      files.flatMap {
        // 如果是文件，判断文件名是否以给定的后缀结尾，如果是，返回文件的完全路径，否则返回空列表
        case file if file.isFile =>
          if (file.getName.endsWith(suffix)) {
            List(file.getAbsolutePath)
          } else {
            List()
          }
        // 如果是文件夹，递归调用函数，返回子文件夹下的所有符合条件的文件的完全路径
        case folder if folder.isDirectory && recursive =>
          listFiles(folder.getAbsolutePath, suffix, recursive)
        // 其他情况，返回空列表
        case _ =>
          List()
      }.toList // 将数组转换为列表，返回结果
    }
  }

  def repalceFileFomat(path: String, suffix: String): String = {
    path.substring(0, path.lastIndexOf(".")) + "." + suffix
  }

  // 定义一个函数，接受一个文件名的序列，返回一个字符串
  def concatFiles(fileNames: Seq[String]): String = {
    // 创建一个空的字符串构建器
    val sb = new StringBuilder()
    // 遍历每个文件名
    for (fileName <- fileNames) {
      // 使用scala.io.Source从文件中读取内容，指定编码为utf-8
      val source = scala.io.Source.fromFile(fileName, "utf-8")
      // 将文件内容追加到字符串构建器中
      sb.append(source.mkString)
      // 关闭文件
      source.close()
    }
    // 返回字符串构建器的结果
    sb.toString()
  }


  def appendSuffix(file: File, suffix: String): String = {
    val abPath = file.getAbsolutePath
    val index = abPath.lastIndexOf(".")

    val (start, format) = {
      abPath.splitAt(index)
    }
    s"${start}_${suffix}$format"
  }

  // 定义一个函数，接受三个参数，一个是文件地址，一个是文件结尾符合的一个条件，一个是是否递归
  def filterFiles(path: String, condition: String => Boolean, recursive: Boolean): Seq[String] = {
    // 创建一个文件对象，用来表示输入的文件地址
    val file = new File(path)

    // 定义一个空的序列，用来存放符合条件的文件地址
    var result = Seq.empty[String]

    // 判断输入的文件是否存在，是否是一个文件或者文件夹
    if (file.exists) {
      if (file.isFile) {
        // 如果是一个文件，判断文件名是否符合条件
        val abPath = file.getAbsolutePath
        if (condition(abPath)) {
          // 如果符合条件，将文件地址添加到结果序列中
          result = result :+ abPath
        }
      } else if (file.isDirectory) {
        // 如果是一个文件夹，获取文件夹中的所有文件和子文件夹
        val files = file.listFiles

        // 遍历所有的文件和子文件夹
        for (f <- files) {
          // 如果是一个文件，判断文件名是否符合条件
          if (f.isFile && condition(f.getName)) {
            // 如果符合条件，将文件地址添加到结果序列中
            result = result :+ f.getAbsolutePath
          } else if (f.isDirectory && recursive) {
            // 如果是一个子文件夹，并且递归参数为真，调用自身函数，将子文件夹中符合条件的文件地址添加到结果序列中
            result = result ++ filterFiles(f.getAbsolutePath, condition, recursive)
          }
        }
      }
    }

    // 返回结果序列
    result
  }

  // 定义一个函数，判断一个文件是否是icon文件，即扩展名为.ico
  def isIconFile(file: File): Boolean = {
    file.isFile && file.getName.endsWith(".ico")
  }

  def getFileName(filePath: String): String = {
    val file = new File(filePath)
    val fileName = file.getName
    val dotIndex = fileName.lastIndexOf(".")
    if (dotIndex != -1) {
      fileName.substring(0, dotIndex)
    } else {
      fileName
    }
  }

}