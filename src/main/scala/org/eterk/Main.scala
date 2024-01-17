package org.eterk

import app.App

// 定义一个 case class 来表示命令行配置
case class Param(list: Option[Boolean],
                 execute: Option[String] = None,
                 help: Option[Boolean])


// 定义一个 scopt 的 OParser，来解析命令行参数
object Main {


  // 定义一个方法，根据应用程序的名称，找到对应的应用程序实例
  def findApp(name: String): Option[App] = {
    AppFactory.availableApp.find(_.appName == name)
  }

  // 定义一个方法，根据类别，打印出相应的应用程序的信息
  def listApps(): Unit = {
    AppFactory.availableApp
      .zipWithIndex
      .foreach {
        case (app, index) =>
          println(s"$index . ${app.appName} : ${app.appDescription}")
      }
  }

  def help(name: String) = {
    findApp(name) match {
      case Some(app) =>
        println(app)
        println(app.appDescription)
        println(app.help())
      case None =>
        println(s"No such app: $name,available name ${AppFactory.availableApp}")
    }
  }

  // 定义一个方法，根据应用程序的名称，执行对应的应用程序
  def executeApp(name: String, args: Array[String]): Unit = {
    findApp(name) match {
      case Some(app) =>
        println(s"Executing app: $name")
        app.execute(args: _*)
      case None =>
        println(s"No such app: $name")
    }
  }

  import scopt._

  // 定义一个case class来存储命令行参数
  case class Config(help: Option[String] = None, // 显示指定App帮助信息
                    list: Boolean = false, // 是否执行list()方法
                    exe: Option[Array[String]] = None // 是否执行executeApp(arg:Array[String])方法
                   )

  implicit val arrayRead: Read[Array[String]] = Read.reads(_.split(","))

  // 创建一个OptionParser实例
  val parser: OptionParser[Config] = new OptionParser[Config]("") {
    // 定义--help或-h选项
    opt[String]("help").abbr("h").action((x, c) => c.copy(help = Some(x))).text("显示帮助信息")
    // 定义--list或-l选项
    opt[Unit]("list").abbr("l").action((_, c) => c.copy(list = true)).text("执行list()方法")
    // 定义--exe或-e选项
    opt[Array[String]]("exe").abbr("e")
      .valueName("<args>")
      .action((x, c) => c.copy(exe = Some(x))).text("执行executeApp(arg:Array[String])方法")
  }


  // 在 main 方法中，解析命令行参数，并根据解析结果执行相应的逻辑
  def main(args: Array[String]): Unit = {

    //    val picDir = "S:\\util\\pic\\ao"
    //    val iconDir = "S://util/icon"
    //    val colNums = Seq(2)
    //    val degrees = Seq(45)

    //    listApps()


    // 解析命令行参数
    parser.parse(args, Config()) match {
      case Some(config) =>

        config.help.foreach(appName => help(appName))
        if (config.list) {
          listApps()
        }
        config.exe.foreach(args => {
          val args = config.exe.get
          executeApp(args.head, args.tail)
        })

      case None =>
        // 如果解析失败，显示错误信息
        listApps()
        println("无效的参数，请使用--help或-h选项查看帮助信息")
    }
    // 打印结果
    //    println(objectsInPackage)
    //    OParser.parse(parser, args, Config()) match {
    //      case Some(config) =>
    //        config.list.foreach(listApps)
    //        config.execute.foreach(executeApp(_, args.drop(2)))
    //      case _ =>
    //        println("Invalid arguments")
    //    }

    //    val audio_in =
    //    val output_dir = "/home/data/output"
    //    WavToText.execute("/home/data/a.wav", "/home/data/output")
    //    ExportWav.execute("S:\\lib\\video")
    //    WavToText.execute("S:\\lib\\video")
    //    WavToText.execute("S:\\util\\icon\\disk_c", "S:\\util\\icon\\disk_c")

    //        Util
    //          .filterFiles("S:\\lib\\video",p=>p.endsWith(".txt"),recursive = false)

    //    Util
    //      .filterFiles("S:\\util\\icon\\disk_c",p=>p.endsWith(".svg"),recursive = false)
    //      .foreach(p=>{
    //        ImageToIco.execute(p,Util.repalceFileFomat(p,"ico"))
    //      })


  }
}
