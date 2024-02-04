package org.eterk.gui.input

import scala.swing.TextField
import scala.swing.event.ValueChanged


case class TextInput(label: String) extends InputPanel[String] {
  // 组件类型标签
  val typeName: String = "TEXT"

  // 创建一个文本框，限制只能输入数字
  private val textField: TextField = new TextField {
    // 设置文本框的列数
    columns = 10

    listenTo(this)

    // 定义一个反应器，处理文本框的输入事件
    reactions += {
      // 当文本框的值改变时
      case ValueChanged(_) =>
        // 获取文本框的文本
        val text = this.text

        setResult(text)
    }
  }

  // 将文本框添加到 GUI 组件中
  panel.contents += textField


}