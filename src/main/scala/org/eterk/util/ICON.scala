package org.eterk.util

import org.apache.commons.imaging.Imaging

import java.awt.image.BufferedImage
import java.io.File


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
