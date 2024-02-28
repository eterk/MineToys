package org.eterk.gui


import io.github.eterk.elements.seqPanel
import io.github.eterk.mian.{ComponentWrapper, GuiTheme}
import io.github.eterk.mian.input.SeqPanel.{BorderType, CellTheme}
import io.github.eterk.mian.input.{EMPTYInputPanel, InputPanel, SeqPanel}


import org.eterk.app.TypedApp
import org.eterk.util.Logger

import java.awt.{Color, Dimension}
import scala.swing._
import scala.swing.event._
import scala.util.Try
import scala.util.control.NonFatal


object AppRowPanel extends Logger {


  private def titleCol(index: Int, appName: String, desc: String): ComponentWrapper =
    new ComponentWrapper {
      override def component: Component = {

        new BoxPanel(Orientation.Vertical) {
          val nameLabel: Label = new Label {
            text = s"${index + 1}. $appName"
            horizontalAlignment = Alignment.Left
          }
          nameLabel.font = GuiTheme.FontConfig.h1

          //创建一个appDescription标签，显示app的description，设置字体为普通，大小为12
          val descLabel = new Label(desc)
          descLabel.font = GuiTheme.FontConfig.h2

          //将appName标签和appDescription标签添加到描述面板中
          contents += nameLabel
          contents += descLabel
        }

        //创建一个appName标签，显示app的name和index，设置字体为粗体，大小为16


      }
    }

  private def paramCol(app: TypedApp[_],
                       maxParamLength: Int,
                       cellDimeIn: Seq[Dimension],
                       orientation: Orientation.Value): ComponentWrapper = new ComponentWrapper {
    val nameTypes: Seq[(String, String)] = app.paramSeq.zip(app.paramTypeSeq)

    override def component: Component = {

      val cells: Seq[InputPanel[_]] = {
        nameTypes
          .map {
            case (label, tpe) =>
              val paramRow = InputPanel(label, tpe)

              paramRow
          }
          .padTo(maxParamLength, EMPTYInputPanel)
      }

      val action = new ComponentWrapper {

        override def component: Component =
          //          new swing.Component {
          //          xLayoutAlignment = peer.getAlignmentX / 2
          //          yLayoutAlignment = 200
          //创建一个执行按钮，用来提交任务
          //          val executeButton =
          new Button("Execute") {

            reactions += {
              case ButtonClicked(_) =>
                //获取参数面板中的所有输入框
                val params: Seq[String] = cells
                  .take(nameTypes.size)
                  .zipWithIndex
                  .map {
                    case (i: InputPanel[_], index) =>
                      Try {
                        //                    println(index+":   "+i.getCounter)
                        val res = i.result.toString

                        println(i.result)

                        res

                      }
                        .recover {
                          case NonFatal(_) =>
                            ////                        e.printStackTrace()
                            msg(s"Input ${nameTypes(index)} is Empty.")
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

          }

      }

      seqPanel(cells :+ action, cellDimeIn, orientation)

    }

  }

  def apply(app: TypedApp[_], index: Int, maxParamLength: Int): SeqPanel = {


    val weight = Seq(3, 7)

    val dim = new Dimension(2100, 100)

    val orientation: Orientation.Value = Orientation.Horizontal

    val cellDimOut: Seq[Dimension] = SeqPanel.getDimension(weight, dim, orientation)

    val cellDimeIn: Seq[Dimension] = SeqPanel.getDimension(Seq.fill(maxParamLength + 1)(1), cellDimOut(1), orientation)

    val first = titleCol(index, app.appName, app.appDescription)
    val second = paramCol(app, maxParamLength, cellDimeIn, orientation)


    val theme = CellTheme(new Dimension(50, 50), new Dimension(3, 3), new Color(220, 220, 220) :: Nil, BorderType.EMPTY)

    SeqPanel(Seq(first, second), cellDimOut, orientation)(theme)

  }


}