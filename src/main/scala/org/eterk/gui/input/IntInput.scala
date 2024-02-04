package org.eterk.gui.input


import scala.swing._
import scala.swing.event._

case class IntInput(val label: String) extends InputPanel[Int] {
  // 组件类型标签
  val typeName: String = "INT"

  // 创建一个文本框，限制只能输入数字
  private val textField: TextField = new TextField {
    // 设置文本框的列数
    columns = 10

    // 添加一个监听器，检测文本框的输入变化
    listenTo(this)

    // 定义一个方法，将字符串转换为整数，如果无法转换，返回 0
    def toInt(s: String): Int = {
      try {
        s.toInt
      } catch {
        case _: NumberFormatException => 0
      }
    }

    // 定义一个反应器，处理文本框的输入事件
    reactions += {
      // 当文本框的值改变时
      case ValueChanged(_) =>
        // 获取文本框的文本
        val text = this.text


        setResult(toInt(text))
    }
  }

  // 将文本框添加到 GUI 组件中
  panel.contents += textField


}
