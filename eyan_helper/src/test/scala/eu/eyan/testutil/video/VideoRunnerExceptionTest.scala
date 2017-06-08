package eu.eyan.testutil.video

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(classOf[VideoRunner])
class VideoRunnerExceptionTest {

  val _expectedException = ExpectedException.none
  @Rule def expectedException = _expectedException

  @Before
  def setUp = {
    _expectedException.expect(classOf[Exception])
    def contains(text:String) = new BaseMatcher[String]{
      def matches(item: Any): Boolean = item.toString.contains(text)
      def describeTo(desc: Description): Unit = desc.appendText("contains text")
    }
    _expectedException.expectMessage(contains("Component to record not found. Please set it in the @Before method for the test"))
  }

  @Test
  def test_expectedExceptionWhenCompoentNotSetInBefore = {}
}