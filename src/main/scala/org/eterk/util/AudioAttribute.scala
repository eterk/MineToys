package org.eterk.util

import ws.schild.jave.AudioAttributes


case class AudioAttribute(sampleRate: Int,
                          channels: Int,
                          bitRate: Int,
                          codec: String) {

  override def toString: String = {
    s"""
       |音频的采样率:$sampleRate Hz;
       |音频的声道数:$channels;
       |音频的比特率:$bitRate ;
       |音频的编码格式:$codec;
       |""".stripMargin

  }

  def samplesPerByte: Double = (sampleRate * channels) / bitRate

  def bytesPerSample: Int = bitRate / (sampleRate * channels)


  // 计算每个分割文件的字节数（不包括头部）
  def bytesPerFile(interval: Int): Int = sampleRate * channels * bytesPerSample * interval

  def create(): AudioAttributes = {
    // 创建一个 AudioAttributes 对象
    val audio = new AudioAttributes()
    // 设置音频的编码格式为 PCM
    audio.setCodec(codec)
    // 设置音频的采样率为 16 kHz
    audio.setSamplingRate(sampleRate)
    // 设置音频的声道数为 1
    audio.setChannels(channels)
    // 设置音频的比特率为 16 bit
    audio.setBitRate(bitRate)
    // 返回 AudioAttributes 对象
    audio
  }
}

