package org.eterk.app


import com.osinka.i18n.{Lang, Messages}

import scala.annotation.varargs

trait App {

  import org.eterk.util.LanguageSetting.lang

  implicit val langeImp: Lang = lang

  /**
   * a unique key to start up App,as simple,short as possible;
   */
  def appKey: String


  def appName: String = Messages(s"${appKey}.name")

  def paramSeq: Seq[String] = Messages(s"${appKey}.params").split(",")

  /**
   * a  detailed about app's function and target
   */
  def appDescription: String = Messages(s"${appKey}.desc")

  @varargs
  def execute(params: String*): Unit

}
