package org.eterk.server.funasr

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.{Bind, ExposedPort, Ports, Volume}
import com.github.dockerjava.core.DockerClientBuilder
import org.eterk.server.Server

import java.lang

class FunasrDocker(containerName: String,
                   dataPath: String) extends Server {
  // 创建一个 docker 客户端
  val dockerClient: DockerClient = DockerClientBuilder.getInstance().build()

  val port: Int = 10095

  val imageID = "0250f8ef981b"
  /**
   * {{{
   *   wsl.exe docker ps -q --filter ancestor=$imageID
   * }}}
   *
   * @return
   */
  lazy val containerId: String = getContainer.getId

  val containerDatPath = "/home/data"

  private lazy val getContainer = {
    val localModel = "~/funasr0.4.1/funasr-runtime-resources/models"

    // 创建一个端口映射对象，用于设置容器的端口映射
    val portBindings = new Ports()
    // 将容器的 10095 端口映射到主机的 10095 端口
    portBindings.bind(ExposedPort.tcp(port), Ports.Binding.bindPort(port))

    // 创建一个绑定对象，用于设置容器的数据卷绑定
    val binds0 = new Bind(dataPath, new Volume(containerDatPath))
    val binds1 = new Bind(localModel, new Volume("/workspace/models"))

    // 创建一个 docker 容器
    dockerClient
      .createContainerCmd(imageID)
      .withName(containerName)
      .withPortBindings(portBindings)
      .withBinds(binds0, binds1)
      .withPrivileged(true)
      .exec()
  }

  def startDockerImage(): Unit = {

    val container = getContainer

    // 启动 docker 容器
    dockerClient.startContainerCmd(container.getId).exec()
  }

  def stop(): Boolean = {
    status() match {
      case 1 =>
        // 停止 docker 容器
        dockerClient.stopContainerCmd(containerId).exec()
        stop()
      case _ => msg("已关闭")
        true
    }
  }


  override def status(): Int = {
    Option(dockerClient.inspectContainerCmd(containerName).exec()) match {
      case Some(f) => Option(f.getState) match {
        case Some(f) if f.getRunning => 1
        case _ => 0
      }
      case None => -1
    }
  }

  override def start(): Boolean = status() match {
    case 1 => msg("已启动")
      true
    case _ => startDockerImage()
      start()
  }

  override def execute(params: String*): Int = {
    // 使用 docker 客户端的 execCreateCmd 方法，创建一个执行容器内命令的请求
    val execCreateResponse = dockerClient.execCreateCmd(containerName)
      // 设置要执行的命令，可以是一个或多个参数
      .withCmd(params: _*)
      // 设置是否附加到标准输出和标准错误
      .withAttachStdout(true)
      .withAttachStderr(true)
      // 执行请求，获取响应
      .exec()

    // 获取执行请求的 ID
    //    val execId = execCreateResponse.getId
    //
    //    // 使用 docker 客户端的 execStartCmd 方法，启动执行容器内命令的进程
    //    val execStartResponse = dockerClient.execStartCmd(execId)
    //      // 执行请求，获取响应
    ////      .exec()
    //
    //    // 使用 docker 客户端的 execInspectCmd 方法，检查执行容器内命令的进程的状态
    //    val execInspectResponse = dockerClient.execInspectCmd(execId)
    //      // 执行请求，获取响应
    //      .exec()
    //
    //    // 获取执行容器内命令的进程的退出码
    //    val exitCode = execInspectResponse.getExitCode
    //
    //    // 返回退出码
    //    exitCode
    1
  }

  override def cmd(params: String*): String = ???
}
