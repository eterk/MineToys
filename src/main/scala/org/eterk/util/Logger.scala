package org.eterk.util

import org.eterk.util.Logger.callLocation


object Logger {

  def debug: Boolean = debug_

  private var debug_ : Boolean = true

  def setDebug(b: Boolean): Unit = this.debug_ = b

  // 定义一个 callLocation 函数，返回一个字符串
  def callLocation: String = {
    // 获取当前线程的堆栈跟踪
    val stackTrace = Thread.currentThread.getStackTrace
    // 如果堆栈跟踪为空或只有一个元素，返回空字符串
    if (stackTrace == null || stackTrace.length <= 1) ""
    else {
      // 否则，获取堆栈跟踪的第二个元素，即调用 callLocation 的位置


      import ColorString._
      stackTrace
        .slice(4,10)
        .map(trace=>(trace.getFileName,trace.getLineNumber))
        .reverse
        .scanLeft(Seq.empty[(String,Int)]){
          (seq,context)=>{
            if( seq.nonEmpty&&seq.head._1==context._1){
              seq
            }else{
              context+:seq
            }
          }
        }
        .last
        .map(x=> s"@.${x._1}:${x._2}")
        .toSeq
        .magentaYellow(" , ")


//
//      stackTrace.take(10).map(caller => {
//        val fileName = caller.getFileName
//        val lineNumber = caller.getLineNumber
//        s"@.$fileName:$lineNumber"
//      }).toSeq.magentaYellow(" , ")
      // 获取文件名，行号，类名和方法名

      //      val className = caller.getClassName
      //      val methodName = caller.getMethodName
      // 拼接成一个字符串，格式为 "文件名:行号 (类名.方法名)"
      //      ($className.$methodName)

      //      s"@.$fileName:$lineNumber".green
    }
  }


}

trait Logger {

  import org.eterk.util.Logger.debug

  protected def msg(f: String): Unit = {
    if (debug) {
      println(callLocation + "   " + f)
    }
  }

  protected def debugDo(f: () => Unit): Unit = {
    if (debug) {
      f()
    }
  }

}
