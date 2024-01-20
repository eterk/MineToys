package org.eterk.app

import org.eterk.util.Util

import java.io.File

object WavToText extends App {


  import sys.process._


  override def appKey: String = "wtt"

  override def appName: String = "wav to text"

  override def paramSeq: Seq[String] = Seq("audio_in")


  override def appDescription: String = "use funasr to transfer wav to text(funasr server require)"

  override def execute(params: String*): Unit = {
    val audio_in = params.head
    val inputFiles =
      Util
        .filterFiles(audio_in, p => p.endsWith(".wav"), recursive = false)
        .map(p => {
          val path = "/home/data/video/"
          val in = path + new File(p).getName
          (in, path)
        })

    inputFiles.foreach(audio => {
      println(audio)
      callServer(audio._1, audio._2)
    })

  }


  def callServer(audio_in: String, output_dir: String): Unit = {
    val host = "127.0.0.1"
    val port = 10095
    val mode = "offline"
    val imageID = "0250f8ef981b"
    val containerId = s"wsl.exe docker ps -q --filter ancestor=$imageID".!!

    println(containerId)
    // 构造你的命令
    val cmd = s"wsl.exe docker exec $containerId python3 /workspace/FunASR/runtime/python/websocket/funasr_wss_client.py --host $host --port $port --mode $mode --audio_in $audio_in --output_dir $output_dir"

    println(cmd)

    val result = cmd.!!

    println(result)
  }

  def x1(audio_in: String, output_dir: String) = {
    //    import sys.process._
    //    import java.io.File

    // 定义你的参数
    val host = "127.0.0.1"
    val port = 10095
    val mode = "offline"
    val imageID = "0250f8ef981b"

    //    // 启动你的镜像
    //    val run_cmd = s"wsl.exe docker run -p $port:$port -it --privileged=true -v ~/funasr0.4.1/funasr-runtime-resources/models:/workspace/models -v /mnt/s/lib:/home/data registry.cn-hangzhou.aliyuncs.com/funasr_repo/funasr:funasr-runtime-sdk-cpu-0.4.1"
    //
    //    Thread.sleep(5000)
    //    println(run_cmd)
    //    println(run_cmd.!!)
    //
    //    // 获取你的容器的ID
    val containerId = s"wsl.exe docker ps -q --filter ancestor=$imageID".!!
    //
    println(containerId)
    //    // 启动你的服务端

    val dockerExec = s"wsl.exe docker exec ${containerId} "

    val serverStart = "cd /workspace/FunASR/runtime/;nohup bash run_server.sh --download-model-dir /workspace/models --vad-dir damo/speech_fsmn_vad_zh-cn-16k-common-onnx --model-dir damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-onnx --punc-dir damo/punc_ct-transformer_cn-en-common-vocab471067-large-onnx --lm-dir damo/speech_ngram_lm_zh-cn-ai-wesp-fst --itn-dir thuduj12/fst_itn_zh > log.out 2>&1 &"
    val server_cmd = dockerExec + "bash -c\"" + serverStart + "\""
    server_cmd.!


    println(server_cmd)
    Thread.sleep(3000)
    //
    //    // 启动你的客户端
    val client_cmd = s"wsl.exe docker exec $containerId python3 /workspace/FunASR/runtime/python/websocket/funasr_wss_client.py --host $host --port $port --mode $mode --audio_in $audio_in --output_dir $output_dir"

    println(client_cmd)
    val result = client_cmd.!!
    //
    //    // 输出你的结果
    println(result)
    //
    //    // 关闭你的镜像服务
    //    val stop_cmd = s"wsl.exe docker stop $containerId"
    //    stop_cmd.!


  }

}
