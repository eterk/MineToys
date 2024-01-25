package org.eterk

import scala.io.StdIn.readLine
import com.osinka.i18n.Messages
import org.eterk.util.{Config, Theme}

object Client {

  import org.eterk.util.LanguageSetting._

  def main(args: Array[String]): Unit = {
    println(Messages("client.welcome"))
    Theme.printBanner()
    println(Config.parser.usage)
    import org.jline.reader._
    import org.jline.terminal._
    // 创建一个终端对象
    val terminal: Terminal = TerminalBuilder.terminal()
    // 创建一个读取器对象
    val reader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()


    var running = true
    while (running) {
      Config.msg(Config.showInfo() + " " + Messages("client.bye.tips"))
      // 读取用户的输入
      val input = reader.readLine(Messages("client.input"))
      // 根据用户的输入执行相应的操作
      input match {
        case c if "exit" == c || c == "q" =>
          // 在这里添加一个回车确认操作
          println(Messages("client.quit.confirm"))
          val confirm = readLine()
          if (confirm == "") {
            // 在这里添加一个bye和停留1秒的操作
            println(Messages("client.bye"))
            Thread.sleep(1000)
            running = false
          }
        case other => Main.main(other.split(" "))
      }
    }

  }
}
