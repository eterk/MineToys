package org.eterk.gui.input

import scala.swing.MenuBar.NoMenuBar.{listenTo, reactions}
import scala.swing.event.ButtonClicked
import scala.swing.{BoxPanel, CheckBox, Orientation}

// 定义一个多选框的类，继承自 InputPanel[Seq[String]]
case class CheckBoxInput(label: String, options: Seq[String]) extends InputPanel[Seq[String]] {
  // 组件类型标签
  val typeName: String = "CHECKBOX"

  // 创建一个空的序列，用于存储用户选择的选项
  private var selected: Seq[String] = Seq.empty

  // 创建一个空的 GUI 组件，用于放置多选框
  private val checkBoxPanel: BoxPanel = new BoxPanel(Orientation.Horizontal)

  // 遍历选项，为每个选项创建一个多选框
  for (option <- options) {
    // 创建一个多选框，设置其文本为选项
    val checkBox: CheckBox = new CheckBox(option)

    // 添加一个监听器，检测多选框的状态变化事件
    listenTo(checkBox)

    // 定义一个反应器，处理多选框的状态变化事件
    reactions += {
      // 当多选框的状态改变时
      case ButtonClicked(_) =>
        // 如果多选框被选中
        if (checkBox.selected) {
          // 将其对应的选项添加到选择的序列中
          selected = selected :+ option
        } else {
          // 如果多选框被取消选中
          // 将其对应的选项从选择的序列中移除
          selected = selected.filterNot(_ == option)
        }

        // 更新结果
        setResult(selected)
    }

    // 将多选框添加到 GUI 组件中
    checkBoxPanel.contents += checkBox
  }

  // 将 GUI 组件添加到面板中
  panel.contents += checkBoxPanel
}
