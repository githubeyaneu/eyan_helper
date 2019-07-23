package eu.eyan.testutil

import org.fest.assertions.Assertions._
import org.junit.Assert
import eu.eyan.util.io.OutputStreamPlus
import eu.eyan.util.io.PrintStreamPlus
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import eu.eyan.util.time.TimeCounter
import org.fest.swing.exception.ComponentLookupException
import eu.eyan.util.scala.Try
import eu.eyan.util.scala.TryCatch
import org.mockito.Mockito
import scala.reflect.ClassTag
import org.mockito.ArgumentCaptor
import org.mockito.stubbing.OngoingStubbing
import org.mockito.MockSettings
import org.mockito.internal.matchers.CapturingMatcher
import org.mockito.internal.matchers.CapturingMatcher
import org.mockito.internal.util.Primitives
import org.mockito.ArgumentMatchers
import org.fest.assertions.ObjectAssert
import eu.eyan.log.Log
import org.fest.swing.timing.Pause

object TestPlus {

  val DEFAULT_WAIT_TIME = 1500

  val DEFAULT_SLEEP_TIME = 100

  def waitFor[T](assertion: => T, timeout: Long = DEFAULT_WAIT_TIME): T = {
    val start = System.currentTimeMillis()
    def elapsedTime = System.currentTimeMillis() - start
    var ok = false
    var res: T = null.asInstanceOf[T]
    def checkTimeout(t: Throwable) = if (timeout < elapsedTime) throw t else Thread.sleep(DEFAULT_SLEEP_TIME)
    while (!ok)
      try {
        Log.debug("try")
        res = assertion
        ok = true 
        }
      catch {
        case t: AssertionError           => checkTimeout(t)
        case t: ComponentLookupException => checkTimeout(t)
      }
    res
  }
}

trait TestPlus {
  def pause(ms: Long) = Pause.pause(ms)
    
  def waitFor[T](assertion: => T, timeout: Long = TestPlus.DEFAULT_WAIT_TIME): T = TestPlus.waitFor(assertion, timeout)

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

  /** Expect a Throwable type of the method */
  def expectThrowable(expectedThrowableClass: Class[_], test: => Unit) =
    try { test; Assert.fail("Exception(Throwable) was expected but none came.") }
    catch {
      case e: Throwable =>{
        if(e.getClass != expectedThrowableClass) e.printStackTrace
    	  e.getClass ==> ("expectedThrowable class", expectedThrowableClass)
      }
    }

  /**
   * Expect no Throwable of the method: remember this makes no sense, as the exception thrown makes a junit test failed.
   *  However it is nicer to read what the test about and if it is deeper tested in an other try...
   */
  def expectNoException(test: => Unit) = try { test } catch { case e: Throwable => Assert.fail("Exception(Throwable) was NOT expected but came: " + e) }

  implicit class IntTestImpicit[T <: Int](actual: T) {
    def shouldBeLessThan(msg: String, other: Int) = assertThat(actual).as(msg).isLessThan(other)
    def shouldBeMoreThan(msg: String, other: Int) = assertThat(actual).as(msg).isGreaterThan(other)
  }

  implicit class LongTestImpicit[T <: Long](actual: T) {
    def shouldBeLessThan(msg: String, other: Long) = assertThat(actual).as(msg).isLessThan(other)
    def shouldBeMoreThan(msg: String, other: Long) = assertThat(actual).as(msg).isGreaterThan(other)
  }

  implicit class StringTestImpicit[T <: String](actual: T) {
    def shouldStartWith(expectedStart: String) = assertThat(actual).startsWith(expectedStart)
    def shouldContain(expectedContains: String) = assertThat(actual).contains(expectedContains)
  }

  implicit class TestPlusImplicitAssert[A](actual: => A) {
    def shouldBeNull = shouldBe(null)
    def shouldBe(expected: Any) = ==>(expected)
    def ==>(expected: Any): A = ==>("", expected)
    def ===(expected: Any): A = ==>("", expected)
    def ==>(descriptionAndExpected: Tuple2[String, Any]): A = {
      val description = descriptionAndExpected._1
      val expected= descriptionAndExpected._2
      expected match {
        case t:Throwable => {expect(t, actual); null.asInstanceOf[A]} 
        case _ => {val a = actual; assertThat(a).as(description).isEqualTo(expected); a}
      }
    }
  }

