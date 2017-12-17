package eu.eyan.util.scala

import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunner
import org.junit.Test
import org.fest.assertions.Assertions._
import java.io.Closeable
import org.junit.Rule
import org.junit.internal.runners.statements.ExpectException
import org.junit.rules.ExpectedException
import org.fest.assertions.Assertions
import org.junit.Assert
import scala.util.Success
import scala.util.Failure

@RunWith(classOf[ScalaEclipseJunitRunner])
class TryCatchFinallyTest {

  trait Result
  object ActionResult extends Result
  object CatchResult extends Result
  object CloseableResult extends Result with Closeable { def close = if (closeOk) closeAction else closeError }
  object CloseableActionResult extends Result

  def expect(expectedException: Throwable, test: => Unit) = try { test; Assert.fail("Exception was expected but none came.") } catch { case e: Exception => e ==> ("expectedException", expectedException) }

  implicit class ThrowableTestImplicit[T <: Throwable](expected: T) {
    def <==(actual: => Unit) = expect(expected, actual)
  }

  implicit class AnyTestImpicit[T <: Any](actual: T) {
    def ==>(expected: Any) = assertThat(actual).isEqualTo(expected)
    //		def <==(action: => Unit) = action ==> actual // why it does not work??
    def shouldBe(expected: Any) = ==>(expected)

    def ==>(expected: Tuple2[String, Any]) = assertThat(actual).as(expected._1).isEqualTo(expected._2)
    def !==(expected: Any) = assertThat(actual).isNotEqualTo(expected)
  }

  var actionDone = false
  val Exeption_Action = new Exception("actionError")
  def action = { println("actionDone"); actionDone = true; ActionResult }
  def actionError = { println("actionError"); throw Exeption_Action }

  var catchDone = false
  val Exception_Catch = new Exception("catchError")
  def catcch = (t: Throwable) => { println("catchDone"); catchDone = true; CatchResult }
  def catchError = { println("catchError"); throw Exception_Catch }

  var finallyDone = false
  val Exception_Finally = new Exception("finallyError")
  def finaly = { println("finallyDone"); finallyDone = true; }
  def finallyError = { println("finallyError"); throw Exception_Finally }

  var closeableDone = false
  val Exeption_Closeable = new Exception("closeableError")
  def closeable = { println("closeableDone"); closeableDone = true; CloseableResult }
  def closeableError = { println("closeableError"); throw Exeption_Closeable }

  var closeableActionDone = false
  val Exeption_CloseableAction = new Exception("closeableEctionError")
  def closeableAction = (closeable: Closeable) => { println("closeableActionDone"); closeableActionDone = true; CloseableActionResult }
  def closeableActionError = { println("closeableActionError"); throw Exeption_CloseableAction }

  var closeOk = true
  var closeDone = false
  val Exeption_Close = new Exception("closeError")
  def closeAction = { println("closeDone"); closeableDone = true; }
  def closeError = { println("closeError"); throw Exeption_Close }

  def actionCatchFinally(a: Boolean, c: Boolean, f: Boolean) = {
    actionDone ==> ("actionDone", a)
    catchDone ==> ("catchDone", c)
    finallyDone ==> ("finallyDone", f)
  }

  // Test test :)
  @Test def noException = { action ==> ActionResult; actionDone ==> true }
  @Test def exception__ = { Exeption_Action <== actionError; actionDone ==> false }

  // TryCatchFinally
  @Test def tcf_ActionDone__CatchOk____FinallyDone_ = { TryCatchFinally(action, catcch, finaly) ==> ActionResult; actionCatchFinally(true, false, true) }
  @Test def tcf_ActionDone__CatchOk____FinallyError = { Exception_Finally <== TryCatchFinally(action, catcch, finallyError); actionCatchFinally(true, false, false) }
  @Test def tcf_ActionDone__CatchError_FinallyDone_ = { TryCatchFinally(action, catchError, finaly) ==> ActionResult; actionCatchFinally(true, false, true) }
  @Test def tcf_ActionDone__CatchError_FinallyError = { Exception_Finally <== TryCatchFinally(action, catchError, finallyError); actionCatchFinally(true, false, false) }
  @Test def tcf_ActionError_CatchOk____FinallyDone_ = { TryCatchFinally(actionError, catcch, finaly) ==> CatchResult; actionCatchFinally(false, true, true) }
  @Test def tcf_ActionError_CatchOk____FinallyError = { Exception_Finally <== TryCatchFinally(actionError, catcch, finallyError); actionCatchFinally(false, true, false) }
  @Test def tcf_ActionError_CatchError_FinallyDone_ = { Exception_Catch <== TryCatchFinally(actionError, catchError, finaly); actionCatchFinally(false, false, true) }
  @Test def tcf_ActionError_CatchError_FinallyError = { Exception_Finally <== TryCatchFinally(actionError, catchError, finallyError); actionCatchFinally(false, false, false) }

  // Try
  @Test def try_ActionDone_ = { Try(action) ==> Success(ActionResult); actionDone ==> true }
  @Test def try_ActionError = { Try(actionError) ==> Failure(Exeption_Action); actionDone ==> false }

  def closeableActionCatchClose(a: Boolean, c: Boolean, f: Boolean) = {
    closeableDone ==> ("closeableDone", a)
    closeableActionDone ==> ("closeableActionDone", f)
    catchDone ==> ("catchDone", c)
  }

  // TryCatchFinallyClose
  @Test def tcfc_CloseableOk_ActionOk_CatchOk_CloseOk = { TryCatchFinallyClose(closeable, closeableAction, catcch) ==> CloseableActionResult; closeableActionCatchClose(true, false, true) }
}