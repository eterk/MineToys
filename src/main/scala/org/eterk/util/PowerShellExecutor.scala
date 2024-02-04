package org.eterk.util

object PowerShellExecutor {

  import scala.sys.process._

  def runPowershell(cmd: String): Unit = {
    // 拼接powershell命令字符串，使用Bypass执行策略，注意转义引号和空格
    val fullCmd = s"""powershell -ExecutionPolicy Bypass -Command "$cmd""""
    // 执行命令，忽略输出
    fullCmd.!
  }
}
