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

@RunWith(classOf[ScalaEclipseJunitRunner])
class TryCatchFinallyTest {

  def expect(msg: String)(test: => Unit) =
    try { test; Assert.fail } catch { case t: Throwable => assertThat(t.getMessage).isEqualTo(msg) }

  var actionDone = false
  def action = actionDone = true
  def actionError = throw new Exception("actionError")

  var catchDone = false
  def catcch = (t: Throwable) => catchDone = true
  def catchError = throw new Exception("catchError")

  var finallyDone = false
  def finaly = finallyDone = true
  def finallyError = throw new Exception("finallyError")

  def actionCatchFinally(a: Boolean, c: Boolean, f: Boolean) = {
    assertThat(actionDone).as("actionDone").isEqualTo(a)
    assertThat(catchDone).as("catchDone").isEqualTo(c)
    assertThat(finallyDone).as("finallyDone").isEqualTo(f)
  }

  @Test def noException = { action; assertThat(actionDone).isTrue }
  @Test def exception = { expect("actionError") { actionError }; assertThat(actionDone).isFalse }

  @Test def tcf = { TryCatchFinally(action, catcch, finaly); actionCatchFinally(true, false, true) }
  @Test def tcfActionError = { TryCatchFinally(actionError, catcch, finaly); actionCatchFinally(false, true, true) }
  @Test def tcfCatchError = { expect("catchError") { TryCatchFinally(action, catchError, finaly) }; actionCatchFinally(true, false, true) }
  @Test def tcfFinallyError = { TryCatchFinally(action, catcch, finallyError); actionCatchFinally(false, true, true) }

}