package org.eterk.app


import com.osinka.i18n.Messages
import org.eterk.util.Logger

import scala.annotation.varargs

trait TypedApp[T] extends Logger {

  import org.eterk.util.LanguageSetting._


  /**
   * a unique key to start up App,as simple,short as possible;
   */
  def appKey: String


  def appName: String = Messages(s"${appKey}.name")

  def paramSeq: Seq[String] = Messages(s"${appKey}.params").split(",")

  def paramTypeSeq: Seq[String]

  /**
   * a  detailed about app's function and target
   */
  def appDescription: String = Messages(s"${appKey}.desc")

  @varargs
  def execute(params: String*): T

}