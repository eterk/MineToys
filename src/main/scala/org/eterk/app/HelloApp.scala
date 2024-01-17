package org.eterk.app

import org.eterk.AppFactory

object HelloApp extends App {
  // 应用程序的名称
  def appName: String = "Hello"

  // 应用程序的参数序列
  def paramSeq: Seq[String] = Seq("name")

  // 应用程序的参数描述
  def paramDescription: Seq[String] = Seq("the name to greet")

  // 应用程序的功能说明
  def appDescription: String = "A simple app that prints hello to the given name"

  // 应用程序的执行逻辑
  def execute(params: String*): Unit = {
    val name = params.headOption.getOrElse("world")
    println(s"Hello, $name!")

  }


}
