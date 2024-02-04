package org.eterk.script

import java.io.File
import scala.language.implicitConversions


object CreateLink {

  import scala.sys.process._

  implicit def pathToFile(p: String): File = new File(p)

  // 定义一个函数，用于在文件夹内创建链接
  def createLink(currenPath: File, targetPath: File): Unit = {
    require(targetPath.exists() && targetPath.isDirectory)
    require(currenPath.exists() && currenPath.isDirectory)

    val linkFolder: File = currenPath.getAbsoluteFile + "\\" + targetPath.getName

    val targetFolder = targetPath.getAbsolutePath
//
//    if (!linkFolder.exists()) {
//      linkFolder.mkdir()
//    }


    // 使用mklink命令创建一个符号链接，指向目标文件夹
    val cmd = s"cmd /c mklink /D $linkFolder $targetFolder"
    println(cmd)
    cmd.!
  }

  def main(args: Array[String]): Unit = {

    Seq(
      "S:\\\\dev",
      "S:\\\\lib"
//      "S:\\\\util"
    ).foreach(createLink("S:\\\\mid\\onedrive\\pc", _))


  }


}
