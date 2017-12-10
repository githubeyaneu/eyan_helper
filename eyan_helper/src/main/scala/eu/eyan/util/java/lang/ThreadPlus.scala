package eu.eyan.util.java.lang

object ThreadPlus {
  def run[T](action: => T) = new ThreadRunner(action)

  class ThreadRunner[T](action: => T) {
    var ret: Option[T] = None
    new Thread(RunnablePlus.runnable( ret = Option(action) )).start
    def result: T = ret.get
  }
}