package org.eterk.util

import org.scalatest.funsuite.AnyFunSuiteLike

class UtilTest extends AnyFunSuiteLike {

  test("testFilterFiles") {
    Util
      .filterFiles("s:\\\\lib\\\\video", _.endsWith(".mp4"), recursive = false)
      //      .map(mp4ToWav)
      .foreach(println)
  }

}
