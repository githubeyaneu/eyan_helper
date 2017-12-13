package eu.eyan.util.scala

import scala.util.Failure
import scala.util.Success
import java.io.Closeable
import eu.eyan.log.Log

object Try { def apply[T](action: => T): T = TryFinally[T](action, {}) }
object TryCatch { def apply[T](action: => T, errorAction: Throwable => T): T = TryCatchFinally[T](action, errorAction, {}) }
object TryFinally { def apply[T](action: => T, finaly: => Unit): T = TryCatchFinally[T](action, e => { Log.error(e); null.asInstanceOf[T] }, finaly) } //TODO / FIXME: in error case null as result. Is it good? Should be Optional?
object CloseFinally { def apply[T, CLOSEABLE <: Closeable](closeable: CLOSEABLE, action: CLOSEABLE => T): T = FinallyClose(closeable, action) }
object FinallyClose { def apply[T, CLOSEABLE <: Closeable](closeable: CLOSEABLE, action: CLOSEABLE => T): T = TryCatchFinally(action(closeable), e => { Log.error(e); null.asInstanceOf[T] }, closeable.close) }
object TryCatchFinally { def apply[T](action: => T, errorAction: Throwable => T, finaly: => Unit): T = new Try[T](action, finaly).Catch(errorAction) }

private class Try[T](action: => T, finaly: => Unit) {
  private val tryResult =
    try {
      val res = action
      Success(res)
    } catch {
      case t: Throwable => Failure(t)
    } finally {
      finaly
    }

  def Catch(errorAction: => T) = tryResult match {
    case Success(res) => res
    case Failure(t)   => errorAction
  }

  def Catch(errorAction: Throwable => T) = tryResult match {
    case Success(res) => res
    case Failure(t)   => errorAction(t)
  }
}