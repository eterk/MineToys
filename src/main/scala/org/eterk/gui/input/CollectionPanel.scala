package org.eterk.gui.input

import org.eterk.gui.ComponentWrapper
import org.eterk.gui.input.CollectionPanel.CellTheme
import org.eterk.util.Util

import java.awt.image.RGBImageFilter
import java.awt.{Color, Dimension}
import javax.swing.BorderFactory
import javax.swing.border.Border
//import javax.swing.border.Border
import scala.swing.{BorderPanel, BoxPanel, Orientation}


object CollectionPanel {

  /**
   *
   * @param padding    内边距
   * @param borderSize 边框大小
   * @param borderType 边框样式，虚线，实现
   */
  case class CellTheme(padding: Dimension,
                       borderSize: Dimension,
                       cellColors: Seq[Color],
                       borderType: BorderType.Value)

  implicit val theme: CellTheme = CellTheme(new Dimension(20, 20), new Dimension(3, 3), new Color(220, 220, 220) :: Nil, BorderType.DASHED)


  def apply(cells: Seq[ComponentWrapper], cellSize: Dimension)(implicit theme: CellTheme): CollectionPanel = {

    require(cellSize != null)
    val cellSizeSeq: Seq[Dimension] = LazyList.fill(cells.size)(cellSize)
    new CollectionPanel(cells, Orientation.Horizontal, theme) {
      override val cellSize: Seq[Dimension] = cellSizeSeq
    }.render()
  }


  def apply(cells: Seq[ComponentWrapper],
            dimension: Dimension,
            orientation: Orientation.Value): CollectionPanel = {
    apply(cells, LazyList.fill(cells.size)(1), dimension, orientation)
  }

  private def getDimensionFunc(dimension: Dimension, orientation: Orientation.Value): Double => Dimension = {
    import dimension._
    orientation match {
      case Orientation.Horizontal =>
        (r: Double) => new Dimension((r * width).toInt, height)


      case Orientation.Vertical =>
        (r: Double) => new Dimension(width, (r * height).toInt)
    }
  }

  def getDimension(weight: Seq[Int], dimension: Dimension,
                   orientation: Orientation.Value): Seq[Dimension] = {
    val total: Double = weight.sum.toDouble
    val getDimension = getDimensionFunc(dimension, orientation)

    weight
      .map(i => {
        require(i > 0)
        getDimension(i / total)
      })
  }


  def apply(cells: Seq[ComponentWrapper],
            cellSizeSeq: Seq[Dimension],
            orientation: Orientation.Value)(implicit theme: CellTheme): CollectionPanel = {

    new CollectionPanel(cells, orientation, theme) {
      override val cellSize: Seq[Dimension] = cellSizeSeq
    }.render()

  }

  def apply(cells: Seq[ComponentWrapper],
            weight: Seq[Int],
            dimension: Dimension,
            orientation: Orientation.Value)(implicit theme: CellTheme): CollectionPanel = {
    require(cells.size == weight.size)


    new CollectionPanel(cells, orientation, theme) {
      override val cellSize: Seq[Dimension] = getDimension(weight, dimension, orientation)
    }.render()

  }


  object BorderType {
    type Value = String
    val EMPTY = "empty"
    val DASHED = "dashed"
    val LINE = "line"
    val BLINK_DASHED = "blink_dashed"
    val BLINK_LINE = "blink_line"
  }

}

/**
 * 可选功能： 边框闪烁 。透明度，纵向排列的 每个cell 横边必须一致， 横线排列的纵边大小一致
 *
 * @param cells       内部的组件
 * @param orientation cell 排列方式
 */
