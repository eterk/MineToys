package org.eterk.app


import scala.annotation.varargs

trait App {


  /**
   * a unique key to start up App,as simple,short as possible;
   */
  def appKey: String

  def appName: String

  def paramSeq: Seq[String]

  /**
   * a  detailed about app's function and target
   */
  def appDescription: String

  @varargs
  def execute(params: String*): Unit

}