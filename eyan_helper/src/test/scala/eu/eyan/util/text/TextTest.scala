package eu.eyan.util.text

import org.junit.runner.RunWith
import org.junit.Test
import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.testutil.TestPlus
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.log.Log
import org.junit.Before

@RunWith(classOf[ScalaEclipseJunitRunner])
class TextTest extends TestPlus {

  @Before
  def setUp = Log.activateDebugLevel

  @Test
  def textNoParam = {
    val text = BehaviorSubject("abc")
    case object Test extends Text(text) {}

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "abc"

    text.onNext("cde")
    actual ==> "cde"

  }

  @Test
  def textOneParam = {
    val text = BehaviorSubject("abc%s")
    val param = BehaviorSubject(123)
    case object Test extends Text(text, param)

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "abc123"

    text.onNext("cde%s")
    actual ==> "cde123"

    param.onNext(456)
    actual ==> "cde456"

  }

  @Test
  def textTwoParams = {
    val text = BehaviorSubject("a%sb%sc")
    val param1 = BehaviorSubject(123)
    val param2 = BehaviorSubject("")
    case object Test extends Text(text, param1, param2)

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "a123bc"

    //more params ok
    text.onNext("cde%s")
    actual ==> "cde123"

    param2.onNext("TT")
    actual ==> "cde123"
  }

  @Test
  def badParamNumber = {
    val text = BehaviorSubject("a%sb%sc")
    val param = BehaviorSubject(123)
    case object Test extends Text(text, param)
    var actual = ""
    Test.subscribe(string => actual = string)
    actual ==> ""

    text.onNext("cde%s")
    actual ==> "cde123"

    text.onNext("c %s %s %s")
    actual ==> "cde123"
  }
}