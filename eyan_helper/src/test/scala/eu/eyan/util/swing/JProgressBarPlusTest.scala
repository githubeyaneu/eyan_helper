package eu.eyan.util.swing

import org.fest.assertions.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.testutil.TestPlus.waitFor

@RunWith(classOf[ScalaEclipseJunitRunner])
class JProgressBarPlusTest() {

  @Before
  def setUp {}

  @Test
  def test_JProgressBarPlus = {
    val pb = new JProgressBarPlus(0, 100, "for %d mat")
    assertThat(pb.getMinimum).isEqualTo(0)
    assertThat(pb.getMaximum).isEqualTo(100)
  }

  @Test
  def test_createPercentChangedConsumer = {
    val pb = new JProgressBarPlus(0, 100, "for %d mat")
    val pc = pb.createPercentChangedConsumer
    pc(50)
    assertThat(pb.isVisible).isTrue()
    waitFor(() => assertThat(pb.getValue).isEqualTo(50))
    waitFor(() => assertThat(pb.getString).isEqualTo("for 50 mat"))
  }

  @Test
  def test_percentChanged = {
    val pb = new JProgressBarPlus(0, 100, "for %d%% mat")

    assertThat(pb.isVisible).isTrue()
    waitFor(() => assertThat(pb.getValue).isEqualTo(0))
    waitFor(() => assertThat(pb.getString).isEqualTo("for 0% mat"))

    pb.percentChanged(1)
    assertThat(pb.isVisible).isTrue()
    waitFor(() => assertThat(pb.getValue).isEqualTo(1))
    waitFor(() => assertThat(pb.getString).isEqualTo("for 1% mat"))

    pb.percentChanged(1)
    assertThat(pb.isVisible).isTrue()
    waitFor(() => assertThat(pb.getValue).isEqualTo(1))
    waitFor(() => assertThat(pb.getString).isEqualTo("for 1% mat"))
  }

  @Test
  def test_doneThenInvisible = {
    val pb = new JProgressBarPlus(0, 100, "for %d%% mat")

    waitFor(() => assertThat(pb.isVisible).isTrue)
    
    pb.doneThenInvisible()
    waitFor(() => assertThat(pb.isVisible).isFalse)
  }

  @Test
  def test_setFormat = {
    val pb = new JProgressBarPlus(0, 100, "for %d%% mat")

    waitFor(() => assertThat(pb.getString).isEqualTo("for 0% mat"))

    pb.setFormat("p: %d")
    waitFor(() => assertThat(pb.getString).isEqualTo("p: 0"))
  }

  @Test
  def test_finished = {
    val pb = new JProgressBarPlus(0, 444, "for %d%% mat")

    pb.finished
    waitFor(() => assertThat(pb.getString).isEqualTo("Ready"))
    waitFor(() => assertThat(pb.getValue).isEqualTo(444))
  }
}