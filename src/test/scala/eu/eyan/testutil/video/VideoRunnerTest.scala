package eu.eyan.testutil.video

import org.junit.runner.RunWith
import org.junit.Test
import org.junit.Before
import javax.swing.JFrame
import javax.swing.JLabel

@RunWith(classOf[VideoRunner])
class VideoRunnerTest {

  @Before
  def setUp = {
    val frame = new JFrame
    frame.add(new JLabel("Test"))
    frame.setVisible(true)
    frame.pack
    VideoRunner.setComponentToRecord(frame)
  }

  @Test
  def test_video_ok = {

  }
}