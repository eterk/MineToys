package org.eterk

import app.App
import com.osinka.i18n.{Lang, Messages}
import org.eterk.util.{Config, LanguageSetting, Logger}

import scala.util.{Failure, Success, Try}


// 定义一个 scopt 的 OParser，来解析命令行参数
object Main extends Logger {

  import org.eterk.util.Theme._
  import LanguageSetting._

  // 定义一个方法，根据应用程序的名称，找到对应的应用程序实例
  private def findApp(name: String): Option[App] = {
    AppFactory.availableApp.find(_.appKey == name)
  }

  private def listApp(app: Seq[App]): Unit = {
    app
      .zipWithIndex
      .foreach {
        case (app, index) =>
          import org.eterk.util.ColorString._

          val i = index.toString
          val key = app.appKey.padTo(10, ' ').appKey
          val desc = app.appDescription
          val param = app.paramSeq.magentaYellow(";")

          msg(s"  $i.$key:" + param)
          msg("  ".padTo(12, ' ') + desc)

      }
  }


  // 定义一个方法，根据类别，打印出相应的应用程序的信息
  private def listApps(group: String): Unit = {
    val appGroups =
      group match {
        case "all" => AppFactory.appGroup.keySet.toSeq.sorted
        case "active" => AppFactory.activeGroup :: Nil
        case other => other :: Nil
      }

    appGroups.foreach {
      group =>
        msg(group.group)
        if (!AppFactory.appGroup.isDefinedAt(group)) {
          msg(s"unknown ${group.group}")
        } else {
          listApp(AppFactory.appGroup(group))
        }
    }

  }


  def help(name: String): Unit = {
    findApp(name) match {
      case Some(app) =>
        msg("")
        val head = s"【${app.appKey.appKey}】.${app.appName}"
        val param = app.paramSeq.zipWithIndex.map {
          case (k, i) => (i + 1) + "." + k
        }.mkString("param:", ",", "")
        val info =
          s"""
             |${head}
             |${param}
             |${app.appDescription}
             |""".stripMargin
        msg(info)
      case None =>
        msg(s"No such app: ${name.appKey} in ${Config.group.group},available name: ${AppFactory.availableApp.map(_.appKey.appKey).mkString(",")}")

    }
  }

  // 定义一个方法，根据应用程序的名称，执行对应的应用程序
  private def executeApp(name: String, args: Array[String]): Unit = {
    import org.eterk.util.ColorString._
    findApp(name) match {
      case Some(app) =>
        msg(s"${Messages("exe.param.app")}: ${name.appKey}")
        msg(s"${Messages("exe.param.param")}: ${args.toSeq.magentaYellow(";")}")
        if (args.length != app.paramSeq.length) {
          msg(s"${Messages("exe.params.length.error")}  need  ${app.paramSeq.length}, given ${args.length}")
          return
        }

        Try(app.execute(args: _*)) match {
          case Success(value) => Messages("exe.success")
          case Failure(exception) =>
            debugDo(() => exception.printStackTrace())
            help(name.appKey)
            Thread.sleep(2000)
        }

      case None =>
        msg(s"No such app: ${name.appKey} in app group [${Config.group.group}]")
    }
  }


  // 在 main 方法中，解析命令行参数，并根据解析结果执行相应的逻辑
  def main(args: Array[String]): Unit = {


    // 解析命令行参数
    Config.parser.parse(args, Config()) match {

      case Some(config) =>
        Try {
          LanguageSetting.setLang(Lang(config.language))

          AppFactory.setActiveGroup(config.group)


          config.help.foreach(appName => help(appName))

          config.list.foreach(listApps)

          config.exe.foreach(args => {
            executeApp(args.head, args.tail)
          })
        } match {
          case Success(s) =>
          case Failure(e) => msg(args.mkString(",") + " failed")

        }


      case None =>
        // 如果解析失败，显示错误信息
        msg(Messages("config.wrong.input"))
    }

  }
}
