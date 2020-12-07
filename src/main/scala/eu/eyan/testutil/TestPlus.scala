package eu.eyan.testutil

import java.io.{BufferedReader, InputStreamReader}
import java.net.{HttpURLConnection, URL}
import java.util.Scanner
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicReference}
import java.util.stream.Collectors

import eu.eyan.log.Log
import eu.eyan.testutil.TestPlus.DEFAULT_WAIT_TIME
import eu.eyan.util.io.{InputStreamPlus, OutputStreamPlus}
import eu.eyan.util.io.InputStreamPlus.InputStreamPlusImplicit
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import eu.eyan.util.scala.{TryCatch, TryFinallyClose}
import eu.eyan.util.time.TimeCounter
import org.fest.assertions.Assertions._
import org.fest.swing.exception.ComponentLookupException
import org.fest.swing.timing.Pause
import org.junit.Assert
import org.mockito.internal.matchers.CapturingMatcher
import org.mockito.internal.util.Primitives
import org.mockito.{ArgumentMatchers, Mockito}
import rx.internal.observers.AssertableSubscriberObservable
import rx.lang.scala.Observable
import rx.observers.AssertableSubscriber

import scala.io.{BufferedSource, Source}
import scala.reflect.ClassTag
import scala.util.{Failure, Try}

object TestPlus {
  val DEFAULT_WAIT_TIME = 1500
}

trait TestPlus {
  def pause(ms: Long) = Pause.pause(ms)

  def waitFor[T](assertion: => T, timeout: Long = DEFAULT_WAIT_TIME): T = {
    val start = System.currentTimeMillis()

    def elapsedTime = System.currentTimeMillis() - start

    val ok = new AtomicBoolean(false)
    val retryCounter = new AtomicInteger(-1)
    val res = new AtomicReference[T](null.asInstanceOf[T])

    def fibs: Stream[Int] = 0 #:: 1 #:: (fibs zip fibs.tail).map { t => t._1 + t._2 } //TODO not effective...
    val sleepTimeIterator = fibs.toIterator

    def checkTimeout(t: Throwable) = if (timeout < elapsedTime) throw t else Thread.sleep(sleepTimeIterator.next())

    def stackTraceToString(stack: StackTraceElement) = {
      if (stack.isNativeMethod) "(Native Method)"
      else if (stack.getFileName != null && stack.getLineNumber >= 0) "(" + stack.getFileName + ":" + stack.getLineNumber + ")"
      else if (stack.getFileName != null) "(" + stack.getFileName + ")"
      else "(Unknown Source)"
    }

    while (!ok.get())
      try {
        if (retryCounter.incrementAndGet() > 0) Log.debug(retryCounter.get + ". retry " + stackTraceToString(Thread.currentThread().getStackTrace()(12)))
        val result = assertion
        result match {
          case bool: Boolean if !bool => throw new AssertionError("result was false")
          case _ =>
        }
        res.set(result)
        ok.set(true)
      } catch {
        case t: AssertionError => checkTimeout(t)
        case t: ComponentLookupException => checkTimeout(t)
      }
    res.get
  }

  /** Expect a Throwable of the method */
  def expect(expectedThrowable: Throwable, test: => Any, same: Boolean = false): Any = {
    val result = try {
      test
    }
    catch {
      case e: Throwable => e
    }

    result match {
      case e: Throwable =>
        if (same) e ==> ("expectedThrowable", expectedThrowable)
        e.getClass ==> ("expectedThrowable class", expectedThrowable.getClass)
        e.getMessage ==> ("expectedThrowable message", expectedThrowable.getMessage)
      case _ =>
        Assert.fail("Exception(Throwable) was expected but none came. Result: " + result)
    }
  }

  /** Expect a Failure with throwable */
  def expectFailure(expectedThrowable: Throwable, test: => Any, same: Boolean = false): Any = {
    val result = test

    result match {
      case Failure(e) => expect(expectedThrowable, e)
      case _ => Assert.fail("Result is not failure: " + result)
    }
  }

  /** Expect a Throwable type of the method */
  def expectThrowable(expectedThrowableClass: Class[_], test: => Unit) =
    try {
      test
      Assert.fail("Exception(Throwable) was expected but none came.")
    }
    catch {
      case e: Throwable =>
        if (e.getClass != expectedThrowableClass) e.printStackTrace()
        e.getClass ==> ("expectedThrowable class", expectedThrowableClass)
    }

  /**
   * Expect no Throwable of the method: remember this makes no sense, as the exception thrown makes a junit test failed.
   * However it is nicer to read what the test about and if it is deeper tested in an other try...
   */
  def expectNoException(test: => Unit) = try {
    test
  } catch {
    case e: Throwable => Assert.fail("Exception(Throwable) was NOT expected but came: " + e)
  }

  implicit class IntTestImplicit[T <: Int](actual: T) {
    def shouldBeLessThan(msg: String, other: Int) = assertThat(actual).as(msg).isLessThan(other)

    def shouldBeMoreThan(msg: String, other: Int) = assertThat(actual).as(msg).isGreaterThan(other)
  }

  implicit class LongTestImplicit[T <: Long](actual: T) {
    def shouldBeLessThan(msg: String, other: Long) = assertThat(actual).as(msg).isLessThan(other)

    def shouldBeMoreThan(msg: String, other: Long) = assertThat(actual).as(msg).isGreaterThan(other)
  }

