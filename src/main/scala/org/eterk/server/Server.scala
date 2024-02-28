package org.eterk.server

import org.eterk.util.Logger

trait Server extends Lifecycle with Logger {

  def status(): Int


  def execute(params: String*): Int

  def cmd(params: String*): String
}

trait Lifecycle {


  def start(): Boolean

  def stop(): Boolean
}
