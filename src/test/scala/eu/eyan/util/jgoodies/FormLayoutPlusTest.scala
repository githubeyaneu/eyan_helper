package eu.eyan.util.jgoodies

import java.awt.Component
import java.io.IOException

import org.fest.assertions.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner
import javax.swing.JPanel

@RunWith(classOf[ScalaEclipseJunitRunner])
class FormLayoutPlusTest() {

  val container = new JPanel

  @Before
  @throws(classOf[IOException])
  def setUp = {

  }

  @Test
  @throws(classOf[Exception])
  def test_FormLayoutPlus_col = {
    val flp = FormLayoutPlus(container, "p")
    assertThat(getLayout.getColumnSpec(1).encode()).isEqualTo("p")
    assertThat(getLayout.getRowCount).isEqualTo(0)
  }

  @Test
  @throws(classOf[Exception])
  def test_FormLayoutPlus_colRowComp = {
    val subComponent = new JPanel
    val flp = FormLayoutPlus(container, "p", "1px", subComponent)
    assertThat(getLayout.getColumnSpec(1).encode()).isEqualTo("p")
    assertThat(getLayout.getRowSpec(1).encode()).isEqualTo("1px")

    assertThat(container.getComponents.contains(subComponent)).isTrue()
    assertThat(getLayout.getConstraints(subComponent).gridX).isEqualTo(1)
    assertThat(getLayout.getConstraints(subComponent).gridY).isEqualTo(1)
  }

  @Test
  @throws(classOf[Exception])
  def test_FormLayoutPlus = {
    val flp = FormLayoutPlus(container, "p")
    flp.appendRow("1px", "2px")
    assertThat(getLayout.getRowSpec(1).encode()).isEqualTo("1px")
    assertThat(getLayout.getRowSpec(2).encode()).isEqualTo("2px")

    assertThat(flp.getComponent).isSameAs(container)

    flp.appendRow("3px", null.asInstanceOf[Component])
    assertThat(getLayout.getRowSpec(3).encode()).isEqualTo("3px")
    assertThat(container.getComponentCount).isEqualTo(0)
    //    assertThat(container.getComponents.contains(subComponent)).isTrue()
    //    assertThat(getLayout().getConstraints(subComponent).gridX).isEqualTo(1)
    //    assertThat(getLayout().getConstraints(subComponent).gridY).isEqualTo(1)
  }

  private def getLayout = container.getLayout.asInstanceOf[FormLayoutPlus]
}