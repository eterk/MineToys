package org.eterk.gui.input

import org.eterk.gui.ComponentWrapper
import org.eterk.util.Logger

import scala.swing.event.ValueChanged
import scala.swing.{BoxPanel, Component, Label, Orientation, TextField}

/**
 * INT 只能输入数字的输入框
 * DIR 只能通过访问资源管理器选择文件夹的
 * TYPED_FILE 只能通过访问资源管理器选择特定格式的文件的
 * TYPED_FILE_DIR 只能通过访问资源管理器选择特定格式的文件或包含这种格式的文件的文件夹的（不用递归）
 * FILE 只能选择文件的，不限定格式
 * FILE_DIR 任意文件或文件夹
 * RANGEED_INT 只能输入特定问范围的数字
 * RADIO 只能选择特定的单选
 * SELECT_LIST 多选框
 */
trait InputPanel[T] extends ComponentWrapper {
  // 组件类型标签
  val typeName: String

  // 组件输入名称
  val label: String

  final private var res: T = _

  // 输入的结果数据
  final def result: T = res

  final def setResult(i: T): Unit = {
    this.res = i
  }

  override def component: Component = panel

  val panel: BoxPanel = InputPanel.panel(label)
}


object InputPanel extends Logger {


  def panel(label: String): BoxPanel = {
    new BoxPanel(Orientation.Vertical) {
      // 创建一个标签，显示组件输入名称
      contents += new Label(label)

    }
  }


  def apply(label: String, tpe: String): InputPanel[_] = {
    tpe match {
      case "TEXT" => TextInput(label)
      case "INT" => IntInput(label)
      case s if s.startsWith("FILE") || s.startsWith("DIR") => DirFileInput(s, label)
      case s if s.startsWith("RADIO") || s.startsWith("CHECKBOX") || s.startsWith("LISTBOX") => {
        val args = s.split(":")
        val options = args(1).split(",").toSeq
        args(0) match {
          case "RADIO" => RadioButtonInput(label, options, x => x)
          case "CHECKBOX" => CheckBoxInput(label, options)
          case "LISTBOX" => ListBoxInput(label, options)
        }
      }
      case _ =>
        msg(s"$label => $tpe")

        EMPTYInputPanel
    }
  }


}
