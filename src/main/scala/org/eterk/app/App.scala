package org.eterk.app

import org.eterk.util.Util

trait App {
  def appName: String

  def paramSeq: Seq[String]

  def paramDescription: Seq[String]

  def appDescription: String

  def execute(params: String*): Unit


  def help(): Unit

}


object App {

  def main(args: Array[String]): Unit = {

    //    val audio_in =
    //    val output_dir = "/home/data/output"
//    WavToText.execute("/home/data/a.wav", "/home/data/output")
//    WavToText.execute("S:\\util\\icon\\disk_c", "S:\\util\\icon\\disk_c")



    Util
      .filterFiles("S:\\util\\icon\\disk_c",p=>p.endsWith(".svg"),recursive = false)
      .foreach(p=>{
        ImageToIco.execute(p,Util.repalceFileFomat(p,"ico"))
      })

  }








}


