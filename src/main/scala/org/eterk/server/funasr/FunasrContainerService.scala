package org.eterk.server.funasr

import org.eterk.server.Server
import org.eterk.util.PathUtil

import scala.sys.process._

protected[server] case class FunasrContainerService(containerName: String,
                                                    linuxPath: String) extends Server {

  val port: Int = 10095

  val imageID = "0250f8ef981b"

  val volumePath: String = "/home/data/"

  def pathIsIn(path: String): Boolean = {
    path.startsWith(linuxPath)
  }

  def pathToVolume(path: String): String = {
    println(path)
    if (pathIsIn(path)) {
      val res=
      path.replaceFirst(linuxPath, volumePath)
      println(path)
      println(linuxPath)
      println(volumePath)
      res
    } else {
      throw new IllegalAccessError(path)
    }
  }

  /**
   * {{{
   *   wsl.exe docker ps -q --filter ancestor=$imageID
   * }}}
   *
   * @return
   */
  lazy val containerId: String = {
    s"wsl.exe docker ps -q --filter name=$containerName".!!
  }

  def stop(): Boolean = {
    status() match {
      case 1 =>
        val stop_cmd = s"wsl.exe docker stop $containerId"
        stop_cmd.!
        stop()
      case _ => msg("已关闭")
        true
    }


  }


  def start(): Boolean = {

    status() match {
      case -1 => startDockerImage()
        start()
      case 0 => startContainerName()
        start()
      case 1 =>
        msg("container 已经启动")
        true
    }

  }

  val modelWinDir = "S://dev/model/funasr0.4.1/models"

  def startDockerImage(): Unit = {

    val localModel = PathUtil.windowsToWsl(modelWinDir)


    val run_cmd = s"wsl.exe nohup docker run -p $port:$port -it --detach --name $containerName  --privileged=true -v $localModel:/workspace/models -v $linuxPath:${volumePath} registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-cpu-0.4.1"


    msg(run_cmd)
    msg(run_cmd.!!)
  }

  def startContainerName(): Unit = {

    val run_cmd = s"wsl.exe nohup sudo docker start  $containerName"
    msg(run_cmd)
    msg(run_cmd.!!)
  }

  override def status(): Int = {
    val cmd =
      s"docker ps  -a -f name=${containerName} --format \"{{.Status}}\""


    val str = cmd.!!
    msg(str)

    str match {
      case "" => -1
      case running if running.startsWith("Up") => 1
      case stopped if stopped.startsWith("Exited") => 0
    }
  }

  override def execute(params: String*): Int = {

    cmd(params: _*).!

  }

  override def cmd(params: String*): String = s"wsl.exe docker exec $containerName ${params.head}"
}
