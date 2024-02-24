package org.eterk.server

import org.eterk.server.funasr.{FunasrContainerService, FunasrServerService}
import org.eterk.util.{Logger, PathUtil}

import scala.util.Try

object FunasrService extends Logger {

  lazy val service: FunasrService =defaultService()

  def defaultService(dataHome: String): FunasrService = {
    val str =
      dataHome
        .replaceAll(":", "-")
        .replaceAll("\\\\", "-")
        .replaceAll("/", "-")

    val containerName = "mt-fs-" + str

    val serverLogName = s"funasr-service-$containerName-${System.nanoTime()}"


    FunasrService(containerName, serverLogName, dataHome)
  }

  def defaultService(): FunasrService = {

    val containerName = "mt-fs-chinese"

    val serverLogName = s"funasr-service-$containerName-${System.nanoTime()}"


    FunasrService(containerName, serverLogName)
  }


  def apply(containerName: String, serverLogName: String, dataHome: String): FunasrService = {
    val linuxPath = PathUtil.windowsToWsl(dataHome)
    val container = FunasrContainerService(containerName, linuxPath)
    val server = FunasrServerService(serverLogName, container)
    new FunasrService(container, server)
  }

  def apply(containerName: String, serverLogName: String): FunasrService = {
    val container = FunasrContainerService(containerName, "/mnt/s/")
    val server = FunasrServerService(serverLogName, container)
    new FunasrService(container, server)
  }


}

case class FunasrService(container: FunasrContainerService,
                         server: FunasrServerService) extends Server {

  def serverInfo = ""


  override def status(): Int = {

    if (container.status() == 1 && server.status() != 0) {
      1
    } else {
      0
    }

  }

  override def start(): Boolean = {
    Try(container.start())
      .map(u => server.start())
      .get


  }

  override def stop(): Boolean = {
    server.stop() && container.stop()
  }

  override def execute(params: String*): Int = {

    msg(params.mkString(","))

    val paramSeq =
      params
        .map {
          case "" => ""
          case x => PathUtil.windowsToWsl(x)
        }
        .map {
          case "" => ""
          case x => container.pathToVolume(x)
        }

    msg(paramSeq.mkString(","))

    server.execute(paramSeq: _*)
  }

  override def cmd(params: String*): String = ???
}