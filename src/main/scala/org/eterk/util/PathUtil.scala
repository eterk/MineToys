package org.eterk.util

import java.io.File

object PathUtil extends Logger{


  def windowsToWsl(windowsPath: String): String = {
    val Array(disk, context) = new File(windowsPath).getAbsolutePath.split(":")
    msg("ss")
    s"/mnt/${disk.toLowerCase()}" + context.replaceAll("\\\\", "/")
  }

  def wslToWindows(windowsPath: String): String = {
    windowsPath match {
      case s"/mnt/${disk}/${other}" => disk + "://" + other
      case _ => throw new IllegalArgumentException(s"not wsl path ${windowsPath}")
    }
  }

  //  def wslToDocker(wslPath:String,dockerPath:String)(name:String)={
  //
  //
  //  }

}
