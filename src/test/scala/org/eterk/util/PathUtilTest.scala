package org.eterk.util

import org.scalatest.funsuite.AnyFunSuiteLike

class PathUtilTest extends AnyFunSuiteLike {

  import PathUtil._
  test("das"){
    val wsl1=windowsToWsl("s:/lib/test")

    println(wsl1)

    val window=wslToWindows(wsl1)
    println(window)
    println(windowsToWsl("s:/lib/tesasdat"))
    println(windowsToWsl("s:/lib/tesasdat"))

  }

}
