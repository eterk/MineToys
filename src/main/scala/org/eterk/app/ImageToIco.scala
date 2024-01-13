package org.eterk.app

object ImageToIco extends App {

  // 导入需要的库

  import java.io.{File, FileInputStream, FileOutputStream}
  import java.awt.image.BufferedImage
  import javax.imageio.ImageIO
  import org.apache.batik.apps.rasterizer.SVGConverter

  // 定义一个函数，接受两个参数，一个是输入的本地文件路径，一个是输出的ico文件路径
  def localFileToIco(input: String, output: String): Unit = {
    // 创建一个文件对象，用来读取输入的本地文件
    val inputFile = new File(input)

    // 检查输入的文件是否存在，是否是一个文件，是否是一个图像文件
    if (inputFile.exists && inputFile.isFile && isImageFile(inputFile)) {
      // 定义一个临时的png文件路径
      val tempFile = input + ".png"

      // 判断输入的文件是否是一个svg文件
      if (isSvgFile(inputFile)) {
        // 如果是一个svg文件，使用SVG Rasterizer类将svg文件转换为png文件
        val converter = new SVGConverter()
        converter.setSources(Array(input))
        converter.setDst(new File(tempFile))
        converter.execute()
      } else {
        // 如果不是一个svg文件，直接复制文件到临时的png文件路径
        copyFile(input, tempFile)
      }

      // 读取临时的png文件的图像数据
      val inputImage = ImageIO.read(new File(tempFile))

      // 创建一个ico文件对象，用来写入输出的ico文件
      val outputFile = new File(output)

      // 调用ico转换函数，将输入图像转换为ico图像，并写入输出文件
//      convertToIco(inputImage, outputFile)
      pngToIco2(tempFile,output)

      // 删除临时的png文件
//      new File(tempFile).delete()

      // 打印成功的信息
      println(s"Successfully converted $input to $output")
    } else {
      // 如果输入的文件不符合要求，打印错误的信息
      println(s"Invalid input file: $input")
    }
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

  // 定义一个函数，判断一个文件是否是一个svg文件，根据文件的扩展名
  def isSvgFile(file: File): Boolean = {
    // 获取文件的扩展名
    val extension = file.getName.split("\\.").last.toLowerCase

    // 判断扩展名是否是svg格式
    extension match {
      case "svg" => true
      case _ => false
    }
  }

  // 定义一个函数，将一个文件复制到另一个文件
  def copyFile(source: String, destination: String): Unit = {
    // 获取源文件和目标文件的输入流和输出流
    val inputStream = new FileInputStream(source)
    val outputStream = new FileOutputStream(destination)

    // 定义一个缓冲区，用来存放读取的字节
    val buffer = Array[Byte]()

    // 定义一个变量，用来记录读取的字节数
    var bytesRead = 0

    // 循环读取输入流，直到结束
    while ( {
      bytesRead = inputStream.read(buffer); bytesRead != -1
    }) {
      // 将缓冲区的内容写入输出流
      outputStream.write(buffer, 0, bytesRead)
    }

    // 关闭输入流和输出流
    inputStream.close()
    outputStream.close()
  }

  // 定义一个函数，将一个图像对象转换为ico图像，并写入一个文件对象
  def convertToIco(image: BufferedImage, file: File): Unit = {
    // 定义一个ico图像的宽度和高度，单位是像素
    val icoWidth = 32
    val icoHeight = 32

    // 创建一个新的图像对象，用来存放ico图像
    val icoImage = new BufferedImage(icoWidth, icoHeight, BufferedImage.TYPE_INT_ARGB)

    // 获取ico图像的绘图对象
    val graphics = icoImage.createGraphics()

    // 将原始图像缩放并绘制到ico图像上
    graphics.drawImage(image, 0, 0, icoWidth, icoHeight, null)

    // 释放绘图对象的资源
    graphics.dispose()

    // 获取输出文件的输出流
    val outputStream = new FileOutputStream(file)

    // 将ico图像写入输出流
    ImageIO.write(icoImage, "ico", outputStream)

    // 关闭输出流
    outputStream.close()
  }

  // 引入java.awt.image包，用于处理图像
  import java.awt.image.BufferedImage
  // 引入javax.imageio包，用于读写图像文件
  import javax.imageio.ImageIO
  // 引入java.io包，用于操作文件
  import java.io.File

  // 定义一个函数，接受两个参数，分别是输入文件的地址和输出文件的地址
  def pngToIco1(input: String, output: String): Unit = {
    // 读取输入文件，得到一个BufferedImage对象
    val image = ImageIO.read(new File(input))
    // 获取原始图像的宽高
    val width = image.getWidth
    val height = image.getHeight
    // 计算裁剪的起始坐标和长度
    val x = if (width > height) (width - height) / 2 else 0
    val y = if (height > width) (height - width) / 2 else 0
    val size = math.min(width, height)
    // 创建一个新的BufferedImage对象，用于存储裁剪后的图像
    val cropped = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
    // 获取cropped图像的Graphics对象，用于绘制图像
    val graphics1 = cropped.getGraphics
    // 将原始图像的一部分绘制到cropped图像上
    graphics1.drawImage(image, 0, 0, size, size, x, y, x + size, y + size, null)
    // 释放Graphics对象的资源
    graphics1.dispose()
    // 创建一个新的BufferedImage对象，用于存储ico格式的图像
    // ico格式的图像的宽高都是16像素，类型是TYPE_INT_ARGB
    val ico = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
    // 获取ico图像的Graphics对象，用于绘制图像
    val graphics2 = ico.getGraphics
    // 将cropped图像缩放到16x16像素，并绘制到ico图像上
    graphics2.drawImage(cropped, 0, 0, 16, 16, null)
    // 释放Graphics对象的资源
    graphics2.dispose()
    // 写入输出文件，格式为ico
    ImageIO.write(ico, "ico", new File(output))
  }



  // 引入java.io包，用于操作文件
  import java.io.File
  // 引入net.coobird.thumbnailator包，用于处理图像
  import net.coobird.thumbnailator.Thumbnails
  import net.coobird.thumbnailator.geometry.Positions
  import net.coobird.thumbnailator.resizers.configurations.ScalingMode

  // 定义一个函数，接受两个参数，分别是输入文件的地址和输出文件的地址
  def pngToIco2(input: String, output: String): Unit = {
    // 使用Thumbnails类的builder方法，创建一个Thumbnails对象
    // 设置源文件的路径，目标格式为ico，宽高为16像素，缩放模式为高质量
    val thumbnails = Thumbnails.of(new File(input))
      .outputFormat("ico")
      .size(128, 128)
      .scalingMode(ScalingMode.BILINEAR)
    // 使用crop方法，将图像裁剪成正方形，以中心为基准
    thumbnails.crop(Positions.CENTER)
    // 使用toFile方法，将图像写入输出文件
    thumbnails.toFile(new File(output))
  }





  override def appName: String = "image to icon"

  override def paramSeq: Seq[String] = Seq("input", "output")

  override def paramDescription: Seq[String] = Seq("输入文件地址", "输出文件地址")

  override def appDescription: String = ""

  override def execute(params: String*): Unit = localFileToIco(params.head, params(1))

  override def help(): Unit = ???
}
