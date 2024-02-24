package org.eterk.gui.input

import java.awt.Color
import java.awt.event.ActionEvent
import javax.swing.Timer
import scala.swing._

case class StatusIndicator(title: String, getStatus: () => Int, refreshTime: Int) extends BoxPanel(Orientation.Horizontal) {


  val nameLabel = new Label(s"$title:")
  val statusLight = new Label("💡")

  contents += nameLabel
  contents += statusLight


  // 创建一个定时器，每秒更新一次状态
  val timer = new Timer(refreshTime, (e: ActionEvent) => {
    val status = getStatus()
    statusLight.foreground = status match {
      case 0 => Color.RED
      case 1 => Color.GREEN
      //      case 3 => Color.GREEN
      case _ => Color.BLACK
    }
  })
  timer.start()
}