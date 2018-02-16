package eu.eyan.testutil

import org.fest.assertions.Assertions._
import org.junit.Assert

object TestPlus {
  val DEFAULT_WAIT_TIME = 1000
  val DEFAULT_SLEEP_TIME = 10
  def waitFor(assertion: () => Unit, timeout: Long = DEFAULT_WAIT_TIME): Unit = {
    val start = System.currentTimeMillis()
    def elapsedTime = System.currentTimeMillis() - start
    var ok = false
    while (!ok)
      try { assertion(); ok = true }
      catch { case c: AssertionError => if (timeout < elapsedTime) throw c else Thread.sleep(DEFAULT_SLEEP_TIME) }
  }
}

trait TestPlus {
  /** Expect a Throwable of the method */
  def expect(expectedThrowable: Throwable, test: => Unit) = try { test; Assert.fail("Exception(Throwable) was expected but none came.") } catch { case e: Throwable => e ==> ("expectedThrowable", expectedThrowable) }

  implicit class ThrowableTestImplicit[T <: Throwable](expectedThrowable: T) {
    /** Expect throwable(left) of the right*/
    def <==(actual: => Unit) = expect(expectedThrowable, actual)
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
  }
}