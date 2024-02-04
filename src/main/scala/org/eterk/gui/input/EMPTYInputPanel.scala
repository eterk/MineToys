package org.eterk.gui.input

import scala.swing.Component

case object EMPTYInputPanel extends InputPanel[String] {
  setResult("")

  override def component: Component = new Component {}

  override val typeName: String = "EMPTY"
  override val label: String = "EMPTY"
  component.visible = false
}
