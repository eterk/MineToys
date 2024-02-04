package org.eterk.util

object SeqExtension extends Logger {

  // 定义一个隐式类，用于扩展 seq 的功能
  implicit class SeqExt[A](seq: Seq[A]) {
    def showChange[B](f: Seq[A] => Seq[B]): Seq[B] = {
      val r = f(seq)
      msg(s"${seq.length}=>${r.length}")
      r
    }


  }


}