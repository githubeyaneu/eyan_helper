package eu.eyan.util.swing

import java.awt.FlowLayout
import java.awt.event.KeyEvent

import org.fest.assertions.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.testutil.TestPlus.waitFor
import eu.eyan.util.jgoodies.FormLayoutPlus
import javax.swing.JPanel
import javax.swing.JTextPane
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit
import scala.language.reflectiveCalls

@RunWith(classOf[ScalaEclipseJunitRunner])
class SwingUtilitiesPlusTest() {

  @Test
  def test_createListContentsChangedListener = {
    var called = false
    val listener = SwingPlus.createListContentsChangedListener(e => called = true)
    listener.contentsChanged(null)
    listener.intervalAdded(null)
    listener.intervalRemoved(null)
    assertThat(called).isTrue
  }

  @Test
  def test_addKeyPressedListener = {
    var called = false
    val textPane = new JTextPane {
      def pressKey = {
        processKeyEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_UNDEFINED, 'A', 'A'))
        processKeyEvent(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0, KeyEvent.VK_UNDEFINED, 'A', 'A'))
        processKeyEvent(new KeyEvent(this, KeyEvent.KEY_TYPED, 0, KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED))
      }
    }
    textPane.onKeyPressed(called = true)
    textPane.pressKey
    assertThat(called).isTrue
  }

  @Test
  def test_newLeftFlowPanel = {
    val panel = SwingPlus.newLeftFlowPanel
    assertThat(panel.getLayout.asInstanceOf[FlowLayout].getAlignment).isEqualTo(FlowLayout.LEFT)
  }

  @Test
  def test_newCheckBoxWithAction = {
    var runned = false
    val cb = SwingPlus.newCheckBoxWithAction("a", () => runned = true)
    assertThat(cb.getText).isEqualTo("a")
    cb.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jCheckBox = {
    var runned = false
    val cb = SwingPlus.checkBox("a", (cb) => runned = true)
    assertThat(cb.getText).isEqualTo("a")
    cb.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jButton = {
    var runned = false
    val button = SwingPlus.button("a", () => runned = true)
    assertThat(button.getText).isEqualTo("a")
    button.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_newButtonWithAction = {
    var runned = false
    val button = SwingPlus.newButtonWithAction("a", (b, e) => runned = true)
    assertThat(button.getText).isEqualTo("a")
    button.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jTextField = {
    var runned = false
    val textField = SwingPlus.textField(3, tf => runned = true)
    assertThat(textField.getColumns).isEqualTo(3)
    textField.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_newTextFieldWithAction = {
    var runned = false
    val textField = SwingPlus.newTextFieldWithAction(3, (tf, e) => runned = true)
    assertThat(textField.getColumns).isEqualTo(3)
    textField.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jLabel = {
    val label = SwingPlus.label("a")
    assertThat(label.getText).isEqualTo("a")
  }

  @Test
  def test_jProgressBarPercent = {
    val progressBar = SwingPlus.jProgressBarPercent("_%d%%_")
    waitFor(assertThat(progressBar.getString).isEqualTo("..."))
    assertThat(progressBar.getValue).isEqualTo(0)
    assertThat(progressBar.isVisible).isFalse
  }

  @Test
  def test_jPanelOneRow = {
    val comp1 = new JPanel
    val comp2 = new JPanel
    val container = SwingPlus.jPanelOneRow("1px", "2px", comp1, "3px", comp2)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getColumnSpec(1).encode).isEqualTo("2px")
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getColumnSpec(2).encode).isEqualTo("3px")

    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getRowSpec(1).encode).isEqualTo("1px")

    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp1).gridX).isEqualTo(1)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp1).gridY).isEqualTo(1)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp2).gridX).isEqualTo(2)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp2).gridY).isEqualTo(1)

  }

}