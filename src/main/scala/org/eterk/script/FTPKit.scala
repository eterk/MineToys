package org.eterk.script


import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream, OutputStream}
import scala.util.{Failure, Success, Try}

object FTPKit {

  import java.net.URL
  import java.io.BufferedReader
  import java.io.InputStreamReader
  import java.io.File
  import java.nio.file.{Files, Paths, StandardCopyOption}

  import java.net.URL
  import java.io.{InputStream, OutputStream, BufferedInputStream, BufferedOutputStream, FileOutputStream}
  import java.nio.file.{Files, Paths, StandardCopyOption}

  // 定义一个函数，用来从一个 ftp 链接复制一个文件到一个本地路径，并重命名
  def copyFileFromFtp(ftpLink: String, localPath: String, newName: String): Unit = {
    // 创建一个 URL 对象，连接 ftp 服务器
    val url = new URL(ftpLink)
    // 创建一个 InputStream 对象，读取 ftp 服务器上的文件
    val in: InputStream = new BufferedInputStream(url.openStream())
    // 创建一个 OutputStream 对象，写入本地路径的文件
    val out: OutputStream = new BufferedOutputStream(new FileOutputStream(localPath + newName))
    // 创建一个字节数组，用来存储读取的数据
    val buffer = new Array[Byte](128)
    // 创建一个变量，用来记录读取的字节数
    var len = 0
    // 循环读取数据，直到结束
    while ( {
      len = in.read(buffer);
      len != -1
    }) {
      // 将读取的数据写入输出流
      out.write(buffer, 0, len)
    }
    // 关闭输入流和输出流
    in.close()
    out.close()
  }

