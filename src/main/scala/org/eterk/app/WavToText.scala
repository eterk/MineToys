package org.eterk.app

import org.eterk.server.FunasrService
import org.eterk.util.Util

import java.io.File

object WavToText extends App {


  override def appKey: String = "wtt"

  override def paramTypeSeq: Seq[String] = Seq("DIR:wav", "FILE:txt")


  override def execute(params: String*): Unit = {
    val Seq(audio_in, hotWord) = params.take(2)


    val dataHome = new File(audio_in).getParent
    msg("data_home is =>" + dataHome)

    if (hotWord == "") {
      msg("hot word is empty")
    } else {
      require(hotWord.endsWith(".txt"))
      require(new File(hotWord).getParent == dataHome, "hotword and audio must in same dir")

    }


    val service = FunasrService.defaultService(dataHome)

    service.start()

    Util
      .filterFiles(audio_in, p => p.endsWith(".wav"), recursive = false)
      .foreach {
        audioDir =>

          service.execute(audioDir, dataHome, hotWord)

      }

  }


}
