package org.eterk.app

import org.scalatest.funsuite.AnyFunSuiteLike

class DesktopInITest extends AnyFunSuiteLike {


  test("list") {
    //    DesktopInI.parseIconAndFolder("S:\\util\\icon\\disk_c","C:\\")
    //      .foreach(println)
//    DesktopInI.setDesktopIni("S:\\util\\icon\\disk_c", "C:\\")
    DesktopInI.setDesktopIni("S:\\util\\icon\\disk_s", "S:\\")

    import scala.sys.process._

    //    """powershell -Command ".\\cmd\\utils.ps1"""".!


    //    DesktopInI.cmdIsIn()


  }


}
