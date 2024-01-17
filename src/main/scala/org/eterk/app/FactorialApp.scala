package org.eterk.app

object FactorialApp extends App {
  // 应用程序的名称
  def appName: String = "Factorial"

  // 应用程序的参数序列
  def paramSeq: Seq[String] = Seq("n")

  // 应用程序的参数描述
  def paramDescription: Seq[String] = Seq("the number to calculate its factorial")

  // 应用程序的功能说明
  def appDescription: String = "A simple app that calculates the factorial of the given number"

  // 应用程序的执行逻辑
  def execute(params: String*): Unit = {
    val n = params.headOption.map(_.toInt).getOrElse(0)
    val f = (1 to n).product
    println(s"$n! = $f")
  }

}
