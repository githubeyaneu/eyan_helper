package eu.eyan.util.scala

import scala.util.Failure
import scala.util.Success

object Try {
  def apply[T](action: => T) = new Try[T](action, {})
}

object TryFinally {
  def apply[T](action: => T, finaly: => Unit) = new Try[T](action, finaly)
}

object TryCatch {
//  def apply[T](action: => T, errorAction: => T) = new Try[T](action, {}).Catch(errorAction)
  def apply[T](action: => T, errorAction: Throwable => T) = new Try[T](action, {}).Catch(errorAction)
}

object TryCatchFinally {
  def apply[T](action: => T, errorAction: Throwable => T, finaly: => Unit) = new Try[T](action, finaly).Catch(errorAction)
}

class Try[T](action: => T, finaly: => Unit) {
  val tryResult = try {
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