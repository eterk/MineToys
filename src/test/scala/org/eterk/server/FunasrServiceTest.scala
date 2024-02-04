package org.eterk.server

import org.eterk.util.Logger
import org.scalatest.funsuite.AnyFunSuiteLike

class FunasrServiceTest extends AnyFunSuiteLike {


  test("server") {

    Logger.setDebug(true)

    val dataHome = "s:/lib/test"
    val service = FunasrService.defaultService(dataHome)
    service.stop()
    service.start()
    //
    service.execute("s:/lib/test/test.wav", "s:/lib/test/", "s:/lib/test/hot.txt")

    service.stop()
  }

}
