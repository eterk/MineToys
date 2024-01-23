package org.eterk

import org.eterk.util.LanguageSetting
import org.scalatest.funsuite.AnyFunSuiteLike

import java.io.File
import java.util.Locale

class MainTest extends AnyFunSuiteLike {

  test("main") {
    //    Main.main(Array("-e Factorial 3"))
    //    Main.main(Array("-e:Factorial,3"))

    //    Main.main(Array("-e:edc,S:\\util\\pic\\color_design,json,S:\\util\\pic\\color.json"))

    //    Main.main(Array("-e:fi,S:\\util\\icon\\disk_s,S:\\\\util\\\\pic\\\\color.json,3,45"))
    //    Main.main(Array("--help:Factorial"))
    //
    //    Main.main(Array("--help:Factorial -e:Factorial"))
    Main.main(Array("--list"))
    Main.main(Array("--help:fi"))
    //    Main.main(Array("-e:wtt,S:\\lib\\video"))
    //    Main.main(Array("-e:mew,S:\\lib\\video,16"))
  }
  test("i118n"){
//    println(getClass.getResource("/messages.zh"))
    Main.main(Array("--help:mew"))

//    print("en")
//    Main.main(Array("-lang:en --help:mew"))
  }

  test("color"){

  }


  test("requie appk unique") {

    AppFactory.availableApp.map(_.appKey).distinct.size == AppFactory.availableApp.size
  }
  test("lang"){
    LanguageSetting.lang
  }

test("dasdas"){

  println(getClass.getResource("/messages.zh"))
  println(new File(getClass.getResource("/").getPath).list().mkString(","))

}

}
