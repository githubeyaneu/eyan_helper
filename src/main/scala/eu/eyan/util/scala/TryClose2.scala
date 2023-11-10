package eu.eyan.util.scala

object TryCatchFinallyClose2 {
  def apply[T, CLOSEABLE](closeable: => CLOSEABLE, action: => CLOSEABLE => T, errorAction: => Throwable => T, closeAction: => CLOSEABLE => Unit):T = {
    try {
      val toClose = closeable
      TryCatchFinally(action(toClose), (t:Throwable) => throw t, closeAction(toClose))
    } catch { case e: Throwable => errorAction(e) }
  }
}
