package org.eterk.app

import org.eterk.util.Util.isIconFile

import scala.collection.mutable
import java.io.File
import scala.tools.nsc.io.{File => ScalaFile}
import scala.sys.process._


object DesktopInI extends App {

  // 导入scala.sys.process包，用于执行powershell命令

  // 导入scala.util.logging包，用于记录日志
  //  import scala.util.logging._

  // 定义一个函数，接受两个字符串参数，分别表示源路径和目标路径
  def setDesktopIni(src: String, tgt: String): Unit = {

    parseIconAndFolder(src, tgt)
      .foreach {
        case (iconPath, folderPath) =>
          setDesktopIni(folderPath, "", iconPath, "", 1)
      }
  }


  private def setUTF8(): Unit = {
    // 在$PROFILE文件的末尾，添加以下命令，设置PowerShell的全局编码为UTF8
    val cmd =
      """
        |$OutputEncoding = [System.Text.Encoding]::UTF8;
        |[Console]::OutputEncoding = [System.Text.Encoding]::UTF8;
        |""".stripMargin
    runPowershell(cmd)
  }


  // 定义一个函数，解析两个文件夹中的icon和文件夹，返回一个Seq[(String, String)]，表示符合条件的图标地址和文件夹
  def parseIconAndFolder(src: String, tgt: String): Seq[(String, String)] = {
    // 创建一个可变的Seq，用于存储结果
    val result = mutable.Buffer.empty[(String, String)]
    // 创建一个File对象，表示源路径
    val srcDir = new File(src)
    // 判断源路径是否存在且是一个文件夹
    if (srcDir.exists && srcDir.isDirectory) {
      // 遍历源路径下的所有文件和文件夹
      for (file <- srcDir.listFiles) {
        // 判断是否是一个icon文件
        if (isIconFile(file)) {
          // 获取icon文件的文件名，不包括扩展名
          val fileName = file.getName.dropRight(4)
          // 创建一个File对象，表示目标路径下的同名文件夹
          val tgtDir = new File(tgt, fileName)
          // 判断目标路径下的同名文件夹是否存在且是一个文件夹
          if (tgtDir.exists && tgtDir.isDirectory) {
            // 将图标地址和文件夹添加到结果Seq中
            result.append((file.getAbsolutePath, tgtDir.getAbsolutePath))
          } else {
            // 如果不符合条件，记录一条警告日志
            println(s"not found same name dir: ${file.getName}")
          }
        }
      }
    }
    // 返回结果Seq
    result.toSeq
  }

  def runPowershell(cmd: String): Unit = {
    // 拼接powershell命令字符串，使用Bypass执行策略，注意转义引号和空格
    val fullCmd = s"""powershell -ExecutionPolicy Bypass -Command "$cmd""""
    // 执行命令，忽略输出
    fullCmd.!
  }

  // 定义一个函数，设置文件的权限为完全控制，传入一个字符串，表示文件路径
  def setFileFullControl(filePath: String): Unit = {
    // 拼接powershell命令字符串，使用icacls命令，传入文件路径和权限参数
    val cmd = s"""icacls "$filePath" /grant Everyone:F"""
    // 调用runPowershell函数，执行命令
    runPowershell(cmd)
  }

  // 定义一个函数，设置文件的属性为隐藏和系统，传入一个字符串，表示文件路径
  def setFileHiddenAndSystem(filePath: String): Unit = {
    // 拼接powershell命令字符串，使用attrib命令，传入文件路径和属性参数
    val cmd = s"""attrib +h +s "$filePath""""
    // 调用runPowershell函数，执行命令
    runPowershell(cmd)
  }

  def desktopIni(bgPic: String,
                 iconPath: String,
                 info: String,
                 confirm: Int): String = {
    s"""[Unicode]
       |[.ShellClassInfo]
       |IconResource="$iconPath",0
       |IconFile="$iconPath"
       |IconIndex=0
       |InfoTip=$info
       |ConfirmFileOp=$confirm
       |""".stripMargin
  }

  // 定义一个函数，接受五个参数，分别表示文件夹路径，背景图片，图标路径，信息提示，确认操作
  def setDesktopIni(folderPath: String,
                    bgPic: String,
                    iconPath: String,
                    info: String,
                    confirm: Int): Unit = {

    // 定义一个函数，执行powershell命令，传入一个字符串，表示命令或表达式
    // 使用try-catch语句来处理异常
    try {
      setUTF8()
      // 删除配置文件，并重新写入
      // 创建一个desktop.ini文件，用来存储文件夹的图标信息
      val desktopIniPath = folderPath + File.separator + "desktop.ini"
      // 创建一个File对象，表示desktop.ini文件
      val desktopIniFile = new File(desktopIniPath)
      // 判断文件是否存在
      if (desktopIniFile.exists) {
        // 如果文件存在，就删除文件
        setFileFullControl(desktopIniPath)
        desktopIniFile.delete()
      }
      // 创建一个新的desktop.ini文件
      desktopIniFile.createNewFile()
      // 定义一个desktop.ini文件的内容，使用插值字符串，传入参数
      val desktopIniContent = desktopIni(bgPic, iconPath, info, confirm)


      // 将desktop.ini文件的内容写入文件，使用scala.io.Source类，指定编码为Unicode
      scala.io.Source.fromString(desktopIniContent)
        .getLines
        .foreach(line => ScalaFile(desktopIniPath).appendAll(line + "\r\n"))
      // 设置desktop.ini文件的属性为隐藏和系统
      setFileHiddenAndSystem(desktopIniPath)
    } catch {
      // 捕获异常，打印异常信息
      case e: Exception => e.printStackTrace()
    }
  }

  override def appKey: String = "sito"

  override def execute(params: String*): Unit = setDesktopIni(params(0), params(1))
}
