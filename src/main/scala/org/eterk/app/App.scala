package org.eterk.app

import org.eterk.util.Util

trait App {
  def appName: String

  def paramSeq: Seq[String]

  def paramDescription: Seq[String]

  def appDescription: String

  def execute(params: String*): Unit

  // 应用程序的帮助文档
  def help(): String =
    s"""
       |Usage: $appName [n]
       |$appDescription
       |Options:
       |  n: ${paramDescription.head}
       |""".stripMargin

}