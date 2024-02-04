package org.eterk

import com.osinka.i18n.Messages
import org.eterk.util.{Config, Logger}


object AppFactory extends Logger {

  import org.eterk.app._

  def appGroup: Map[String, Seq[App]] = {
    Map(
      "audio" -> audioGroup,
      "icon" -> iconGroup,
      "test" -> testGroup
    )
  }

  case class AppGUI(app_key: String, params_type: Seq[String])

  def getParamJson() = {
    //    import org.json4s._
    //    import org.json4s.native.JsonMethods._
    //    import org.json4s.native.Serialization
    //    import org.json4s.native.Serialization.{read, write}
    //    implicit val formats: Formats = Serialization.formats(NoTypeHints)

    val appGUIs = Seq.empty[AppGUI]

    //    val json: String = write(appGUIs)
    //    val appGUIs: Seq[AppGUI] = read[Seq[AppGUI]](json)
  }

  private var activeGroup_ = "test"

  def setActiveGroup(group: String): Unit = {
    import org.eterk.util.LanguageSetting._
    msg(Messages("group.active.config"))
    require(appGroup.keySet.contains(group), s"$group not in available group ${appGroup.keySet.toSeq.sorted.mkString(",")}")
    this.activeGroup_ = group
  }

  def activeGroup: String = activeGroup_

  private def audioGroup: Seq[App] = Seq(ExportWav, SplitWav, WavToText, ExtractWord)

  private def iconGroup: Seq[App] = Seq(DominantColor, GradientIcon, SingleColorIcon, ImageConvert, DesktopInI)

  private def testGroup: Seq[App] = HelloApp :: Nil

  def availableApp: Seq[App] = appGroup(activeGroup)

}