  implicit class ThrowableTestImplicit[T <: Throwable](expectedThrowable: T) {
    /** Expect throwable(left) from the actual (right)*/
//    def <==(actual: => Unit) = expect(expectedThrowable, actual)
  }

  implicit class AnyTestImpicit[T <: Any](actual: T) {
    /** assertThat(left).isEqualTo(right) */
    //    def ==>(expected: Any) = assertThat(actual).isEqualTo(expected)
    /** assertThat(left).as(right_1).isEqualTo(right_2) */
    //	  def ==>(expected: Tuple2[String, Any]) = assertThat(actual).as(expected._1).isEqualTo(expected._2)

    //		def <==(action: => Unit) = action ==> actual // why it does not work??

    /** assertThat(left).isNotEqualTo(right) */
    def !==(expected: Any) = assertThat(actual).isNotEqualTo(expected)

    def setFieldValue(field: String, value: Object) = reflectionSetFieldValue(actual.asInstanceOf[Object], field, value)

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
    Console.withOut(newConsoleOut)(action)
    System.setOut(originalSystemOut)

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
    Console.withErr(newConsoleErr)(action)
    System.setErr(originalSystemErr)

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

  def shouldBeFaster(expectedMaxMillisecs: Int, msg: String = "execution time")(action: => Unit) = {
    val executionTime = TimeCounter.millisecsOf(action)
    println(s"$msg $executionTime ms")
    executionTime shouldBeLessThan (msg, expectedMaxMillisecs)
    executionTime.toInt
  }

  def reflectionSetFieldValue(objekt: Object, fieldName: String, value: Object, klazz: Class[_] = null): Boolean = {
    if (objekt == null) false
    else {
      import scala.language.existentials
      val klass = if (klazz == null) objekt.getClass else klazz
      val field = klass.getDeclaredField(fieldName)
      if (field == null) {
        val zuper = klass.getSuperclass
        if (zuper != null) reflectionSetFieldValue(objekt, fieldName, value, zuper)
        else false
      } else
        TryCatch({
          field.setAccessible(true)
          field.set(objekt, value)
          true
        }, false)
    }
  }

  implicit class MockitoTestPlusImplicit[A](mockee: => A) {
    def -->(args: A*) = args.foldLeft(Mockito.when(mockee))((stubbing, nextValue) => stubbing thenReturn nextValue)
  }
  implicit class MockitoTestPlusImplicitAny[T <: Any](actual: T) {
    def verify = Mockito.verify(actual, Mockito.times(1))
  }
  def mock[T](implicit m: Manifest[T]) = Mockito.mock(implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
  def captor[T](actionOnArgument: T => Unit = (x: T) => {})(implicit m: Manifest[T]) = new ArgumentCaptorPlus(implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]], actionOnArgument)
  def capture[T](actionOnArgument: T => Unit = (x: T) => {})(implicit m: Manifest[T]): T = captor[T](actionOnArgument).capture

  class CapturingMatcherPlus[T](actionOnArgument: T => Unit = (x: T) => {}) extends CapturingMatcher[T]() {
    override def captureFrom(argument: Object) = {
      super.captureFrom(argument)
      actionOnArgument(argument.asInstanceOf[T])
    }
  }
  class ArgumentCaptorPlus[T](val clazz: Class[_ <: T], actionOnArgument: T => Unit = (x: T) => {}) {
    val capturingMatcher: CapturingMatcher[T] = new CapturingMatcherPlus(actionOnArgument)
    def capture: T = { ArgumentMatchers.argThat(capturingMatcher); Primitives.defaultValue(clazz) }
    def getValue: T = this.capturingMatcher.getLastValue
    def getAllValues: java.util.List[T] = this.capturingMatcher.getAllValues
  }

}