// 定义一个集合面板的类，继承自 BoxPanel
abstract class CollectionPanel(val cells: Seq[ComponentWrapper],
                               val orientation: Orientation.Value,
                               val theme: CellTheme) extends BoxPanel(orientation) {

  import theme._

  //  每一个cell 的大小
  val cellSize: Seq[Dimension]

  def render(): CollectionPanel = {
    // 遍历传入的组件序列，为每个组件创建一个单元格
    for ((cell, index) <- cells.map(_.component).zipWithIndex) {
      // 创建一个单元格，继承自 BorderPanel
      val cellPanel: BorderPanel = new BorderPanel {
        // 设置单元格的大小
        preferredSize = cellSize(index)
        // 设置单元格的背景颜色，根据 cellColors 的长度进行循环取值
        background = cellColors(index % cellColors.length)

        // 将组件放置在单元格的中间
        layout(cell) = BorderPanel.Position.Center
      }
      import CollectionPanel.BorderType
      // 创建一个边框，根据 borderType 的值选择实线或者虚线
      val border: Border = borderType match {
        case BorderType.EMPTY => BorderFactory.createEmptyBorder() // 无边框
        case BorderType.LINE => BorderFactory.createLineBorder(new Color(0, 255, 0, 0.5f), borderSize.width) // 实线边框
        case BorderType.DASHED => BorderFactory.createDashedBorder(new Color(0, 180, 0), borderSize.width, borderSize.height, 0, true) // 虚线边框
        case BorderType.BLINK_LINE => new BlinkBorder(Color.black, borderSize.width, borderSize.height, 100, (0.0f, 1.0f)) // 实线闪烁边框
        case BorderType.BLINK_DASHED => new BlinkBorder(Color.black, 5, 5, 500, (0.0f, 1.0f))
        case _ => throw new IllegalArgumentException("Invalid border type") // 无效的边框类型
      }

      // 设置单元格的边框
      cellPanel.border = border

      // 将单元格添加到集合面板中
      contents += cellPanel
    }

    this
  }

  // 设置集合面板的内边距
  border = BorderFactory.createEmptyBorder(padding.height, padding.width, padding.height, padding.width)
}


// 可以让row panel 闪烁
//  import javax.swing.Timer
//  import java.awt.event.ActionListener
//  import java.awt.AlphaComposite
//
//  // 定义一个变量，用于存储当前的透明度
//  private var alpha: Float = 1.0f
//
//  // 定义一个变量，用于存储当前的边框颜色
//  private var borderColor: Color = Color.black
//
//  // 定义一个变量，用于存储当前的边框闪烁的方向
//  private var blinkDirection: Int = 1
//
//  // 定义一个定时器，用于控制边框闪烁的频率
//  private val timer: Timer = new Timer(500, new ActionListener {
//    override def actionPerformed(e: java.awt.event.ActionEvent): Unit = {
//      // 如果边框闪烁的方向是正向
//      if (blinkDirection == 1) {
//        // 透明度增加 0.1
//        alpha += 0.1f
//        // 如果透明度达到 1.0
//        if (alpha >= 1.0f) {
//          // 透明度设为 1.0
//          alpha = 1.0f
//          // 边框闪烁的方向设为反向
//          blinkDirection = -1
//        }
//      } else {
//        // 如果边框闪烁的方向是反向
//        // 透明度减少 0.1
//        alpha -= 0.1f
//        // 如果透明度达到 0.0
//        if (alpha <= 0.0f) {
//          // 透明度设为 0.0
//          alpha = 0.0f
//          // 边框闪烁的方向设为正向
//          blinkDirection = 1
//        }
//      }
//      // 根据透明度，设置边框颜色
//      borderColor = new Color(0, 0, 0, alpha)
//      // 重新绘制集合面板
//      repaint()
//    }
//  })
//
//  // 启动定时器
//  timer.start()
//
//  // 重写 paintComponent 方法，用于绘制集合面板
//  override def paintComponent(g: Graphics2D): Unit = {
//    // 调用父类的 paintComponent 方法
//    super.paintComponent(g)
//    // 设置绘制模式为透明
//    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha))
//    // 设置绘制颜色为边框颜色
//    g.setColor(borderColor)
//    // 遍历集合面板的内容，为每个单元格绘制边框
//    for (cell <- contents) {
//      // 获取单元格的位置和大小
//      val x = cell.location.x
//      val y = cell.location.y
//      val w = cell.size.width
//      val h = cell.size.height
//      // 根据边框大小，绘制四条边
//      g.fillRect(x, y, w, borderSize.height) // 上边
//      g.fillRect(x, y + h - borderSize.height, w, borderSize.height) // 下边
//      g.fillRect(x, y, borderSize.width, h) // 左边
//      g.fillRect(x + w - borderSize.width, y, borderSize.width, h) // 右边
//    }
//  }