  implicit class StringTestImplicit[T <: String](actual: T) {
    def shouldStartWith(expectedStart: String) = assertThat(actual).startsWith(expectedStart)

    def shouldContain(expectedContains: String) = assertThat(actual).contains(expectedContains)
  }

  implicit class TestPlusImplicitAssert[A](actual: => A) {
    def shouldBeNull = shouldBe(null)

    def shouldBe(expected: Any) = ==>(expected)

    def ==>(expected: Any): A = ==>("", expected)

    def ===(expected: Any): A = ==>("", expected)

    def ==>(descriptionAndExpected: (String, Any)): A = {
      val description = descriptionAndExpected._1
      val expected = descriptionAndExpected._2
      expected match {
        case Failure(t) => expectFailure(t, actual); null.asInstanceOf[A]
        case t: Throwable => expect(t, actual); null.asInstanceOf[A]
        case _ => val a = actual; assertThat(a).as(description).isEqualTo(expected); a
      }
    }
  }

  implicit class ThrowableTestImplicit[T <: Throwable](expectedThrowable: T) {
    /** Expect throwable(left) from the actual (right) */
    //    def <==(actual: => Unit) = expect(expectedThrowable, actual)
  }

  implicit class AnyTestImplicit[T <: Any](actual: T) {
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
      out = collectOutput {
        action
      }
    }
    OutAndErr(out, err)
  }

  case class OutAndErr(output: String, error: String)

  def shouldBeFaster(expectedMaxMilliseconds: Int, msg: String = "execution time")(action: => Unit) = {
    val executionTime = TimeCounter.millisecsOf(action)
    println(s"$msg $executionTime ms")
    executionTime shouldBeLessThan(msg, expectedMaxMilliseconds)
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

  implicit class MockitoTestPlusImplicit[A](mock: => A) {
    def -->(args: A*) = args.foldLeft(Mockito.when(mock))((stubbing, nextValue) => stubbing thenReturn nextValue)
  }

  implicit class MockitoTestPlusImplicitAny[T <: Any](actual: T) {
    def verify = Mockito.verify(actual, Mockito.times(1))
  }

  def mock[T](implicit m: Manifest[T]) = Mockito.mock(implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])

  def captor[T](actionOnArgument: T => Unit = (_: T) => {})(implicit m: Manifest[T]) = new ArgumentCaptorPlus(implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]], actionOnArgument)

  def capture[T](actionOnArgument: T => Unit = (_: T) => {})(implicit m: Manifest[T]): T = captor[T](actionOnArgument).capture

  class CapturingMatcherPlus[T](actionOnArgument: T => Unit = (_: T) => {}) extends CapturingMatcher[T]() {
    override def captureFrom(argument: Object) = {
      super.captureFrom(argument)
      actionOnArgument(argument.asInstanceOf[T])
    }
  }

  class ArgumentCaptorPlus[T](val clazz: Class[_ <: T], actionOnArgument: T => Unit = (_: T) => {}) {
    val capturingMatcher: CapturingMatcher[T] = new CapturingMatcherPlus(actionOnArgument)

    def capture: T = {
      ArgumentMatchers.argThat(capturingMatcher)
      Primitives.defaultValue(clazz)
    }

    def getValue: T = this.capturingMatcher.getLastValue

    def getAllValues: java.util.List[T] = this.capturingMatcher.getAllValues
  }

  implicit class ObservableTestPlusImplicit[T](observable: Observable[T]) {
    //noinspection UnstableApiUsage
    def test: AssertableSubscriber[T] = {
      val ts = AssertableSubscriberObservable.create[T](Long.MaxValue)
      observable.subscribe(ts.onNext, ts.onError, () => ts.onCompleted())
      ts
    }
  }

  implicit class StringTestPlusImplicit(string: String) {
    def urlGet(timeout: Int = 100): Try[String] = {
      val ct = Thread.currentThread().getId
      val url = new URL(string)
      val conn = url.openConnection.asInstanceOf[HttpURLConnection]
      conn.setUseCaches(false)
      conn.setRequestMethod("GET")
      conn.setRequestProperty("User-Agent", "Mozilla/5.0")
      conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
      conn.setConnectTimeout(timeout)
      conn.setReadTimeout(timeout)

      try {
        val responseCode = conn.getResponseCode
        Log.debug("UrlGet - Response Code : ", responseCode)
        if (responseCode == 200) TryFinallyClose(Source.fromInputStream(conn.getInputStream), (stream: BufferedSource) => stream.mkString)
        else Failure(new Exception(string + " not available, response code:" + responseCode + ", response message: " + conn.getResponseMessage))
      } catch {
        case t: Throwable =>
          System.err.synchronized {
            System.err.println(ct + " Error >>>>>>>>> ")
            System.err.println(ct + " Error " + string)
            System.err.println(ct + " Error " + t.getMessage)
            //System.err.println(ct + " conn.getErrorStream " + conn.getErrorStream)
            t.printStackTrace()
//            val errorLines = new BufferedReader(new InputStreamReader(conn.getErrorStream)).lines().collect(Collectors.joining("\n"));
//            System.err.println(ct + " Error errorLines" + errorLines)
            System.err.println(ct + " Error <<<<<<<<< ")

          }

          Log.error(string, t)
          Failure(t)
      }
    }
  }

}