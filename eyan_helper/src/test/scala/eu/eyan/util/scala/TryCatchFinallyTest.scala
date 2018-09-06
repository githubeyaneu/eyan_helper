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
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import org.junit.experimental.theories.Theory
import eu.eyan.testutil.TestPlus

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class TryCatchFinallyTest extends TestPlus {

  trait Result
  object ActionResult extends Result
  object CatchResult extends Result
  object Closeable1Result extends Result with Closeable { def close = if (close1Ok) close1Action else close1Error }
  object Closeable1ActionResult extends Result
  object Closeable2Result extends Result with Closeable { def close = if (close2Ok) close2Action else close2Error }
  object Closeable2ActionResult extends Result

  var actionDone = false
  val Exeption_Action = new Exception("actionError")
  def action = { println("actionDone"); actionDone = true; ActionResult }
  def actionError = { println("actionError"); throw Exeption_Action; actionDone = true; ActionResult }

  // Test test :)
  @Test def noException = { action ==> ActionResult; actionDone ==> true }
  @Test def exception__ = { actionError ==> Exeption_Action; actionDone ==> false }

  // Tests TryCatchFinally
  var catchDone = false
  val Exception_Catch = new Exception("catchError")
  def catcch = (t: Throwable) => { println("catchDone"); catchDone = true; CatchResult }
  def catchError = (t: Throwable) => { println("catchError"); throw Exception_Catch; catchDone = true; CatchResult }

  var finallyDone = false
  val Exception_Finally = new Exception("finallyError")
  def finaly = { println("finallyDone"); finallyDone = true; }
  def finallyError = { println("finallyError"); throw Exception_Finally; finallyDone = true }

  def actionCatchFinally(a: Boolean, c: Boolean, f: Boolean) = {
    actionDone ==> ("actionDone", a)
    catchDone ==> ("catchDone", c)
    finallyDone ==> ("finallyDone", f)
  }
  @Test def tcf_ActionOk__CatchOk__FinallyOk_ = { TryCatchFinally(action, catcch, finaly) ==> ActionResult; actionCatchFinally(true, false, true) }
  @Test def tcf_ActionOk__CatchOk__FinallyErr = { TryCatchFinally(action, catcch, finallyError)==> Exception_Finally; actionCatchFinally(true, false, false) }
  @Test def tcf_ActionOk__CatchErr_FinallyOk_ = { TryCatchFinally(action, catchError, finaly) ==> ActionResult; actionCatchFinally(true, false, true) }
  @Test def tcf_ActionOk__CatchErr_FinallyErr = {  TryCatchFinally(action, catchError, finallyError)==>Exception_Finally; actionCatchFinally(true, false, false) }
  @Test def tcf_ActionErr_CatchOk__FinallyOk_ = { TryCatchFinally(actionError, catcch, finaly) ==> CatchResult; actionCatchFinally(false, true, true) }
  @Test def tcf_ActionErr_CatchOk__FinallyErr = { TryCatchFinally(actionError, catcch, finallyError)==>Exception_Finally; actionCatchFinally(false, true, false) }
  @Test def tcf_ActionErr_CatchErr_FinallyOk_ = { TryCatchFinally(actionError, catchError, finaly) ==> Exception_Catch; actionCatchFinally(false, false, true) }
  @Test def tcf_ActionErr_CatchErr_FinallyErr = { TryCatchFinally(actionError, catchError, finallyError)==>Exception_Finally; actionCatchFinally(false, false, false) }

  @Theory def tcf(actionOk: Boolean, catchOk: Boolean, finallyOk: Boolean) = {
    def testCall = TryCatchFinally(
      if (actionOk) action else actionError,
      if (catchOk) catcch else catchError,
      if (finallyOk) finaly else finallyError)

    if (!finallyOk) testCall ==> Exception_Finally
    else if (!actionOk && !catchOk) testCall ==> Exception_Catch
    else if (actionOk) testCall ==> ActionResult
    else testCall ==> CatchResult

    actionDone ==> ("actionDone", actionOk)
    catchDone ==> ("catchDone", !actionOk && catchOk)
    finallyDone ==> ("finallyDone", finallyOk)
  }

  // Tests for Try
  @Test def try_ActionDone_ = { Try(action) ==> Success(ActionResult); actionDone ==> true }
  @Test def try_ActionError = { Try(actionError) ==> Failure(Exeption_Action); actionDone ==> false }

  // Tests for TryCatchFinallyClose
  var closeable1Done = false
  val Exeption_Closeable1 = new Exception("closeable1Error")
  def closeable1 = { println("closeable1Done"); closeable1Done = true; Closeable1Result }
  def closeable1Error = { println("closeable1Error"); throw Exeption_Closeable1; closeable1Done = true; Closeable1Result }

  var closeable1ActionDone = false
  val Exeption_Closeable1Action = new Exception("closeable1EctionError")
  def closeable1Action = (closeable1: Closeable) => { println("closeable1ActionDone"); closeable1ActionDone = true; Closeable1ActionResult }
  def closeable1ActionError = (closeable1: Closeable) => { println("closeable1ActionError"); throw Exeption_Closeable1Action; closeable1ActionDone = true; Closeable1ActionResult }

  var close1Ok = true
  var close1Done = false
  val Exeption_Close1 = new Exception("close1Error")
  def close1Action = { println("close1Done"); close1Done = true; }
  def close1Error = { println("close1Error"); throw Exeption_Close1; close1Done = true; }

  def closeableActionCatchClose(closeableExecuted: Boolean, closeableActionExecuted: Boolean, catchExecuted: Boolean, closeExecuted: Boolean) = {
    closeable1Done ==> ("closeableExecuted", closeableExecuted)
    closeable1ActionDone ==> ("closeableActionExecuted", closeableActionExecuted)
    catchDone ==> ("catchExecuted", catchExecuted)
    close1Done ==> ("closeExecuted", closeExecuted)
  }

  @Test def tcfc_CloseableOk__ActionOk__CatchOk__CloseOk_ = { TryCatchFinallyClose(closeable1, closeable1Action, catcch) ==> Closeable1ActionResult; closeableActionCatchClose(true, true, false, true) }
  @Test def tcfc_CloseableOk__ActionOk__CatchErr_CloseOk_ = { TryCatchFinallyClose(closeable1, closeable1Action, catchError) ==> Closeable1ActionResult; closeableActionCatchClose(true, true, false, true) }
  @Test def tcfc_CloseableOk__ActionErr_CatchOk__CloseOk_ = { TryCatchFinallyClose(closeable1, closeable1ActionError, catcch) ==> CatchResult; closeableActionCatchClose(true, false, true, true) }
  @Test def tcfc_CloseableOk__ActionErr_CatchErr_CloseOk_ = { TryCatchFinallyClose(closeable1, closeable1ActionError, catchError)==> Exception_Catch; closeableActionCatchClose(true, false, false, true) }
  @Test def tcfc_CloseableErr_ActionOk__CatchOk__CloseOk_ = { TryCatchFinallyClose(closeable1Error, closeable1Action, catcch) ==> CatchResult; closeableActionCatchClose(false, false, true, false) }
  @Test def tcfc_CloseableErr_ActionOk__CatchErr_CloseOk_ = { TryCatchFinallyClose(closeable1Error, closeable1Action, catchError)==>Exception_Catch; closeableActionCatchClose(false, false, false, false) }
  @Test def tcfc_CloseableErr_ActionErr_CatchOk__CloseOk_ = { TryCatchFinallyClose(closeable1Error, closeable1ActionError, catcch) ==> CatchResult; closeableActionCatchClose(false, false, true, false) }
  @Test def tcfc_CloseableErr_ActionErr_CatchErr_CloseOk_ = { TryCatchFinallyClose(closeable1Error, closeable1ActionError, catchError)==> Exception_Catch; closeableActionCatchClose(false, false, false, false) }
  @Test def tcfc_CloseableOk__ActionOk__CatchOk__CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable1Action, catcch) ==> Closeable1ActionResult; closeableActionCatchClose(true, true, false, false) }
  @Test def tcfc_CloseableOk__ActionOk__CatchErr_CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable1Action, catchError) ==> Closeable1ActionResult; closeableActionCatchClose(true, true, false, false) }
  @Test def tcfc_CloseableOk__ActionErr_CatchOk__CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable1ActionError, catcch) ==> CatchResult; closeableActionCatchClose(true, false, true, false) }
  @Test def tcfc_CloseableOk__ActionErr_CatchErr_CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable1ActionError, catchError)==>Exception_Catch; closeableActionCatchClose(true, false, false, false) }
  @Test def tcfc_CloseableErr_ActionOk__CatchOk__CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable1Action, catcch) ==> CatchResult; closeableActionCatchClose(false, false, true, false) }
  @Test def tcfc_CloseableErr_ActionOk__CatchErr_CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable1Action, catchError)==>Exception_Catch; closeableActionCatchClose(false, false, false, false) }
  @Test def tcfc_CloseableErr_ActionErr_CatchOk__CloseErr = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable1ActionError, catcch) ==> CatchResult; closeableActionCatchClose(false, false, true, false) }
  @Test def tcfc_CloseableErr_ActionErr_CatchErr_CloseErr = { close1Ok = false;  TryCatchFinallyClose(closeable1Error, closeable1ActionError, catchError)==>Exception_Catch; closeableActionCatchClose(false, false, false, false) }

  @Theory def closables(closeableOk: Boolean, actionOk: Boolean, catchOk: Boolean, _closeOk: Boolean) = {
    close1Ok = _closeOk;
    def testCall = TryCatchFinallyClose(
      if (closeableOk) closeable1 else closeable1Error,
      if (actionOk) closeable1Action else closeable1ActionError,
      if (catchOk) catcch else catchError)

    if (!catchOk && (!closeableOk || !actionOk))testCall==>Exception_Catch
    else if (catchOk && (!closeableOk || !actionOk)) testCall ==> ("result should be CatchResult", CatchResult)
    else testCall ==> ("result should be Closeable1ActionResult", Closeable1ActionResult)

    closeable1Done ==> ("closeableExecuted", closeableOk)
    closeable1ActionDone ==> ("closeableActionExecuted", closeableOk && actionOk)
    catchDone ==> ("catchExecuted", catchOk && (!closeableOk || !actionOk))
    close1Done ==> ("closeExecuted", closeableOk && _closeOk)
  }

  // Tests for TryCatchFinallyClose2 2^6 = 64 testcases :D yipeee
  var closeable2Done = false
  val Exeption_Closeable2 = new Exception("closeable2Error")
  def closeable2 = { println("closeable2Done"); closeable2Done = true; Closeable2Result }
  def closeable2Error = { println("closeable2Error"); throw Exeption_Closeable2; closeable2Done = true; Closeable2Result }

  var closeable2ActionDone = false
  val Exeption_Closeable2Action = new Exception("closeable2EctionError")
  def closeable2Action = (closable1: Closeable, closeable2: Closeable) => { println("closeable2ActionDone"); closeable2ActionDone = true; Closeable2ActionResult }
  def closeable2ActionError = (closable1: Closeable, closeable2: Closeable) => { println("closeable2ActionError"); throw Exeption_Closeable2Action; closeable2ActionDone = true; Closeable2ActionResult }

  var close2Ok = true
  var close2Done = false
  val Exeption_Close2 = new Exception("close2Error")
  def close2Action = { println("close2Done"); close2Done = true; }
  def close2Error = { println("close2Error"); throw Exeption_Close2; close2Done = true; }

  def checkClose2(closeable1Executed: Boolean, closeable2Executed: Boolean, closeable2ActionExecuted: Boolean, catchExecuted: Boolean, close1Executed: Boolean, close2Executed: Boolean) = {
    closeable1Done ==> ("closeable1Executed", closeable1Executed)
    closeable2Done ==> ("closeable2Executed", closeable2Executed)
    closeable2ActionDone ==> ("closeable2ActionExecuted", closeable2ActionExecuted)
    catchDone ==> ("catchExecuted", catchExecuted)
    close1Done ==> ("close1Executed", close1Executed)
    close2Done ==> ("close2Executed", close2Executed)
  }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catcch) ==> Closeable2ActionResult; checkClose2(true, true, true, false, true, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catchError) ==> Closeable2ActionResult; checkClose2(true, true, true, false, true, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, true, false, true, true, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, true, false, false, true, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(true, false, false, true, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(true, false, false, false, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, false, false, true, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, false, false, false, true, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchOk__Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchErr_Close1Ok__Close2Ok_ = { TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }

  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catcch) ==> Closeable2ActionResult; checkClose2(true, true, true, false, false, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catchError) ==> Closeable2ActionResult; checkClose2(true, true, true, false, false, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, true, false, true, false, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, true, false, false, false, true) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(true, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(true, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchOk__Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchErr_Close1Err_Close2Ok_ = { close1Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }

  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catcch) ==> Closeable2ActionResult; checkClose2(true, true, true, false, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catchError) ==> Closeable2ActionResult; checkClose2(true, true, true, false, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, true, false, true, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, true, false, false, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(true, false, false, true, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(true, false, false, false, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, false, false, true, true, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, false, false, false, true, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchOk__Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchErr_Close1Ok__Close2Err = { close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }

  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catcch) ==> Closeable2ActionResult; checkClose2(true, true, true, false, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionOk__CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2Action, catchError) ==> Closeable2ActionResult; checkClose2(true, true, true, false, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, true, false, true, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Ok__ActionErr_CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, true, false, false, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(true, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionOk__CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(true, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(true, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Ok__Closeable2Err_ActionErr_CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(true, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionOk__CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Ok__ActionErr_CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionOk__CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2Action, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchOk__Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catcch) ==> CatchResult; checkClose2(false, false, false, true, false, false) }
  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchErr_Close1Err_Close2Err = { close1Ok = false; close2Ok = false; TryCatchFinallyClose(closeable1Error, closeable2Error, closeable2ActionError, catchError)==>Exception_Catch; checkClose2(false, false, false, false, false, false) }

  @Theory def closables2(closeable1Ok: Boolean, closeable2Ok: Boolean, actionOk: Boolean, catchOk: Boolean, _close1Ok: Boolean, _close2Ok: Boolean) = {
    close1Ok = _close1Ok;
    close2Ok = _close2Ok;
    def testCall = TryCatchFinallyClose(
      if (closeable1Ok) closeable1 else closeable1Error,
      if (closeable2Ok) closeable2 else closeable2Error,
      if (actionOk) closeable2Action else closeable2ActionError,
      if (catchOk) catcch else catchError)

    if (!catchOk && (!closeable1Ok || !closeable2Ok || !actionOk)) testCall==>Exception_Catch
    else if (catchOk && (!closeable1Ok || !closeable2Ok || !actionOk)) testCall ==> ("result should be CatchResult", CatchResult)
    else testCall ==> ("result should be Closeable2ActionResult", Closeable2ActionResult)

    closeable1Done ==> ("closeable1Executed", closeable1Ok)
    closeable2Done ==> ("closeable2Executed", closeable1Ok && closeable2Ok)
    closeable2ActionDone ==> ("closeable2ActionExecuted", closeable1Ok && closeable2Ok && actionOk)
    catchDone ==> ("catchExecuted", catchOk && (!closeable1Ok || !closeable2Ok || !actionOk))
    close1Done ==> ("close1Executed", closeable1Ok && _close1Ok)
    close2Done ==> ("close2Executed", closeable1Ok && closeable2Ok && _close2Ok)
  }
}