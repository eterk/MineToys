package org.eterk.app

import org.eterk.server.FunasrService
import org.eterk.util.Util

import java.io.File
import java.time.LocalDate

object WavToText extends App {


  override def appKey: String = "wtt"

  override def paramTypeSeq: Seq[String] = Seq("DIR_FILE:wav,mp4", "FILE:txt")


  override def execute(params: String*): Unit = {
    val Seq(audioOrMp4, hotWord) = params.take(2)
    val dataHome: String =
      new File(audioOrMp4) match {
        case file if file.isFile => new File(audioOrMp4).getParent
        case dir if dir.isDirectory => dir.getAbsolutePath
      }

    msg("data_home is =>" + dataHome)

    if (hotWord == "") {
      msg("hot word is empty")
    } else {
      require(hotWord.endsWith(".txt"))
      require(new File(hotWord).getParent == dataHome, "hotword and audio must in same dir")

    }
    // 开启服务
    val service = FunasrService.service

    service.start()

    Util
      .filterFiles(audioOrMp4, p => p.endsWith(".wav") || p.endsWith(".mp4"), recursive = false)
      .flatMap {
        case mp4 if mp4.endsWith(".mp4") =>
          ExportWav.execute(mp4, "10")
        case wav => wav :: Nil
      }
      .map(file => {
        println(file)
        val f = new File(file)
        if (f.isDirectory) {
          (f.getName, f, f.listFiles().map(_.getAbsolutePath).toSeq)
        } else {
          val nameWithType = f.getName
          val (name, tpe) = nameWithType.splitAt(nameWithType.lastIndexOf("."))
          (name, f, f.getAbsolutePath :: Nil)
        }
      })
      .foreach {
        case (name: String, srcFile: File, seq: Seq[String]) =>
          msg(name)
          seq.map(service.execute(_, dataHome, hotWord))
          val getFile = (name: String) => new File(new File(dataHome).getAbsolutePath + "/" + name)
          val output = getFile("text.0_0")
          if (output.exists()) {
            output.renameTo(getFile(s"${LocalDate.now()}-${name}.md"))
            srcFile.delete()
          } else {
            msg(s"$name 导出失败!")
          }

      }

    //

  }


}
