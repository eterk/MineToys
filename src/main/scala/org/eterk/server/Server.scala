package org.eterk.server

import org.eterk.util.Logger

trait Server extends Logger{

  def status(): Int

  def start(): Boolean

  def stop(): Boolean

   def execute(params: String*): Int

  def cmd(params: String*): String
}
