package org.eterk.server.funasr

import org.eterk.script.PSList
import org.eterk.server.Server
import scala.sys.process._

protected[server] case class FunasrServerService(serverLogName: String,
                                                 container: FunasrContainerService) extends Server {


  val serverLogPath: String = s"${container.volumePath}/${serverLogName}.log"

  msg("log at:" + serverLogPath)

  def startServer(): Int = {


    val serverStart = s"cd /workspace/FunASR/runtime/;nohup bash run_server.sh --download-model-dir /workspace/models --vad-dir damo/speech_fsmn_vad_zh-cn-16k-common-onnx --model-dir damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-onnx --punc-dir damo/punc_ct-transformer_cn-en-common-vocab471067-large-onnx --lm-dir damo/speech_ngram_lm_zh-cn-ai-wesp-fst --itn-dir thuduj12/fst_itn_zh > $serverLogPath 2>&1 &"

    val pid = container.execute("bash -c \"" + serverStart + "\"")

    msg("sleep 10s wait server startup")
    Thread.sleep(10 * 1000)
    msg("sleep 10s wait server startup")
    Thread.sleep(10 * 1000)
    pid

  }

  def serverStatus(): Option[PSList] = {
    if (container.status() != 1) {
      None
    } else {
      val cmd = container.cmd("ps")

      PSList(cmd.!!)
        .find(_.cmd == "funasr-wss-serv")
    }


  }

  override def start(): Boolean = {
    if (status() == 0) {
      startServer()
      start()
    } else {
      msg("server已经启动")
      true
    }


  }

  override def stop(): Boolean = {
    val statusCode = status()
    if (statusCode == 0) {
      msg("already close")
      true
    } else {
      container.execute(s"kill -9 $statusCode")
      stop()
    }


  }


  override def status(): Int = serverStatus() match {
    case Some(ps) => ps.pid
    case None => 0
  }

  override def execute(params: String*): Int = {

    val command: String = cmd(params.head, params(1), params(2))

    msg(command)

    container.execute(command)

  }

  override def cmd(params: String*): String = {

    val Seq(audio_in, output_dir, hot_word) = params.take(3)
    val host = "127.0.0.1"
    val port = container.port
    val mode = "offline"
    val hotword = if (hot_word.isEmpty) {
      ""
    } else {
      s"--hotword $hot_word"
    }

    // 构造你的命令

    val app = s"python3 /workspace/FunASR/runtime/python/websocket/funasr_wss_client.py"

    val param = s"--host $host --port $port --mode $mode --audio_in $audio_in $hotword --output_dir $output_dir"

    app + " " + param
  }
}
