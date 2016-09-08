package eu.eyan.testutil

import org.junit.runner.RunWith
import eu.eyan.testutil.video.VideoRunner
import org.junit.Rule
import org.junit.rules.Timeout
import java.util.concurrent.TimeUnit
import org.junit.BeforeClass
import org.fest.swing.core.EmergencyAbortListener
import org.fest.swing.timing.Pause

@RunWith(classOf[VideoRunner])
class AbstractUiTest {

  val _globalTimeout = new Timeout(20, TimeUnit.SECONDS)
  @Rule def globalTimeout = _globalTimeout

  def pause(ms: Long) = Pause.pause(ms)
}

object AbstractUiTest {
  @BeforeClass def setUpClass() = EmergencyAbortListener.registerInToolkit()
}