  // 定义一个函数，将一个文件复制到一个目标目录
  def copyFilesToDir(file: File, dir: String): Unit = {
    // 创建一个 File 对象，表示目标目录
    val target = new File(dir)
    // 如果目标目录不存在，就创建它
    if (!target.exists()) {
      target.mkdirs()
    }
    // 获取文件名
    val fileName = file.getName
    // 创建一个 Path 对象，表示目标文件的路径
    val targetPath = Paths.get(dir + fileName)
    // 使用 Files.copy 方法，复制文件到目标目录
    Files.copy(file.toPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
    println(s"Copied $fileName to $dir")
  }

  // 定义一个 case class，表示文件类型的相关信息
  case class FormatInfo(tpe: String, // 文件类型
                        dirNum: Int, //涉及的文件夹数量
                        fileSize: Int, // 总的占用存储大小
                        fileLink: Seq[String] //涉及到的文件路径
                       ) {
    override def toString: String = {
      s"$tpe=>  dir:$dirNum fileNums:$fileNums  memoryUsage:${fileSize / 1024.0 / 1024.0}MB "
    }

    private def fileNums: Int = fileLink.size
  }


  // 定义一个函数，将一行目录信息转换成 DirInfo 对象
  def parseFtpInfo(line: String): FTPInfo = {
    // 使用空格分割目录信息，得到一个数组
    val arr = line.split("\\s+")
    // 创建一个 DirInfo 对象，使用数组中的元素作为参数
    FTPInfo(
      permission = arr(0),
      link = arr(1).toInt,
      owner = arr(2),
      group = arr(3),
      size = arr(4).toInt,
      time = arr.slice(5, 8).mkString(" "),
      name = arr.slice(8, arr.size).mkString(" ")
    )
  }

  // 定义一个 case class，表示文件夹的属性
  case class FTPInfo(
                      permission: String, // 文件夹的权限
                      link: Int, // 文件夹的硬链接数
                      owner: String, // 文件夹的拥有者的用户名
                      group: String, // 文件夹的所属组的名称
                      size: Int, // 文件夹的大小，单位是字节
                      time: String, // 文件夹的最后修改时间
                      name: String // 文件夹的名称
                    ) {
    def isDir: Boolean = if (permission.startsWith("d")) true else false

    def getTpe: String = {
      if (isDir) {
        "DIR"
      } else {
        val index = {
          name.lastIndexOf(".")
        }
        if (index == -1) {
          "UNKNOWN"
        } else {
          val tpe = name.substring(index)
          if (tpe == "") {
            "UNKNOWN"
          } else {
            tpe.toLowerCase()
          }
        }


      }

    }
  }


  // 定义一个函数，从一个 ftp 链接获取目录信息，并返回一个 Map，其中键是文件类型，值是 FormatInfo
  def getSummaryByFormat(ftpLink: String): Map[String, FormatInfo] = {
    // 创建一个 Map 对象，存储文件类型和 FormatInfo 的映射
    val summaryMap = scala.collection.mutable.Map[String, FormatInfo]()

    // 调用辅助函数，从根目录开始遍历
    traverseDir(ftpLink, summaryMap, "")
    // 返回一个不可变的 Map
    summaryMap.toMap
  }


  // 定义一个辅助函数，递归地遍历 ftp 目录的所有子目录和文件，更新 Map 中的数据
  def traverseDir(ftpLink: String, summaryMap: scala.collection.mutable.Map[String, FormatInfo], dir: String): Unit = {

    // 创建一个 URL 对象，连接 ftp 服务器
    val url = new URL(ftpLink + dir)


    // 创建一个 BufferedReader 对象，读取目录信息
    Try(new BufferedReader(new InputStreamReader(url.openStream()))) match {

      case Failure(exception) => println(s"visit => $url failed")
      case Success(br) =>
        var line = br.readLine()

        while (line != null) {
          val info = parseFtpInfo(line)
          info.getTpe match {
            case "DIR" => // 获取文件夹名
              // 在 summaryMap 中增加文件夹的数量
              summaryMap
                .keys
                .foreach { k =>
                  val info = summaryMap(k)
                  summaryMap(k) = info.copy(dirNum = info.dirNum + 1)
                }
              // 递归地遍历该文件夹
              traverseDir(ftpLink, summaryMap, dir + info.name + "/")
            case tpe =>
              val fileLink = ftpLink + dir + info.name
              // 在 summaryMap 中增加文件的数量，文件的大小，以及文件的路径
              val formatInfo: FormatInfo = summaryMap.getOrElse(tpe, FormatInfo(tpe, 0, 0, Seq()))
              summaryMap(tpe) = formatInfo.copy(
                fileSize = formatInfo.fileSize + info.size,
                fileLink = formatInfo.fileLink :+ fileLink
              )
          }

          // 读取下一行
          line = br.readLine()
        }
        // 关闭 BufferedReader 对象
        br.close()
    }
  }

  // 定义一个 saveObj 函数，用来存储 scala 对象到本地路径
  def saveObj[T](obj: T, path: String): Boolean = {
    // 使用 try-catch 语句来处理可能的异常
    try {
      // 创建一个 FileOutputStream 对象，表示要写入的文件流
      val fos = new FileOutputStream(path)
      // 创建一个 ObjectOutputStream 对象，表示要写入的对象流
      val oos = new ObjectOutputStream(fos)
      // 使用 ObjectOutputStream.writeObejct 方法，将 obj 对象写入对象流
      oos.writeObject(obj)
      // 关闭 ObjectOutputStream 对象
      oos.close()
      // 关闭 FileOutputStream 对象
      fos.close()
      // 返回 true，表示存储成功
      true
    } catch {
      // 如果发生异常，打印异常信息
      case e: Exception =>
        e.printStackTrace()
        // 返回 false，表示存储失败
        false
    }
  }

  // 定义一个 load 函数，用来读取 scala 对象从本地路径
  def load[T](path: String): Option[T] = {
    // 使用 try-catch 语句来处理可能的异常
    try {
      // 创建一个 FileInputStream 对象，表示要读取的文件流
      val fis = new FileInputStream(path)
      // 创建一个 ObjectInputStream 对象，表示要读取的对象流
      val ois = new ObjectInputStream(fis)
      // 使用 ObjectInputStream.readObject 方法，从对象流中读取一个对象，然后转换为 T 类型
      val obj = ois.readObject().asInstanceOf[T]
      // 关闭 ObjectInputStream 对象
      ois.close()
      // 关闭 FileInputStream 对象
      fis.close()
      // 返回 Some(obj)，表示读取成功
      Some(obj)
    } catch {
      // 如果发生异常，打印异常信息
      case e: Exception =>
        e.printStackTrace()
        // 返回 None，表示读取失败
        None
    }
  }


  def main(args: Array[String]): Unit = {
    val ftpRoot = "ftp://192.168.137.132:7021/"
    val path = "S://dev/obj_tmp_map_1"
    //    val mapG: Map[String, FormatInfo] = getSummaryByFormat(ftpRoot)
    //    saveObj(mapG, path)

    val map = load[Map[String, FormatInfo]](path).get
    map
      .toSeq
      .sortBy(x => x._2.fileSize)
      .reverse
      .foreach(println)

    // 定义一个 Seq，包含我列举的常用格式
    val need = Seq(
      //      ".png", // 图像文件格式
//            ".pdf", // 文档文件格式
//            ".docx", // 文档文件格式
      //      ".xlsx", // 电子表格文件格式
      //      ".wav", // 音频文件格式
      //      ".mp3", // 音频文件格式
      //      ".mp4", // 视频文件格式
      //      ".zip", // 压缩文件格式
//      ".txt", // 文本文件格式
      //      ".csv", // 文本文件格式
      //      ".xml", // 文本文件格式
            ".gif", // 图像文件格式
      //      ".bat" // 批处理文件格式
    )

    val outputDir = "S:\\tmp\\backup\\"
    map
      .filter(x => need.contains(x._1))
      .foreach {
        case (k, v) =>
          val output = outputDir + k + "/"
          println(output)

          val target = new File(output)
          // 如果目标目录不存在，就创建它
          if (!target.exists()) {
            target.mkdirs()
          }
          println(v)

          v.fileLink
            //            .headOption
            .foreach(f => {
              val newName =
                f
                  .replaceAll(ftpRoot, "")
                  .replaceAll("\\s+", "-")
                  .replaceAll("\\/", "_")

              val info= f + "   " + newName
//              println(info)

              Try(copyFileFromFtp(f, output, newName))
                .recover {
                  case e => println(info + " " + e.getMessage)
                }


            })

      }

    //    println(map.keySet.mkString(" , "))
  }


}

import java.io.{ObjectOutputStream, ObjectInputStream, FileOutputStream, FileInputStream}
