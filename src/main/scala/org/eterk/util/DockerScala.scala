package org.eterk.util

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.command.ExecStartResultCallback

object DockerScala {

  def main(args: Array[String]): Unit = {

    // 创建一个 docker 客户端
    val dockerClient: DockerClient = DockerClientBuilder.getInstance().build()

    // 创建一个 docker 容器
    val container: CreateContainerResponse = dockerClient.createContainerCmd("hello-scala:0.1.0")
      .withName("hello-scala-container")
      .exec()

    // 启动 docker 容器
    dockerClient.startContainerCmd(container.getId).exec()

    // 在 docker 容器内执行一个命令
    val execId: String = dockerClient.execCreateCmd(container.getId)
      .withCmd("echo", "Hello from docker!")
      .withAttachStdout(true)
      .withAttachStderr(true)
      .exec()
      .getId

    // 获取执行结果的输出
    val output: String = dockerClient.execStartCmd(execId)
      .exec(new ExecStartResultCallback())
      .awaitCompletion()
      .toString

    // 打印输出
    println(output)

    // 停止 docker 容器
    dockerClient.stopContainerCmd(container.getId).exec()

    // 删除 docker 容器
    dockerClient.removeContainerCmd(container.getId).exec()

    // 关闭 docker 客户端
    dockerClient.close()
  }
}

