package org.eterk.gui.input

import scala.swing.MenuBar.NoMenuBar.{listenTo, reactions}
import scala.swing.{BoxPanel, ButtonGroup, ListView, Orientation, RadioButton, ScrollPane}
import scala.swing.event.{ButtonClicked, SelectionChanged}



// 定义一个单选框的类，继承自 InputPanel[String]
case class RadioButtonInput(override val label: String,
                            options: Seq[String],
                            additionCallBack:String=>Unit) extends InputPanel[String] {
  // 组件类型标签
  val typeName: String = "RADIO"

  // 创建一个按钮组，用于管理单选框的互斥性
  private val buttonGroup: ButtonGroup = new ButtonGroup

  // 创建一个空的 GUI 组件，用于放置单选框
  private val radioButtonPanel: BoxPanel = new BoxPanel(Orientation.Horizontal)

  // 遍历选项，为每个选项创建一个单选框
  for (option <- options) {
    // 创建一个单选框，设置其文本为选项

    val radioButton: RadioButton = new RadioButton(option)

    // 将单选框添加到按钮组中
    buttonGroup.buttons += radioButton

    // 添加一个监听器，检测单选框的状态变化事件
    listenTo(radioButton)

    // 定义一个反应器，处理单选框的状态变化事件
    reactions += {
      // 当单选框的状态改变时
      case ButtonClicked(_) =>
        // 如果单选框被选中
        if (radioButton.selected) {
          // 更新结果为其对应的选项
          setResult(option)
          additionCallBack(option)
        }
    }

    // 将单选框添加到 GUI 组件中
    radioButtonPanel.contents += radioButton
  }

  // 将 GUI 组件添加到面板中
  panel.contents += radioButtonPanel
}

// 定义一个列表框的类，继承自 InputPanel[Seq[String]]
case class ListBoxInput(label: String, options: Seq[String]) extends InputPanel[Seq[String]] {
  // 组件类型标签
  val typeName: String = "LISTBOX"

  // 创建一个空的序列，用于存储用户选择的选项
  private var selected: Seq[String] = Seq.empty

  // 创建一个列表框，设置其可见行数为 5
  private val listBox: ListView[String] = new ListView[String] {
    // 设置列表框的可见行数
    visibleRowCount = 5
  }

  // 添加一个监听器，检测列表框的选择事件
  listenTo(listBox.selection)

  // 定义一个反应器，处理列表框的选择事件
  reactions += {
    // 当列表框的选择改变时
    case SelectionChanged(_) =>
      // 获取列表框的选择模型
      val selectionModel = listBox.selection

      // 获取选择的索引
      val indices = selectionModel.indices

      // 根据索引获取选择的选项
      selected = indices.map(options(_)).toSeq

      // 更新结果
      setResult(selected)
  }

  // 将列表框添加到面板中
  panel.contents += new ScrollPane(listBox)
}
