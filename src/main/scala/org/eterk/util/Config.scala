package org.eterk.util

import com.osinka.i18n.{Lang, Messages}
import scopt._
import org.eterk.util.Theme._

object Config {

  import LanguageSetting._

  var group: String = "test"

  var debug: Boolean = false


  def showInfo(): String = {
    Seq(
      s"${Messages("config.env.debug")}: ${Config.debug}",
      s"${Messages("config.env.lang")}: ${LanguageSetting.lang.language}",
      s"${Messages("config.env.group")}: ${Config.group.group}").mkString(" ")
  }


  def msg(f: String): Unit = {
    if (debug) {
      println(f)
    }
  }

  def debugDo(f: () => Unit): Unit = {
    if (debug) {
      f()
    }
  }


  def apply(): Config = new Config(None, group, LanguageSetting.lang.language, debug, None, None)

  // 自定义的类型转换器
  implicit val seqStringConverter: scopt.Read[Seq[String]] =
    scopt.Read.reads { s =>
      // 按照；分割字符串
      s.split(";").toSeq
    }

  val parser: OptionParser[Config] = new OptionParser[Config]("") {

    //    head(s"${Messages("config.parser.start")}: " + Config.group.group)

    // 定义--help或-h选项
    opt[String]("help")
      .abbr("h")
      .valueName("appKey".appKey)
      .action((x, c) => c.copy(help = Some(x)))
      .text(s"${Messages("config.parser.help")}: " + s" --help:${"appKey".appKey} -h:${"appKey".appKey}")

    opt[String]("group")
      .abbr("g")
      .valueName("group".group)
      .action((x, c) => {
        Config.group = x
        c.copy(group = x)
      })
      .text(s"${Messages("config.parser.group")}: " + s" --group:${"test".group} -g:${"test".group}")

    opt[String]("language")
      .abbr("lang")
      .valueName("zh en")
      .action((x, c) => {
        LanguageSetting.setLang(Lang(x))
        c.copy(language = x)
      })
      .text(s"${Messages("config.parser.lang")}")

    // 定义--debug或-d选项
    opt[Boolean]("debug")
      .abbr("d")
      .valueName("0 ,1")
      .action((x, c) => {
        Config.debug = x
        c.copy(debug = x)
      })
      .text(s"${Messages("config.parser.debug")}:  --debug:0 --d:1")

    // 定义--list或-l选项
    opt[String]("list")
      .abbr("l")
      .valueName(s"all ,active, ${"group".group}")
      .action((x, c) => c.copy(list = Some(x)))
      .text(s"${Messages("config.parser.list")}:  --list:all -l:active -l:test")

    // 定义--exe或-e选项
    opt[Seq[String]]("exe")
      .abbr("e")
      .valueName("appName,args....")
      .action((x, c) => c.copy(exe = Some(x.toArray)))
      .text(s"${Messages("config.parser.exe")}:  --exe:hello;eterk")

  }

}


case class Config(help: Option[String], // 显示指定App帮助信息
                  group: String,
                  language: String,
                  debug: Boolean,
                  list: Option[String], // 是否执行list()方法
                  exe: Option[Array[String]] // 是否执行executeApp(arg:Array[String])方法
                 )
