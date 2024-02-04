package org.eterk.gui


import org.eterk.app.App
import org.eterk.gui.input.CollectionPanel.{BorderType, CellTheme}
import org.eterk.gui.input.{CollectionPanel, EMPTYInputPanel, InputPanel}
import org.eterk.util.Logger

import java.awt.{Color, Dimension}
import scala.swing.{BoxPanel, _}
import scala.swing.event._
import scala.util.Try
import scala.util.control.NonFatal


object AppRowPanel extends Logger {

  def apply(app: App, index: Int, maxParamLength: Int): CollectionPanel = {
    val paramNameTypeSeq: Seq[(String, String)] = app.paramSeq.zip(app.paramTypeSeq)
    val weight = Seq(6, 13, 1)
    val dim = new Dimension(2100, 100)
    val orientation = Orientation.Horizontal
    val cellDimOut = CollectionPanel.getDimension(weight, dim, orientation)
    val cellDimeIn = CollectionPanel.getDimension(Seq.fill(maxParamLength)(1), cellDimOut(1), orientation)


    val first =
      new ComponentWrapper {
        override def component: Component = {

          new BoxPanel(Orientation.Vertical) {
            val nameLabel: Label = new Label {
              text = s"${index + 1}. ${app.appName}"
              horizontalAlignment = Alignment.Left
            }
            nameLabel.font = GuiTheme.FontConfig.h1

            //创建一个appDescription标签，显示app的description，设置字体为普通，大小为12
            val descLabel = new Label(app.appDescription)
            descLabel.font = GuiTheme.FontConfig.h2

            //将appName标签和appDescription标签添加到描述面板中
            contents += nameLabel
            contents += descLabel
          }

          //创建一个appName标签，显示app的name和index，设置字体为粗体，大小为16


        }
      }
    val second = new ComponentWrapper {
      override def component: Component = {


        val paramNameTypeSeq: Seq[(String, String)] = app.paramSeq.zip(app.paramTypeSeq)


        val cells: Seq[ComponentWrapper] = {
          paramNameTypeSeq
            .map {
              case (label, tpe) =>
                val paramRow = InputPanel(label, tpe)

                paramRow
            }
            .padTo(maxParamLength, EMPTYInputPanel)
        }
        CollectionPanel(cells, cellDimeIn, orientation)

      }

    }
    val third = new ComponentWrapper {
      override def component: Component = new BoxPanel(Orientation.Vertical) {
        xLayoutAlignment = peer.getAlignmentX / 2
        yLayoutAlignment = 200
        //创建一个执行按钮，用来提交任务
        val executeButton = new Button("Execute")

        //为执行按钮添加点击事件的监听器
        executeButton.reactions += {
          case ButtonClicked(_) =>
            //获取参数面板中的所有输入框
            val params: Seq[String] = second.component
              .asInstanceOf[CollectionPanel]
              .cells
              .zipWithIndex
              .map {
                case (i: InputPanel[_], index) =>
                  Try(i.result.toString)
                    .recover {
                      case NonFatal(e) =>
                        msg(s"Input ${paramNameTypeSeq(index)} is Empty.")
                        ""
                    }.get

              }

            //获取输入框中的所有参数值
            msg(params.mkString(","))

            //调用app的execute方法，传入参数值
            app.execute(params: _*)

            //显示一个对话框，提示任务已提交
            Dialog.showMessage(null, s"Task submitted for ${app.appName} ${params.mkString(",")}", "Message")
        }

        //将执行按钮和清空按钮添加到操作面板中
        contents += executeButton

      }
    }

    val cells = Seq(first, second, third)


    CollectionPanel(cells, cellDimOut, orientation)(CellTheme(new Dimension(50, 50), new Dimension(3, 3), new Color(220, 220, 220) :: Nil, BorderType.EMPTY))

  }


}