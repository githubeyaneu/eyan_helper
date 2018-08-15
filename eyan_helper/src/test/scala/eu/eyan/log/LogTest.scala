package eu.eyan.log

import org.junit.runner.RunWith
import org.junit.Test
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import eu.eyan.testutil.TestPlus
import eu.eyan.util.io.PrintStreamPlus
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import eu.eyan.util.io.OutputStreamPlus
import scala.collection.mutable.MutableList
import eu.eyan.util.string.StringPlus.StringPlusImplicit

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class LogTest extends TestPlus {

  //  @Test //test the tester...
  def testOut = {
    collectOutputError {
      collectOutput {
        print("D")
        System.out.print("E")
        System.err.println("X")
        println("F")
        System.err.print("Y")
        System.out.println("G")
      } ==> ("Expected system out", "DEF\r\nG\r\n")
    } ==> ("Expected system err", "X\r\nY")
  }

  @Test
  def testActivateLevel = {
    collectOutputAndError {
      Log.activateTraceLevel
    } ==> OutAndErr("Info  LogTest.$anonfun$testActivateLevel$1: activating log level: Trace\r\nInfo  LogTest.$anonfun$testActivateLevel$1: activated log level: Trace\r\n", "")

    Log.activateNone
    collectOutputAndError {
      Log.info("abc")
    } ==> OutAndErr("", "")

    Log.activateFatalLevel
    collectOutputAndError {
      Log.info("abc")
    } ==> OutAndErr("", "")

    Log.activateFatalLevel
    collectOutputAndError {
      Log.fatal("abc")
    } ==> OutAndErr("", "Fatal LogTest.$anonfun$testActivateLevel$6: abc\r\n")

    Log.activateInfoLevel
    collectOutputAndError {
      Log.fatal("a")
      Log.debug("d")
    } ==> OutAndErr("", "Fatal LogTest.$anonfun$testActivateLevel$8: a\r\n")
  }

  @Test
  def testAllLevel = {
    val expectedError = "" +
      "Fatal LogTest.$anonfun$testAllLevel$1: f\r\n" +
      "Error LogTest.$anonfun$testAllLevel$1: e\r\n"

    val expectedOutput = "" +
      "Warn  LogTest.$anonfun$testAllLevel$1: w\r\n" +
      "Info  LogTest.$anonfun$testAllLevel$1: i\r\n" +
      "Debug LogTest.$anonfun$testAllLevel$1: d\r\n" +
      "Trace LogTest.$anonfun$testAllLevel$1: t\r\n"

    Log.activateAllLevel
    collectOutputAndError {
      Log.fatal("f")
      Log.error("e")
      Log.warn("w")
      Log.info("i")
      Log.debug("d")
      Log.trace("t")
    } ==> OutAndErr(expectedOutput, expectedError)
  }

  @Test
  def testAllLevel_NoText = {
    val expectedError = "" +
      "Fatal LogTest.$anonfun$testAllLevel_NoText$1\r\n" +
      "Error LogTest.$anonfun$testAllLevel_NoText$1\r\n"

    val expectedOutput = "" +
      "Warn  LogTest.$anonfun$testAllLevel_NoText$1\r\n" +
      "Info  LogTest.$anonfun$testAllLevel_NoText$1\r\n" +
      "Debug LogTest.$anonfun$testAllLevel_NoText$1\r\n" +
      "Trace LogTest.$anonfun$testAllLevel_NoText$1\r\n"

    Log.activateAllLevel
    collectOutputAndError {
      Log.fatal
      Log.error
      Log.warn
      Log.info
      Log.debug
      Log.trace
    } ==> OutAndErr(expectedOutput, expectedError)
  }

  @Test
  def testErrorException = {
    val ex = new Exception("abc")

    val expectedError = "" +
      "Error LogTest.$anonfun$testErrorException$1:  java.lang.Exception: abc\r\n  " + ex.getStackTrace.mkString("\r\n  ") + "\r\n"

    Log.activateAllLevel
    collectOutputAndError {
      Log.error(ex)
    } ==> OutAndErr("", expectedError)
  }

  @Test
  def testError_ExcNoMsg = {
    val ex = new Exception("abc")

    val expectedError = "" +
      "Error LogTest.$anonfun$testError_ExcNoMsg$1:  java.lang.Exception: abc\r\n  " + ex.getStackTrace.mkString("\r\n  ") + "\r\n"

    Log.activateAllLevel
    collectOutputAndError {
      Log.error("", ex)
    } ==> OutAndErr("", expectedError)
  }

  @Test
  def testError_ExcMsg = {
    val ex = new Exception("abc")

    val expectedError = "" +
      "Error LogTest.$anonfun$testError_ExcMsg$1: abc - java.lang.Exception: abc\r\n  " + ex.getStackTrace.mkString("\r\n  ") + "\r\n"

    Log.activateAllLevel
    collectOutputAndError {
      Log.error("abc", ex)
    } ==> OutAndErr("", expectedError)
  }

  @Test
  def testNoLogExecutionOnNone = {
    var counter = 0

    Log.activateNone

    Log.fatal({ counter += 1; "log" })
    counter ==> 0
    Log.error({ counter += 1; "log" })
    counter ==> 0
    Log.error({ counter += 1; "log" }, new Exception)
    counter ==> 0
    Log.warn({ counter += 1; "log" })
    counter ==> 0
    Log.info({ counter += 1; "log" })
    counter ==> 0
    Log.debug({ counter += 1; "log" })
    counter ==> 0
    Log.trace({ counter += 1; "log" })
    counter ==> 0
  }

  @Test
  def testLogExecutionOnce = {
    var counter = 0

    Log.activateAllLevel

    Log.fatal({ counter += 1; "log" })
    counter ==> 1
    Log.error({ counter += 1; "log" })
    counter ==> 2
    Log.error({ counter += 1; "log" }, new Exception)
    counter ==> 3
    Log.warn({ counter += 1; "log" })
    counter ==> 4
    Log.info({ counter += 1; "log" })
    counter ==> 5
    Log.debug({ counter += 1; "log" })
    counter ==> 6
    Log.trace({ counter += 1; "log" })
    counter ==> 7
    Log.error("log", { counter += 1; new Exception })
    counter ==> 8
  }
}