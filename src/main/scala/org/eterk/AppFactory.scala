package org.eterk

import com.osinka.i18n.Messages
import org.eterk.util.Config


object AppFactory {

  import org.eterk.app._

  def appGroup: Map[String, Seq[App]] = {
    Map(
      "audio" -> audioGroup,
      "icon" -> iconGroup,
      "test" -> testGroup
    )
  }

  private var activeGroup_ = "test"

  def setActiveGroup(group: String): Unit = {
    import org.eterk.util.LanguageSetting._
    Config.msg(Messages("group.active.config"))
    require(appGroup.keySet.contains(group), s"$group not in available group ${appGroup.keySet.toSeq.sorted.mkString(",")}")
    this.activeGroup_ = group
  }

  def activeGroup: String = activeGroup_

  private def audioGroup: Seq[App] = Seq(ExportWav, SplitWav, WavToText)

  private def iconGroup: Seq[App] = Seq(DominantColor, GradientIcon, SingleColorIcon, ImageConvert, DesktopInI)

  private def testGroup: Seq[App] = HelloApp :: Nil

  def availableApp: Seq[App] = appGroup(activeGroup)

}
