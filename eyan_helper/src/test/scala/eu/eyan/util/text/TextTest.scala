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
    case object Test extends Text(text) {
      def get = "get"
    }

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
    case object Test extends Text(text, param) {
      def get = "get"
    }

    var actual = ""

    Test.subscribe(string => actual = string)
    actual ==> "abc123"

    text.onNext("cde%s")
    actual ==> "cde123"

    param.onNext(456)
    actual ==> "cde456"

  }
}