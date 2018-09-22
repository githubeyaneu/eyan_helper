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
    val text = "abc"
    case object Test extends Text(text) {
      def obs = templateObservable
    }

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "abc"

    Test.obs.onNext("cde")
    actual ==> "cde"

  }

  @Test
  def textOneParam = {
    val text = "abc%s"
    val param = BehaviorSubject(123)
    case object Test extends Text(text, param){
      def obs = templateObservable
    }

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "abc123"

    Test.obs.onNext("cde%s")
    actual ==> "cde123"

    param.onNext(456)
    actual ==> "cde456"

  }

  @Test
  def textTwoParams = {
    val text = "a%sb%sc"
    val param1 = BehaviorSubject(123)
    val param2 = BehaviorSubject("")
    case object Test extends Text(text, param1, param2){
      def obs = templateObservable
    }

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "a123bc"

    //more params ok
    Test.obs.onNext("cde%s")
    actual ==> "cde123"

    param2.onNext("TT")
    actual ==> "cde123"
  }

  @Test
  def badParamNumber = {
    val text = "a%sb%sc"
    val param = BehaviorSubject(123)
    case object Test extends Text(text, param){
      def obs = templateObservable
    }
    var actual = ""
    Test.subscribe(string => actual = string)
    actual ==> ""

    Test.obs.onNext("cde%s")
    actual ==> "cde123"

    Test.obs.onNext("c %s %s %s")
    actual ==> "cde123"
  }
}