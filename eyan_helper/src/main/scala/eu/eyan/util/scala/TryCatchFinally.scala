package eu.eyan.util.scala

import scala.util.Failure
import scala.util.Success
import java.io.Closeable
import eu.eyan.log.Log
import eu.eyan.util.io.CloseablePlus

object Try { def apply[T](action: => T) = TryFinally[T](action, {}) }
object CloseFinally { def apply[T, CLOSEABLE <: Closeable](closeable: CLOSEABLE, action: CLOSEABLE => T) = FinallyClose(closeable, action) }
object FinallyClose { def apply[T, CLOSEABLE <: Closeable](closeable: CLOSEABLE, action: CLOSEABLE => T) = TryFinally(action(closeable), CloseablePlus.closeQuietly(closeable)) }
object TryFinally { def apply[T](action: => T, finaly: => Unit) = TryCatchFinally[scala.util.Try[T]](Success(action), e => { Log.error(e); Failure(e) }, finaly) }

object TryCatch { def apply[T](action: => T, errorAction: Throwable => T) = TryCatchFinally[T](action, errorAction, {}) }
object TryCatchFinally { def apply[T](action: => T, errorAction: Throwable => T, finaly: => Unit) = new TryCatchFinally[T](action, finaly).Catch(errorAction) }

private class TryCatchFinally[T](action: => T, finaly: => Unit) {
  private val tryResult =
    try Success(action)
    catch { case t: Throwable => Failure(t) }
    finally finaly

  private def Catch(errorAction: => T) = tryResult match {
    case Success(res) => res
    case Failure(t)   => errorAction
  }

  private def Catch(errorAction: Throwable => T) = tryResult match {
    case Success(res) => res
    case Failure(t)   => errorAction(t)
  }
}