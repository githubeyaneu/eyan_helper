package eu.eyan.util.time

import eu.eyan.log.Log

object TimeCounter {
  def countAndPrint(msg: String)(action: => Unit) = {
    val start = System.currentTimeMillis
    action
    val dur = System.currentTimeMillis - start
    Log.info(s"$msg $dur ms")
  }
}