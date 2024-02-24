package org.eterk.gui.util

object ThemeUtil {

  import java.awt.GraphicsEnvironment

  // 定义一个函数，返回一个 Seq[String]，包含所有可用的字体名称
  def getFonts(): Seq[String] = {
    // 获取 GraphicsEnvironment 对象
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    // 调用 getAvailableFontFamilyNames 方法，返回一个数组
    val fonts = ge.getAvailableFontFamilyNames()
    // 将数组转换为 Seq[String] 类型
    fonts.toSeq
  }


}
