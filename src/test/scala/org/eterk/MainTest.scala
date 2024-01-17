package org.eterk

import org.scalatest.funsuite.AnyFunSuiteLike

class MainTest extends AnyFunSuiteLike {

  test("main") {

    Main.main(Array("-e Factorial"))
//    Main.main(Array("--list"))
//    Main.main(Array("--list -e Factorial"))
//    Main.main(Array("--help Hello"))
  }




}
