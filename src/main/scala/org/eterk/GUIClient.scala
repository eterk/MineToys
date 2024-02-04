package org.eterk

import org.eterk.gui.MainPage
import org.eterk.util.{Config, Logger}

import scala.swing.Dimension

object GUIClient {

  def main(args: Array[String]): Unit = {


    val page = new MainPage()
//    page.frame.pack()

//    page.frame.setLocationRelativeTo(null)
    page.frame.visible = true


  }

}
