package org.eterk.util

object Theme {

  import ColorString._

  implicit class StringOut(str: String) {

    def appKey: String = str.cyan

    def group: String = str.red


  }


  // 用于打印banner的函数
  def printBanner(): Unit = {
    // banner的字符串数组
    val banner = Array(
      " __  __ _       _   _____     _   _  _____         _   _  ",
      "|  \\/  (_)     | | |_   _|   | | | ||_   _|       | | | | ",
      "| \\  / |_ _ __ | |_  | | ___ | |_| |  | | ___  ___| |_| | ",
      "| |\\/| | | '_ \\| __| | |/ _ \\| __| |  | |/ _ \\/ __| __| | ",
      "| |  | | | | | | |_  | | (_) | |_|_|  | |  __/\\__ \\ |_|_| ",
      "|_|  |_|_|_| |_|\\__| \\_/\\___/ \\__(_)  \\_/\\___||___/\\__(_) "
    )
    println(banner.toSeq.rainbow("\n"))
  }

}
