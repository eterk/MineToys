package org.eterk.gui.input

import java.awt.{Color, Graphics, Insets}
import javax.swing.border.Border
import scala.swing.Graphics2D


// 定义一个闪烁边框的类，继承自 Border
class BlinkBorder(color: Color, width: Int, height: Int, speed: Int, alphaRange: (Float, Float)) extends Border {
  // 导入一些需要的包和类

  import javax.swing.Timer
  import java.awt.event.ActionListener
  import java.awt.AlphaComposite

  // 定义一个变量，用于存储当前的透明度
  private var alpha: Float = alphaRange._1

  // 定义一个变量，用于存储当前的边框闪烁的方向
  private var blinkDirection: Int = 1

  // 定义一个定时器，用于控制边框闪烁的频率
  private val timer: Timer = new Timer(speed, new ActionListener {
    override def actionPerformed(e: java.awt.event.ActionEvent): Unit = {
      // 如果边框闪烁的方向是正向
      if (blinkDirection == 1) {
        // 透明度增加 0.1
        alpha += 0.1f
        // 如果透明度达到上限
        if (alpha >= alphaRange._2) {
          // 透明度设为上限
          alpha = alphaRange._2
          // 边框闪烁的方向设为反向
          blinkDirection = -1
        }
      } else {
        // 如果边框闪烁的方向是反向
        // 透明度减少 0.1
        alpha -= 0.1f
        // 如果透明度达到下限
        if (alpha <= alphaRange._1) {
          // 透明度设为下限
          alpha = alphaRange._1
          // 边框闪烁的方向设为正向
          blinkDirection = 1
        }
      }
    }
  })

  // 启动定时器
  timer.start()

  // 重写 getBorderInsets 方法，返回边框的大小
  override def getBorderInsets(c: java.awt.Component): Insets = {
    new Insets(height, width, height, width)

  }

  // 重写 isBorderOpaque 方法，返回 false，表示边框不是不透明的
  override def isBorderOpaque: Boolean = false

  // 重写 paintBorder 方法，用于绘制边框
  override def paintBorder(c: java.awt.Component, g: Graphics, x: Int, y: Int, w: Int, h: Int): Unit = {
    // 将 Graphics 对象转换为 Graphics2D 对象
    val g2d = g.asInstanceOf[Graphics2D]
    // 设置绘制模式为透明
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha))
    // 设置绘制颜色为边框颜色
    g2d.setColor(color)
    // 根据边框大小，绘制四条边
    g2d.fillRect(x, y, w, height) // 上边
    g2d.fillRect(x, y + h - height, w, height) // 下边
    g2d.fillRect(x, y, width, h) // 左边
    g2d.fillRect(x + w - width, y, width, h) // 右边
  }
}