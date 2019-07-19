package eu.eyan.util.scala

import scala.util.Failure
import scala.util.Success
import java.io.Closeable
import eu.eyan.log.Log
import eu.eyan.util.io.CloseablePlus._

/** To execute sg silently -> Throwable catched, logged, result as Try.*/
object Try {
  /**To execute sg silently -> Throwable catched, logged, result as Try. */
  def apply[T](action: => T) = TryCatchFinally(Success(action), e => { Log.error(e); Failure(e) }, {})
}
//object CloseFinally { def apply[T, CLOSEABLE <: Closeable](closeable: => CLOSEABLE, action: CLOSEABLE => T) = FinallyClose(closeable, action) }
//object FinallyClose { def apply[T, CLOSEABLE <: Closeable](closeable: => CLOSEABLE, action: CLOSEABLE => T) = TryFinally(action(closeable), CloseablePlus.closeQuietly(closeable)) }
/** closes (quietly) the closeables afterwards. */
object TryCatchFinallyClose {
  /** closes (quietly) the closeable afterwards. */
  def apply[T, CLOSEABLE <: Closeable](closeable: => CLOSEABLE, action: => CLOSEABLE => T, errorAction: => Throwable => T) =
    try {
      val toClose = closeable
      TryCatchFinally(action(toClose), errorAction, closeQuietly(toClose))
    } catch { case e: Throwable => errorAction(e) }

  /** closes (quietly) the 2 closeables afterwards. */
  def apply[T, CLOSEABLE1 <: Closeable, CLOSEABLE2 <: Closeable](closeable1: => CLOSEABLE1, closeable2: => CLOSEABLE2, action: => (CLOSEABLE1, CLOSEABLE2) => T, errorAction: => Throwable => T) =
    try {
      val toClose1 = closeable1
      try {
        val toClose2 = closeable2
        TryCatchFinally(action(toClose1, toClose2), errorAction, closeQuietly(toClose2))
      } finally closeQuietly(toClose1)
    } catch { case e: Throwable => errorAction(e) }
}

//TODO: create TESTS TryFinallyClose without Catch, return is Try[T]
object TryFinallyClose {
  /** closes (quietly) the closeable afterwards. */
  def apply[T, CLOSEABLE <: Closeable](closeable: => CLOSEABLE, action: => CLOSEABLE => T) = {
    val toClose = closeable
    try {
      Try(action(toClose))
    } finally {
      closeQuietly(toClose)
    }
  }
}

// use try{} finally {} instead. This has no really use.
//object TryFinally { def apply[T](action: => T, finaly: => Unit) = TryCatchFinally[scala.util.Try[T]](Success(action), e => { Log.error(e); Failure(e) }, finaly) }

// use try{} catch {case t:Throwable =>{}} instead. This has no really use.
// TODO: write tests
object TryCatch { def apply[T](action: => T, errorAction: => T) = TryCatchThrowable[T](action, t => errorAction) }
// TODO: write tests
object TryCatchThrowable { def apply[T](action: => T, errorAction: => Throwable => T) = TryCatchFinally[T](action, errorAction, {}) }

object TryCatchFinally { def apply[T](action: => T, errorAction: => Throwable => T, finaly: => Unit) = new TryCatchFinally[T](action, errorAction, finaly).execute /*.Catch(errorAction)*/ }

private class TryCatchFinally[T](action: => T, errorAction: => Throwable => T, finaly: => Unit) {
  private def execute =
    try action
    catch { case t: Throwable => errorAction(t) }
    finally finaly

  //  private def Catch(errorAction: => T) = tryResult match {
  //    case Success(res) => res
  //    case Failure(t)   => errorAction
  //  }

  //  private def Catch(errorAction: => Throwable => T) = tryResult match {
  //    case Success(res) => res
  //    case Failure(t)   => errorAction(t)
  //  }

}

object TryPlus {
  implicit class TryPlusImplicit[FROM](tri: scala.util.Try[FROM]) {

    // TODO: write tests....
    def mapWithErrorHandler[TO](mapper: FROM => TO, errorHandler: Throwable => Unit): scala.util.Try[TO] = {
      tri match {
        case Success(value)     => TryCatchThrowable(Success(mapper(value)), t => Failure(t))
        case Failure(throwable) => Failure(throwable)
      }
    }
  }
}