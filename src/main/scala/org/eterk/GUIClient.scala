package org.eterk

import org.eterk.gui.MainPage
import org.eterk.util.{Config, Logger}

import java.io.File
import scala.swing.Dimension

object GUIClient {

  def main(args: Array[String]): Unit = {


    val page = new MainPage()
    //    page.frame.pack()

    //    page.frame.setLocationRelativeTo(null)
    page.frame.visible = true


  }

}

object Resource {

  private val getResource: String => String = (path: String) => {
    require(path.startsWith("/"))
    if (getClass.getResource("/") == null) {
      new File(path.tail).getPath
    } else {
      getClass.getResource(path).getFile
    }
  }


  lazy val iconImage = new File((getResource("/static/mineToys.ico")))


}
