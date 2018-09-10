package eu.eyan.util.java.lang

import eu.eyan.log.Log

object ThreadPlus {
  def run[T](action: => T) = new ThreadRunner(action)

  class ThreadRunner[T](action: => T) {
    var ret: Option[T] = None
    val thread = new Thread(RunnablePlus.runnable(ret = try { Option(action) } catch { case e: Throwable => { Log.error(e); None } }))
    thread.start
    def result: Option[T] = ret
  }
}