package org.eterk.server

object MailMan {
  // 导入相关的库

  import scalaj.http._
  import java.nio.charset.StandardCharsets

  // 定义一个常量，表示您的 SCKEY，您需要在 server 酱的网站上获取
  val SCKEY = "SCT238690Tm6lG77fXUbFBbhlkZjVtI2Xo"

  // 定义一个发送消息的函数，接受两个参数：消息标题和消息内容
  def sendMsg(title: String, content: String): Unit = {
    // 定义一个常量，表示每个 SCKEY 每天能发送的最大消息数
    val MAX_MSGS = 5
    // 定义一个常量，表示每个消息的最大字节长度
    val MAX_BYTES = 256
    // 定义一个常量，表示每个消息的最大标题长度
    val MAX_TITLE = 64
    // 定义一个常量，表示 server 酱的 API 地址
    val API_URL = "https://sctapi.ftqq.com/"


    // 判断消息标题和内容是否为空，如果为空，直接返回
    if (title.isEmpty || content.isEmpty) {
      return
    }

    // 判断消息标题和内容的字节长度是否超过限制，如果超过，截断
    val titleBytes = title.getBytes(StandardCharsets.UTF_8)
    val contentBytes = content.getBytes(StandardCharsets.UTF_8)
    val titleCut = if (titleBytes.length > MAX_TITLE) {
      new String(titleBytes.slice(0, MAX_TITLE), StandardCharsets.UTF_8)
    } else {
      title
    }
    val contentCut = if (contentBytes.length > MAX_BYTES) {
      new String(contentBytes.slice(0, MAX_BYTES), StandardCharsets.UTF_8)
    } else {
      content
    }

    // 使用 scalaj.http 库发送一个 POST 请求到 server 酱的 API，传递消息标题和内容
    val response = Http(API_URL + SCKEY + ".send")
      .postForm(Seq("title" -> titleCut, "desp" -> contentCut))
      .asString

    // 判断响应的状态码是否为 200，如果是，表示发送成功，否则，表示发送失败
    if (response.code == 200) {
      println("Message sent successfully.")
    } else {
      println("Message failed to send.")
    }
  }


  // 导入相关的库

  import scala.sys.process._
  import scala.util.matching.Regex

  // 定义一个获取本机 ipconfig 中无线网的 ipv4 地址的函数，无参数，返回一个字符串
  def getWirelessIPv4(): String = {
    // 定义一个常量，表示无线网的名称，您可以根据您的实际情况修改
    val WIRELESS_NAME = "WLAN"
    // 定义一个常量，表示 ipv4 地址的正则表达式，用于匹配 ipconfig 的输出
    val IPV4_REGEX = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
    // 定义一个变量，表示是否找到无线网的标志，初始为 false
    var foundWireless = false
    // 定义一个变量，表示无线网的 ipv4 地址，初始为空字符串
    var wirelessIPv4 = ""
    // 使用 scala.sys.process 库的 !! 方法，执行 ipconfig 命令，获取本机的网络配置信息
    val ipconfig = "ipconfig".!!
    // 使用字符串的 linesIterator 方法，将网络配置信息按行遍历
    for (line <- ipconfig.linesIterator) {
      // 如果找到无线网的名称，将标志设为 true
      if (line.contains(WIRELESS_NAME)) {
        foundWireless = true
      }
      // 如果找到无线网的 ipv4 地址，使用正则表达式匹配，将地址赋值给变量，然后跳出循环
      if (foundWireless && line.contains("IPv4")) {
        return IPV4_REGEX.findFirstIn(line).getOrElse("")
      }
    }
    // 返回无线网的 ipv4 地址，如果没有找到，返回空字符串
    wirelessIPv4
  }

  def main(args: Array[String]): Unit = {

    val ip = getWirelessIPv4()
    println(ip)

    sendMsg("本地相册地址", s"http://$ip:80")

  }


}
