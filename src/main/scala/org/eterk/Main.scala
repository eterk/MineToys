package org.eterk

import app.App
import com.osinka.i18n.Lang
import org.eterk.util.LanguageSetting


// 定义一个 scopt 的 OParser，来解析命令行参数
object Main {


  //   在你的main方法或者其他地方，调用AnsiConsole.systemInstall方法


  // 定义一个方法，根据应用程序的名称，找到对应的应用程序实例
  private def findApp(name: String): Option[App] = {
    AppFactory.availableApp.find(_.appKey == name)
  }

  // 定义一个方法，根据类别，打印出相应的应用程序的信息
  private def listApps(): Unit = {
    AppFactory.availableApp
      .zipWithIndex
      .foreach {
        case (app, index) =>
          import org.eterk.util.ColorString._

          val i = index.toString
          val key = app.appKey.padTo(10, ' ').cyan
          val desc = app.appDescription
          val param = app.paramSeq.magentaYellow(",")
          //            mkString(",").yellow

          println(s"$i.$key:" + param)
          println("".padTo(12, ' ') + desc)

        //          println(s"$index .[${app.appKey}] ${app.appName} : ${app.appDescription} =>  ${app.paramSeq.mkString(",")}")

      }
  }

  def help(name: String): Unit = {
    findApp(name) match {
      case Some(app) =>
        println()
        val head = s"【${app.appKey}】.${app.appName}"
        val param = app.paramSeq.zipWithIndex.map {
          case (k, i) => (i + 1) + "." + k
        }.mkString("param:", ",", "")
        val info =
          s"""
             |${head}
             |${param}
             |${app.appDescription}
             |""".stripMargin
        println(info)
      case None =>
        println(s"No such app: $name,available name ${AppFactory.availableApp}")
    }
  }

  // 定义一个方法，根据应用程序的名称，执行对应的应用程序
  private def executeApp(name: String, args: Array[String]): Unit = {
    findApp(name) match {
      case Some(app) =>
        println(s"Executing app: $name")
        require(args.length == app.paramSeq.length, s"param length not match!  need  ${app.paramSeq.length}, given ${args.length}")
        app.execute(args: _*)
      case None =>
        println(s"No such app: $name")
    }
  }

  import scopt._

  // 定义一个case class来存储命令行参数
  case class Config(help: Option[String] = None, // 显示指定App帮助信息
                    language: String = "zh",
                    list: Boolean = false, // 是否执行list()方法
                    exe: Option[Array[String]] = None // 是否执行executeApp(arg:Array[String])方法
                   )

  implicit val arrayRead: Read[Array[String]] = Read.reads(_.split(","))

  // 创建一个OptionParser实例
  private val parser: OptionParser[Config] = new OptionParser[Config]("") {
    // 定义--help或-h选项
    opt[String]("help")
      .abbr("h")
      .action((x, c) => c.copy(help = Some(x)))
      .text("show help message")

    opt[String]("language")
      .abbr("lang")
      .action((x, c) => c.copy(language = x))
      .text("select language show")
    // 定义--list或-l选项
    opt[Unit]("list").abbr("l").action((_, c) => c.copy(list = true)).text("执行list()方法")
    // 定义--exe或-e选项
    opt[Seq[String]]("exe").abbr("e")
      .valueName("appName,arg1,arg2,arg3....")
      .action((x, c) => c.copy(exe = Some(x.toArray))).text("执行executeApp(arg:Array[String])方法")
  }


  // 在 main 方法中，解析命令行参数，并根据解析结果执行相应的逻辑
  def main(args: Array[String]): Unit = {

    // 解析命令行参数
    parser.parse(args, Config()) match {
      case Some(config) =>
        LanguageSetting.setLang(Lang(config.language))

        config.help.foreach(appName => help(appName))
        if (config.list) {
          listApps()
        }

        config.exe.foreach(args => {
          val args = config.exe.get
          println(args.length)
          println(args.mkString(","))
          executeApp(args.head, args.tail)
        })

      case None =>
        // 如果解析失败，显示错误信息
        listApps()
        println("无效的参数，请使用--help或-h选项查看帮助信息")
    }

  }
}
