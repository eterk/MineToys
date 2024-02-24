package org.eterk.app

import org.eterk.server.FunasrService
import org.eterk.util.Util

import java.io.File

object WavToText extends App {


  override def appKey: String = "wtt"

  override def paramTypeSeq: Seq[String] = Seq("DIR_FILE:wav,mp4", "FILE:txt")


  override def execute(params: String*): Unit = {
    val Seq(audioOrMp4, hotWord) = params.take(2)

    println(audioOrMp4)

    val dataHome = new File(audioOrMp4).getParent
    msg("data_home is =>" + dataHome)


    if (hotWord == "") {
      msg("hot word is empty")
    } else {
      require(hotWord.endsWith(".txt"))
      require(new File(hotWord).getParent == dataHome, "hotword and audio must in same dir")

    }
    // 开启服务
    val service = FunasrService.service
    //      FunasrService.defaultService(dataHome)

    service.start()

    Util
      .filterFiles(audioOrMp4, p => p.endsWith(".wav") || p.endsWith(".mp4"), recursive = false)
      .flatMap {
        case mp4 if mp4.endsWith(".mp4") =>
          //          ExportWav.execute(mp4, "10")
          // todo 为开发完
          Nil
        case wav => wav :: Nil
      }
      .foreach {
        audioDir =>
          service.execute(audioDir, dataHome, hotWord)
      }

  }


}
