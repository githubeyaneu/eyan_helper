package eu.eyan.util.swing

import eu.eyan.testutil.ScalaEclipseJunitRunner
import org.fest.assertions.Assertions._
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import eu.eyan.util.tuple.Tuple2
import org.junit.runner.RunWith
import javax.swing.JPanel
import java.awt.Component
import java.util.function.Consumer
import javax.swing.event.ListDataEvent
import java.awt.event.KeyEvent
import javax.swing.JTextPane
import org.fest.swing.util.Modifiers
import java.awt.FlowLayout
import org.fest.swing.fixture.JCheckBoxFixture
import org.fest.swing.core.Robot
import org.fest.swing.core.BasicRobot
import javax.swing.JCheckBox
import java.util.function.BiConsumer
import javax.swing.JButton
import java.awt.event.ActionEvent
import javax.swing.JTextField
import eu.eyan.testutil.TestPlus._
import com.jgoodies.forms.layout.FormLayout
import eu.eyan.util.jgoodies.FormLayoutPlus

@RunWith(classOf[ScalaEclipseJunitRunner])
class SwingUtilitiesPlusTest() {

  @Test def test_SwingUtilitiesPlus = new SwingUtilitiesPlus

  @Test
  def test_createListContentsChangedListener = {
    var called = false
    val listener = SwingUtilitiesPlus.createListContentsChangedListener(
      new Consumer[ListDataEvent] { override def accept(e: ListDataEvent) = { called = true } })
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
        processKeyEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_UNDEFINED, 'A'))
        processKeyEvent(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0, KeyEvent.VK_UNDEFINED, 'A'))
        processKeyEvent(new KeyEvent(this, KeyEvent.KEY_TYPED, 0, KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED))
      }
    }
    val listener = SwingUtilitiesPlus.addKeyPressedListener(
      textPane,
      new Consumer[KeyEvent] { override def accept(e: KeyEvent) = { called = true } })
    textPane.pressKey
    assertThat(called).isTrue
  }

  @Test
  def test_newLeftFlowPanel = {
    val panel = SwingUtilitiesPlus.newLeftFlowPanel
    assertThat(panel.getLayout.asInstanceOf[FlowLayout].getAlignment).isEqualTo(FlowLayout.LEFT)
  }

  @Test
  def test_newCheckBoxWithAction = {
    var runned = false
    val action = new Runnable { def run() = { runned = true } }
    val cb = SwingUtilitiesPlus.newCheckBoxWithAction("a", action)
    assertThat(cb.getText).isEqualTo("a")
    cb.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jCheckBox = {
    var runned = false
    val action = new Consumer[JCheckBox] { override def accept(cb: JCheckBox) = { runned = true } }
    val cb = SwingUtilitiesPlus.jCheckBox("a", action)
    assertThat(cb.getText).isEqualTo("a")
    cb.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jButton = {
    var runned = false
    val action = new Runnable { def run() = { runned = true } }
    val button = SwingUtilitiesPlus.jButton("a", action)
    assertThat(button.getText).isEqualTo("a")
    button.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_newButtonWithAction = {
    var runned = false
    val action = new BiConsumer[JButton, ActionEvent] { override def accept(cb: JButton, e: ActionEvent) = { runned = true } }
    val button = SwingUtilitiesPlus.newButtonWithAction("a", action)
    assertThat(button.getText).isEqualTo("a")
    button.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jTextField = {
    var runned = false
    val action = new Consumer[JTextField] { override def accept(cb: JTextField) = { runned = true } }
    val textField = SwingUtilitiesPlus.jTextField(3, action)
    assertThat(textField.getColumns).isEqualTo(3)
    textField.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_newTextFieldWithAction = {
    var runned = false
    val action = new BiConsumer[JTextField, ActionEvent] { override def accept(cb: JTextField, e: ActionEvent) = { runned = true } }
    val textField = SwingUtilitiesPlus.newTextFieldWithAction(3, action)
    assertThat(textField.getColumns).isEqualTo(3)
    textField.getActionListeners()(0).actionPerformed(null)
    assertThat(runned).isTrue
  }

  @Test
  def test_jLabel = {
    val label = SwingUtilitiesPlus.jLabel("a")
    assertThat(label.getText).isEqualTo("a")
  }

  @Test
  def test_jProgressBarPercent = {
    val progressBar = SwingUtilitiesPlus.jProgressBarPercent("_%d%%_")
    waitFor(() => assertThat(progressBar.getString).isEqualTo("..."))
    assertThat(progressBar.getValue).isEqualTo(0)
    assertThat(progressBar.isVisible).isFalse
  }

  @Test
  def test_jPanelOneRow = {
    val comp1 = new JPanel
    val comp2 = new JPanel
    val container = SwingUtilitiesPlus.jPanelOneRow("1px", "2px", comp1, "3px", comp2)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getColumnSpec(1).encode).isEqualTo("2px")
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getColumnSpec(2).encode).isEqualTo("3px")

    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getRowSpec(1).encode).isEqualTo("1px")

    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp1).gridX).isEqualTo(1)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp1).gridY).isEqualTo(1)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp2).gridX).isEqualTo(2)
    assertThat(container.getLayout.asInstanceOf[FormLayoutPlus].getConstraints(comp2).gridY).isEqualTo(1)

  }

}