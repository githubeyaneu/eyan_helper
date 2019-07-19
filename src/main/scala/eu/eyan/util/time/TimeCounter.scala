package eu.eyan.util.time

import eu.eyan.log.Log

object TimeCounter {
  def now = System.currentTimeMillis
  def countAndPrint(msg: String)(action: => Unit) = Log.info(s"$msg ${millisecsOf(action)} ms") 

  def millisecsOf(action: => Unit) = {
    val start = now
    action
    now - start
  }
}