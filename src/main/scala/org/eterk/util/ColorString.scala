package org.eterk.util


import scala.collection.mutable


object ColorString {

  import org.fusesource.jansi.AnsiConsole

  AnsiConsole.systemInstall()
  import org.fusesource.jansi.Ansi._
  import org.fusesource.jansi.Ansi.Color._


  // 定义一个函数，接受一个序列，一个颜色序列，和一个分隔符作为参数，返回一个带有颜色的字符串
  def colorString(seq: Seq[String], colors: Seq[String => String], sep: String): String = {
    // 使用zipWithIndex方法，将序列的元素和它们的索引组合起来，得到一个新的序列
    val indexedSeq = seq.zipWithIndex

    // 使用map方法，对新的序列的每个元素，根据它们的索引，从颜色序列中选择一个颜色，然后用颜色方法来设置元素的颜色，得到一个带有颜色的序列
    val coloredSeq = indexedSeq.map { case (elem, index) =>
      val color = colors(index % colors.length) // 用取余运算，从颜色序列中循环选择颜色
      color(elem) // 用颜色方法来设置元素的颜色
    }

    // 使用mkString方法，将带有颜色的序列用分隔符连接起来，得到一个带有颜色的字符串
    coloredSeq.mkString(sep)
  }


  implicit class SeqStringImp(seq: Seq[String]) {
    def magentaYellow(sep: String): String = {

      val colFunc = Seq((str: String) => str.red, (str: String) => str.green)


      colorString(seq, colFunc, sep)

    }

  }


  implicit class ColorStringImplicit(str: String) {

    private def use(color: Color): String = ansi().fg(color).a(str).reset().toString

    def red: String = use(RED)

    def yellow: String = use(YELLOW)


    def black: String = use(BLACK)

    def white: String = use(WHITE)

    def cyan: String = use(CYAN)

    def magenta: String = use(MAGENTA)

    def green: String = use(GREEN)

    def blue: String = use(BLUE)

  }

}
