package org.eterk

import com.osinka.i18n.Messages
import org.eterk.util.{Config, Logger, Theme}

import scala.io.StdIn.readLine

object InteractiveCommandLine extends Logger {

  import org.eterk.util.LanguageSetting._

  // 定义一个函数，将一个字节数组转换为十六进制的字符串
  def bytesToHex(bytes: Array[Byte]): String = {
    // 使用StringBuilder来拼接字符串
    val sb = new StringBuilder
    // 遍历每个字节
    for (b <- bytes) {
      // 将字节转换为无符号的整数，然后转换为十六进制的字符串，如果长度不足两位，前面补零
      val hex = f"${b & 0xff}%02x"
      // 将十六进制的字符串追加到StringBuilder中
      sb.append(hex)
    }
    // 返回StringBuilder的内容
    sb.toString
  }

//  def jlineTeminal() {
//    import org.jline.reader._
//    import org.jline.terminal._
//    val terminal: Terminal = TerminalBuilder.terminal()
//    //      .builder().encoding(Charset.forName("UTF-8")).build()
//
//    val reader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()
//
//  }


  def main(args: Array[String]): Unit = {
    msg(Messages("client.welcome"))
    Theme.printBanner()
    msg(Config.parser.usage)

    // 创建一个终端对象
    var running = true
    while (running) {
      msg(Config.showInfo() + " " + Messages("client.bye.tips"))
      // 读取用户的输入

      val input = readLine(Messages("client.input"))
      // 根据用户的输入执行相应的操作

      input match {
        case c if "exit" == c || c == "q" =>
          // 在这里添加一个回车确认操作
          msg(Messages("client.quit.confirm"))
          val confirm = readLine()
          if (confirm == "") {
            // 在这里添加一个bye和停留1秒的操作
            msg(Messages("client.bye"))
            Thread.sleep(1000)
            running = false
          }
        case other => Main.main(other.split(" "))
      }
    }

  }
}
