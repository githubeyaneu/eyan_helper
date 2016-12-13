package eu.eyan.testutil.video

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.internal.matchers.Contains

@RunWith(classOf[VideoRunner])
class VideoRunnerExceptionTest {

  val _expectedException = ExpectedException.none
  @Rule def expectedException = _expectedException

  @Before
  def setUp = {
    _expectedException.expect(classOf[Exception])
    _expectedException.expectMessage(new Contains("Component to record not found. Please set it in the @Before method for the test"))
  }

  @Test
  def test_expectedExceptionWhenCompoentNotSetInBefore = {}
}