package org.eterk

import org.scalatest.funsuite.AnyFunSuiteLike

class MainTest extends AnyFunSuiteLike {

  test("main") {
//    Main.main(Array("-e Factorial 3"))
//    Main.main(Array("-e:Factorial,3"))

//    Main.main(Array("-e:edc,S:\\util\\pic\\color_design,json,S:\\util\\pic\\color.json"))

    Main.main(Array("-e:fi,S:\\util\\icon\\disk_s,S:\\\\util\\\\pic\\\\color.json,3,45"))
    //    Main.main(Array("--help:Factorial"))
    //
    //    Main.main(Array("--help:Factorial -e:Factorial"))
        Main.main(Array("--list"))
        Main.main(Array("--help:fi"))
  }

  test("requie appk unique") {

    AppFactory.availableApp.map(_.appKey).distinct.size == AppFactory.availableApp.size
  }


}
