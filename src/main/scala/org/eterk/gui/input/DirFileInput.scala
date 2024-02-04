package org.eterk.gui.input


import org.eterk.gui.GuiTheme

import java.io.File
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileFilter
import scala.swing.{Button, Dimension, FileChooser, TextField}
import scala.swing.event.ButtonClicked
import scala.util.Try


object DirFileInput {

  // 假设您有一个变量 lastPath，用于存储上一次选择的路径
  var lastFile: File = new File("S://")


  def apply(typeName: String, label: String): DirFileInput = {


    val args = typeName.split(":")

    val mode =
      args(0) match {
        case "DIR_FILE" => FileChooser.SelectionMode.FilesAndDirectories
        case "FILE_DIR" => FileChooser.SelectionMode.FilesAndDirectories
        case "FILE" => FileChooser.SelectionMode.FilesOnly
        case "DIR" => FileChooser.SelectionMode.DirectoriesOnly
      }
    val sizeNum = GuiTheme.Dim.fileChooser
    val fileChooser: FileChooser = new FileChooser {
      // 设置文件选择模式为只能选择文件
      fileSelectionMode = mode


      maximumSize = sizeNum
      minimumSize = sizeNum
      preferredSize = sizeNum
      font = GuiTheme.FontConfig.h1
      if (args.size == 2) {
        val fileTypes = args(1).split(",")


        val available: String => Boolean = (s: String) => {
          fileTypes.map(s.endsWith).reduce(_ || _)
        }

        val ff =
          new FileFilter {
            // 定义一个方法，判断文件是否符合过滤条件
            override def accept(file: File): Boolean = {
              Try(org.eterk.util.Util.filterFiles(file.getAbsolutePath, available, recursive = false).nonEmpty)
                .recover(e => false)
                .get

            }

            override def getDescription: String = fileTypes.mkString(" , ")
          }

        fileFilter = ff

      }


    }


    DirFileInput(label, typeName, fileChooser, "浏览")

  }


}


case class DirFileInput(override val label: String,
                        override val typeName: String,
                        fileChooser: FileChooser,
                        browseButton: String) extends InputPanel[String] {


  // 创建一个文本框，显示选择的文件夹路径
  private val textField = new TextField {
    // 设置文本框的列数
    columns = 20

    // 设置文本框为不可编辑
    editable = false
  }

  // 创建一个按钮，用于打开资源管理器选择文件夹
  private val button = new Button {
    // 设置按钮的文本
    text = browseButton

    // 添加一个监听器，检测按钮的点击事件
    listenTo(this)

    // 定义一个反应器，处理按钮的点击事件
    reactions += {
      // 当按钮被点击时
      case ButtonClicked(_) =>

        // 显示文件选择器，获取用户的选择结果
        val result = fileChooser.showOpenDialog(null)

        // 如果用户选择了一个文件夹
        if (result == FileChooser.Result.Approve) {
          // 获取选择的文件夹
          val dir = fileChooser.selectedFile
          DirFileInput.lastFile = fileChooser.selectedFile

          // 获取文件夹的路径
          val path = dir.getAbsolutePath

          // 更新文本框的文本
          textField.text = path

          setResult(path)
        }
    }
  }
  // 将文本框和按钮添加到 GUI 组件中
  panel.contents += textField
  panel.contents += button


}
