package org.eterk.app

import org.scalatest.funsuite.AnyFunSuiteLike

class ImageConvertTest extends AnyFunSuiteLike {

  test("pngToIcon") {
    //    ImageToIco.localFileToIco("S:\\util\\icon\\disk_c\\windows.png","")
//    ImageConvert.svgToBmp1("S:\\util\\icon\\disk_c\\windows.svg", "S:\\util\\icon\\disk_c\\windows.bmp")
    ImageConvert.svgToBmp("S:\\util\\icon\\disk_c\\windows.bmp","S:\\util\\icon\\disk_c\\xbox.ico")

  }

  test("das"){

    GradientIcon.execute("S:\\util\\clip","S:\\util\\pic\\color_design","2,3,4","45,90")
  }

}
