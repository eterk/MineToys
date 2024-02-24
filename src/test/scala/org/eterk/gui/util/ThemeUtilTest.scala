package org.eterk.gui.util

import org.scalatest.funsuite.AnyFunSuiteLike

class ThemeUtilTest extends AnyFunSuiteLike {



  test("getFonts"){

    ThemeUtil.getFonts().foreach(println)
  }

}
