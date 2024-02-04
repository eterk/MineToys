package org.eterk.script


case class PSList(pid: Int,
                  tty: String,
                  time: String,
                  cmd: String)

object PSList {
  def apply(res: String): Seq[PSList] = {
    res
      .split("\n")
      .toSeq
      .tail //去掉标题
      .map(x => {
        val r =
          x
            .replaceAll("\\s+", " ")
            .split(" ")

        PSList(r(1).toInt, r(2), r(3), r(4))

      })
  }
}