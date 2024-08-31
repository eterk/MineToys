package org.eterk.gui

import com.osinka.i18n.Lang
import io.github.eterk.mian.ComponentWrapper
import io.github.eterk.mian.input._
import org.apache.commons.imaging.Imaging
import org.eterk.{AppFactory, Resource}
import org.eterk.server.FunasrService
import org.eterk.util.{Config, LanguageSetting, Logger}

import java.awt.Dimension
import java.io.File
import scala.swing._


object MainPage extends Logger {
  private def bodyUpdate(s: String): Unit = {
    msg("update=>" + s)
    msg(Config.showInfo())
    BodyPanel.contents.clear()
    BodyPanel.context()
    BodyPanel.revalidate()
    BodyPanel.repaint()
  }


  def groupCall(g: String): Unit = {
    Config.group = g
    bodyUpdate(g)
  }

  def langCall(l: String): Unit = {
    LanguageSetting.setLang(Lang(l))
    bodyUpdate(l)
  }
}

object MainPanel extends BorderPanel {

  //将版本号标签放置在北边
  layout(HeadPanel) = BorderPanel.Position.North

  //将列表面板放置在中间
  layout(BodyPanel) = BorderPanel.Position.Center


}

object HeadPanel extends BoxPanel(Orientation.Horizontal) {

  //定义一个版本号标签
  private val versionLabel =
    new ComponentWrapper {
      override def component: Component = new Label {

        text = "Version 1.0"
        horizontalAlignment = Alignment.Center

      }
    }


  val group = RadioButtonInput("group", AppFactory.appGroup.keySet.toSeq, MainPage.groupCall)

  val language = RadioButtonInput("language", Seq("en", "zh"), MainPage.langCall)

  val debug= RadioButtonInput("debug", Seq("开启", "关闭"), x => Logger.setDebug(if (x == "开启") true else false))

  import io.github.eterk.elements.{wrapper, seqPanel}

  val funasr: ComponentWrapper = wrapper(StatusIndicator("Funasr", () => FunasrService.service.status(), 5000))


  val elements = seqPanel(Seq(group, language, debug, funasr, versionLabel), new Dimension(20, 40), Orientation.Horizontal)


  contents += elements

}

//定义一个列表面板，用来放置多个app面板
object BodyPanel extends BoxPanel(Orientation.Vertical) {

  def context(): Unit = {
    val apps = AppFactory.appGroup(Config.group)
    val maxParamLength: Int = apps.map(_.paramSeq.size).max


    //    val indexCells: Seq[Label] = (1 to maxParamLength).map(i => {
    //      val l = new Label(i.toString)
    //      l.font = Font("Arial", Font.Plain, 30)
    //      l
    //    })

    //遍历传入的app序列，为每个app创建一个面板
    for ((app, index) <- apps.zipWithIndex) {
      // 1600 宽度 100 每一列的高度
      val appPanel = AppRowPanel(app, index, maxParamLength)
      //       val appPanel: AppRowPanel = new AppRowPanel(index, maxParamLength, 2100, 100, app)

      //将app面板添加到列表面板中
      contents += appPanel
    }
  }

  context()


}

//定义一个GUI页面类，继承自SimpleSwingApplication
class MainPage() extends SimpleSwingApplication {

  import scala.swing._

  val frame: MainFrame = new MainFrame {
    val dimension = new Dimension(2300, 6 * 100 + 100)

    title = "Mine Toys"
    iconImage = Imaging.getBufferedImage(Resource.iconImage)
    contents = MainPanel
    maximumSize = dimension
    minimumSize = dimension

  }

  //重写top方法，返回一个主窗口，包含主面板
  override def top: Frame = {
    {
      // 设置窗口的位置为屏幕的中心点
      frame.peer.setLocation(1000, 1000)
      // 返回窗口对象
      frame
    }
  }

}




