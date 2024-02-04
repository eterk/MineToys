package org.eterk.script

import java.io._
import scala.io._
import scala.util.matching.Regex

object Scrapy {


  // 定义一个函数，用于获取一个字符串中所有包含 <br /> 的行，最后拼接成一个字符串
  def getLinesWithBr(str: String): String = {
    // 使用 split 方法，按照换行符将字符串分割为一个数组
    val br = "<br />"
    val lineBreaks = "\n"
    str
      .split(lineBreaks)
      .collect {
        case s if s.contains(br) => s.replaceAll(br, "").replaceAll(" ", "").trim
      }
      .mkString(lineBreaks)
  }


  // 定义一个函数，用于从网页中提取正文内容和标题
  def getContentAndTitle(url: String): (String, String) = {
    // 从网页中读取 HTML 源码
    val html = Source.fromURL(url, "gbk").mkString

    val content = getLinesWithBr(html)

    val titleRegex = new Regex("<td class=\"t50\">(.*?)</td></tr>")

    val title = titleRegex.findFirstMatchIn(html).map(_.group(1)).getOrElse("")

    (content, title)
  }


  // 定义一个函数，用于将正文内容保存为 txt 文件
  def saveAsTxt(content: String, title: String, path: String): String = {

    val output =
      if (path.endsWith("/")) {
        path + title
      } else {
        path + "/" + title
      }


    // 创建一个文件对象，用于写入数据
    val file = new File(output)
    // 创建一个打印流对象，用于输出数据
    val pw = new PrintWriter(file)
    // 将正文内容写入文件
    pw.write(content)
    // 关闭打印流对象
    pw.close()
    output
  }

  def main(args: Array[String]): Unit = {

    val url = (s: String) => s"https://www.xuges.com/ls/jinshu9/${s}.htm"
    val path = "S:/lib/晋书/"

    (1 to 138)
      .map(i => i.toString.reverse.padTo(3, "0").mkString("").reverse)
      .foreach {
        index =>
          Thread.sleep(1000)
          // 从当前的网页中提取正文内容和标题
          val (content, title) = getContentAndTitle(url(index))

          // 将正文内容保存为 txt 文件
          saveAsTxt(content, index + "_" + title, path)
      }

  }


}
