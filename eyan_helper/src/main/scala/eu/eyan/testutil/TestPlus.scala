package eu.eyan.testutil

import org.fest.assertions.Assertions._
import org.junit.Assert
import eu.eyan.util.io.OutputStreamPlus
import eu.eyan.util.io.PrintStreamPlus
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import eu.eyan.util.time.TimeCounter

object TestPlus {

  val DEFAULT_WAIT_TIME = 1000

  val DEFAULT_SLEEP_TIME = 10

  def waitFor(assertion: => Unit, timeout: Long = DEFAULT_WAIT_TIME): Unit = {
    val start = System.currentTimeMillis()
    def elapsedTime = System.currentTimeMillis() - start
    var ok = false
    while (!ok)
      try { assertion; ok = true }
      catch { case c: AssertionError => if (timeout < elapsedTime) throw c else Thread.sleep(DEFAULT_SLEEP_TIME) }
  }
}

trait TestPlus {
  /** Expect a Throwable of the method */
  def expect(expectedThrowable: Throwable, test: => Unit, same: Boolean = false) =
    try { test; Assert.fail("Exception(Throwable) was expected but none came.") }
    catch {
      case e: Throwable => {
        if (same) e ==> ("expectedThrowable", expectedThrowable)
        e.getClass ==> ("expectedThrowable class", expectedThrowable.getClass)
        e.getMessage ==> ("expectedThrowable message", expectedThrowable.getMessage)
      }
    }

  /**
   * Expect no Throwable of the method: remember this makes no sense, as the exception thrown makes a junit test failed.
   *  However it is nicer to read what the test about and if it is deeper tested in an other try...
   */
  def expectNoException(test: => Unit) = try { test } catch { case e: Throwable => Assert.fail("Exception(Throwable) was NOT expected but came: " + e) }

  implicit class ThrowableTestImplicit[T <: Throwable](expectedThrowable: T) {
    /** Expect throwable(left) from the actual (right)*/
    def <==(actual: => Unit) = expect(expectedThrowable, actual)
  }

  implicit class IntTestImpicit[T <: Int](actual: T) {
    def shouldBeLessThan(msg: String, other: Int) = assertThat(actual).as(msg).isLessThan(other)
    def shouldBeMoreThan(msg: String, other: Int) = assertThat(actual).as(msg).isGreaterThan(other)
  }

  implicit class LongTestImpicit[T <: Long](actual: T) {
    def shouldBeLessThan(msg: String, other: Long) = assertThat(actual).as(msg).isLessThan(other)
    def shouldBeMoreThan(msg: String, other: Long) = assertThat(actual).as(msg).isGreaterThan(other)
  }

  implicit class AnyTestImpicit[T <: Any](actual: T) {
    /** assertThat(left).isEqualTo(right) */
    def ==>(expected: Any) = assertThat(actual).isEqualTo(expected)

    /** assertThat(left).isEqualTo(right) */
    def shouldBe(expected: Any) = ==>(expected)

    //		def <==(action: => Unit) = action ==> actual // why it does not work??

    /** assertThat(left).as(right_1).isEqualTo(right_2) */
    def ==>(expected: Tuple2[String, Any]) = assertThat(actual).as(expected._1).isEqualTo(expected._2)

    /** assertThat(left).isNotEqualTo(right) */
    def !==(expected: Any) = assertThat(actual).isNotEqualTo(expected)

    def shouldBeNull = shouldBe(null)
  }

  def collectOutput(action: => Unit) = {

    var output = ""
    def OutputStreamToString = {
      def newChar(i: Int) = output += i.toChar
      OutputStreamPlus(newChar, {}, {})
    }

    val originalSystemOut = System.out
    val originalConsoleOut = Console.out

    val newOutputStream = OutputStreamToString

    val newSystemOut = originalSystemOut.copyToStream(newOutputStream)
    val newConsoleOut = originalConsoleOut.copyToStream(newOutputStream)

    System.setOut(newSystemOut)
    Console.setOut(newConsoleOut)

    action

    System.setOut(originalSystemOut)
    Console.setOut(originalConsoleOut)

    output
  }

  def collectOutputError(action: => Unit) = {

    //TODO code duplicate
    var output = ""
    def OutputStreamToString = {
      def newChar(i: Int) = output += i.toChar
      OutputStreamPlus(newChar, {}, {})
    }

    val originalSystemErr = System.err
    val originalConsoleErr = Console.err

    val newOutputStream = OutputStreamToString

    val newSystemErr = originalSystemErr.copyToStream(newOutputStream)
    val newConsoleErr = originalConsoleErr.copyToStream(newOutputStream)

    System.setErr(newSystemErr)
    Console.setErr(newConsoleErr)

    action

    System.setErr(originalSystemErr)
    Console.setErr(originalConsoleErr)

    output
  }

  def collectOutputAndError(action: => Unit) = {
    var out = ""
    val err = collectOutputError {
      out = collectOutput { action }
    }
    OutAndErr(out, err)
  }

  case class OutAndErr(output: String, error: String)

  def shouldBeFaster(expectedMaxMillisecs: Int, msg:String="execution time")(action: => Unit) = {
    val executionTime = TimeCounter.millisecsOf(action)
    println(s"$msg $executionTime ms")
    executionTime shouldBeLessThan(msg, expectedMaxMillisecs)
    executionTime.toInt
  }
}