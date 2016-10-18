package eu.eyan.util.swing

import java.awt.Component
import java.awt.event.ActionEvent
import com.jgoodies.forms.factories.CC
import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.jgoodies.FormLayoutPlus
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.JButton
import javax.swing.JCheckBox
import java.awt.FlowLayout
import javax.swing.JTextPane
import java.awt.event.KeyListener
import java.awt.event.KeyEvent
import java.awt.event.KeyAdapter
import javax.swing.event.ListDataListener
import javax.swing.event.ListDataEvent

object SwingPlus {
  def showErrorDialog(msg: String, e: Throwable, shown: Set[Throwable] = Set()): Unit = {
    if (e.getCause != null && !shown.contains(e.getCause))
      showErrorDialog(msg + ", " + e.getLocalizedMessage, e.getCause, shown + e)
    else JOptionPane.showMessageDialog(null, msg + ", " + e.getLocalizedMessage)
  }

  def invokeLater(runnable: () => Unit) = SwingUtilities.invokeLater(AwtHelper.newRunnable(() => runnable()))

  def createListContentsChangedListener(listDataContentsChangedEventConsumer: ListDataEvent => Unit) =
    new ListDataListener() {
      override def intervalRemoved(e: ListDataEvent) = {}
      override def intervalAdded(e: ListDataEvent) = {}
      override def contentsChanged(e: ListDataEvent) = listDataContentsChangedEventConsumer(e)
    }

  def addKeyPressedListener(textPane: JTextPane, keyPressedEventConsumer: KeyEvent => Unit) =
    textPane.addKeyListener(new KeyAdapter() { override def keyPressed(e: KeyEvent) = keyPressedEventConsumer(e) });

  def newLeftFlowPanel() = new FlowPanel(new FlowLayout(FlowLayout.LEFT))

  // CHECKBOX
  def newCheckBoxWithAction(text: String, action: () => Unit): JCheckBox = newCheckBoxWithAction(text, (cb, e) => action())

  def checkBox(text: String, action: JCheckBox => Unit) = newCheckBoxWithAction(text, (cb, e) => action(cb))

  def newCheckBoxWithAction(text: String, action: (JCheckBox, ActionEvent) => Unit) = {
    val cb = new JCheckBox(text)
    cb.addActionListener(AwtHelper.newActionListener(e => action(cb, e)))
    cb
  }

  // BUTTON
  def button(text: String, action: () => Unit) = newButtonWithAction(text, (b, e) => action())

  def newButtonWithAction(text: String, action: (JButton, ActionEvent) => Unit) = {
    val button = new JButton(text)
    button.addActionListener(AwtHelper.newActionListener(e => action(button, e)))
    button
  }

  // TEXTFIELD
  def textField(columns: Int, actionListener: JTextField => Unit) = newTextFieldWithAction(columns, (tf, e) => actionListener(tf))

  def newTextFieldWithAction(columns: Int, actionListener: (JTextField, ActionEvent) => Unit) = {
    val tf = new JTextFieldPlus(columns)
    tf.addActionListener(AwtHelper.newActionListener(e => actionListener(tf, e)))
    tf
  }

  // LABEL
  def label(text: String) = new JLabel(text)

  // PROGRESSBAR
  def jProgressBarPercent(format: String) = {
    val progressBar = new JProgressBarPlus(0, 100, format)
    progressBar.setValue(0)
    progressBar.setStringPainted(true)
    progressBar.setVisible(false)
    invokeLater(() => progressBar.setString("..."))
    progressBar
  }

  def jPanelOneRow(rowSpec: String, col1Spec: String, col1Comp: Component, col2Spec: String, col2Comp: Component) = {
    val layout = new FormLayoutPlus(new JPanel(), col1Spec + "," + col2Spec)
    layout.appendRow(rowSpec)
    layout.getComponent().add(col1Comp, CC.xy(1, 1))
    layout.getComponent().add(col2Comp, CC.xy(2, 1))
    layout.getComponent()
  }
}