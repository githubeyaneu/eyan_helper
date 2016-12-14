package eu.eyan.testutil

import java.awt.Component
import java.util.concurrent.TimeUnit
import org.fest.swing.core.EmergencyAbortListener
import org.fest.swing.timing.Pause
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import eu.eyan.testutil.video.VideoRunner
import javax.swing.JFrame
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.JTextField
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.fixture.JTextComponentFixture
import org.fest.swing.fixture.ComponentFixture
import org.junit.After

object AbstractUiTest {
  @BeforeClass def setUpClass(): Unit = EmergencyAbortListener.registerInToolkit()
}

/**
 * Helper class for Java Swing UI Tests. It uses fest-swing.
 * In the setUp method of your test please call the super.setUp with the component you want to test.
 * Then it is recorded with videoscreenrecorder.
 *
 * For the test there are available components: frame, componentBefore/componentAfter
 *
 */
@RunWith(classOf[VideoRunner])
class AbstractUiTest {
  val TIMEOUT = 20
  val _globalTimeout = new Timeout(TIMEOUT, TimeUnit.SECONDS)
  @Rule def globalTimeout: Timeout = _globalTimeout

  var frame: FrameFixture = null
  var componentBefore: JTextComponentFixture = null
  var componentAfter: JTextComponentFixture = null

  def pause(ms: Long): Unit = Pause.pause(ms)

  def setUp(component: Component): FrameFixture = {
    val container = new JPanelWithFrameLayout
    container.setName("AbstractUiTest.container")
    container.newColumn
    container.addTextField("Before").setName("AbstractUiTest.before")
    container.newRow
    container.add(component)
    container.newRow
    container.addTextField("After").setName("AbstractUiTest.after")

    val frame = new JFrame
    frame.add(container)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.pack
    frame.setVisible(true)

    this.frame = new FrameFixture(frame)
    this.componentBefore = this.frame.textBox("AbstractUiTest.before")
    this.componentAfter = this.frame.textBox("AbstractUiTest.after")

    VideoRunner.setComponentToRecord(this.frame.component)

    this.frame
  }

  @After def tearDown(): Unit = frame.cleanUp
}