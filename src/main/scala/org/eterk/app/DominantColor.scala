package org.eterk.app

import org.eterk.util.Util

import java.awt.Color

object DominantColor extends App {


  override def appKey: String = "edc"


  override def execute(params: String*): Unit = {


    val colorMap = Util.getDominantColorMap(params(0))

    val formatFunc: Map[String, Seq[Color]] => String =
      params(1) match {
        case "code" => scalaCode
        case "json" => mapToJson
      }

    val outputFunc: String => Unit =
      params(2) match {
        case "print" => println
        case path => write(path, _)
      }


    formatFunc.andThen(outputFunc).apply(colorMap)

  }

  def write(path: String, content: String): Unit = {
    scala.io.Source.fromString(content)
      .getLines
      .foreach(line => scala.tools.nsc.io.File(path).appendAll(line + "\n"))
  }


  def scalaCode(map: Map[String, Seq[Color]]): String = {
    map
      .zipWithIndex
      .flatMap {
        case ((name, color), i) =>
          val c = color
            .map(colorToHex)
            .mkString("Seq(\"", "\",\"", "\")")

          Seq(s"//$name", s"val c$i=$c")
      }.mkString("\n")
  }

  // 导入 java.awt.Color 类
  //  import java.awt.Color
  //
  //  // 定义一个简单的 Map[String, Seq[Color]] 结构
  //  val map = Map(
  //    "red" -> Seq(new Color(0xFF0000), new Color(0xFF6347)),
  //    "green" -> Seq(new Color(0x00FF00), new Color(0x90EE90)),
  //    "blue" -> Seq(new Color(0x0000FF), new Color(0x87CEEB))
  //  )

  // 定义一个函数，将 Color 对象转换为六位十六进制字符串
  def colorToHex(color: Color): String = {
    // 获取颜色的 RGB 值
    val rgb = color.getRGB()
    // 将 RGB 值转换为十六进制字符串，并去掉前两位
    val hex = Integer.toHexString(rgb).substring(2)
    // 返回结果
    hex
  }

  // 定义一个函数，将 Seq[Color] 转换为 json 数组字符串
  def seqToJson(seq: Seq[Color]): String = {
    // 使用 map 函数，将每个 Color 对象转换为六位十六进制字符串
    val hexSeq = seq.map(colorToHex)
    // 使用 mkString 函数，将字符串序列拼接为 json 数组字符串
    val jsonArray = hexSeq.mkString("[\"", "\", \"", "\"]")
    // 返回结果
    jsonArray
  }

  // 定义一个函数，将 Map[String, Seq[Color]] 转换为 json 对象字符串
  def mapToJson(map: Map[String, Seq[Color]]): String = {
    // 使用 map 函数，将每个键值对转换为 json 属性字符串
    val jsonProps = map.map { case (key, value) =>
      // 使用双引号括起键名
      val jsonKey = "\"" + key + "\""
      // 使用 seqToJson 函数，将值转换为 json 数组字符串
      val jsonValue = seqToJson(value)
      // 使用冒号分隔键和值
      val jsonProp = jsonKey + ": " + jsonValue
      // 返回结果
      jsonProp
    }
    // 使用 mkString 函数，将字符串序列拼接为 json 对象字符串
    val jsonObject = jsonProps.mkString("{\n", ",\n ", "\n}")
    // 返回结果
    jsonObject
  }

  // 定义一个函数，将六位十六进制字符串转换为 Color 对象
  def hexToColor(hex: String): Color = {
    // 使用 Integer.parseInt 函数，将十六进制字符串转换为整数
    val rgb = Integer.parseInt(hex, 16)
    // 使用 Color 构造函数，创建一个 Color 对象
    val color = new Color(rgb)
    // 返回结果
    color
  }


  // 定义一个函数，将 json 对象字符串转换为 Map[String, Seq[Color]]
  def jsonToMap(json: String): Map[String, Seq[Color]] = {
    json
      .split("\n")
      .toSeq
      .collect {
        case kv if kv.contains(":") =>
          val x = kv.split(":")
          val key = x(0)
            .replaceAll("\"", "")
            .replaceAll(" ", "")
          val value = x(1)
            .replaceAll("\\[", "")
            .replaceAll("]", "")
            .replaceAll(" ", "")
            .replaceAll("\"", "")
            .split(",")
            .map(hexToColor)
            .toSeq
          (key, value)
      }.toMap

  }


}
