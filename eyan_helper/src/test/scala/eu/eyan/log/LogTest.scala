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
    } ==> OutAndErr("", "")

    collectOutputAndError {
      Log.activateNone
      Log.info("abc")
    } ==> OutAndErr("", "")

    collectOutputAndError {
      Log.activateFatalLevel
      Log.info("abc")
    } ==> OutAndErr("", "")

    collectOutputAndError {
      Log.activateFatalLevel
      Log.fatal("abc")
    } ==> OutAndErr("", "Fatal LogTest.$anonfun$testActivateLevel$4: abc\r\n")

    collectOutputAndError {
      Log.activateInfoLevel
      Log.fatal("a")
      Log.debug("d")
    } ==> OutAndErr("", "Fatal LogTest.$anonfun$testActivateLevel$5: a\r\n")
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

    collectOutputAndError {
      Log.activateAllLevel
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

    collectOutputAndError {
      Log.activateAllLevel
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

    collectOutputAndError {
      Log.activateAllLevel
      Log.error(ex)
    } ==> OutAndErr("", expectedError)
  }

  @Test
  def testError_ExcNoMsg = {
    val ex = new Exception("abc")

    val expectedError = "" +
      "Error LogTest.$anonfun$testError_ExcNoMsg$1:  java.lang.Exception: abc\r\n  " + ex.getStackTrace.mkString("\r\n  ") + "\r\n"

    collectOutputAndError {
      Log.activateAllLevel
      Log.error("", ex)
    } ==> OutAndErr("", expectedError)
  }

  @Test
  def testError_ExcMsg = {
    val ex = new Exception("abc")

    val expectedError = "" +
      "Error LogTest.$anonfun$testError_ExcMsg$1: abc - java.lang.Exception: abc\r\n  " + ex.getStackTrace.mkString("\r\n  ") + "\r\n"

    collectOutputAndError {
      Log.activateAllLevel
      Log.error("abc", ex)
    } ==> OutAndErr("", expectedError)
  }
}