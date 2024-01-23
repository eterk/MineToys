package org.eterk.app

import org.eterk.AppFactory

object HelloApp extends App {

  override def appKey: String = "test"

  override def appName: String = "Hello"

  override def paramSeq: Seq[String] = Seq("name")


  override def appDescription: String = "A simple app that prints hello to the given name"


  override def execute(params: String*): Unit = {
    val context =
      params match {
        case Nil => "World"
        case seq => seq.mkString(" ")
      }
    println(s"Hello, $context!")
  }


}
