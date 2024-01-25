package org.eterk.app

import com.osinka.i18n.Messages
import org.eterk.AppFactory

object HelloApp extends App {

  import org.eterk.util.LanguageSetting._

  override def appKey: String = "hello"


  override def execute(params: String*): Unit = {
    val context =
      params match {
        case Nil => Messages("hello.default")
        case seq => seq.mkString(" ")
      }
    println(s"${Messages("hello.hello")} $context!")
  }


}
