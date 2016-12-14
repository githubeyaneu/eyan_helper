package eu.eyan.log

import javax.swing.JFrame
import com.jgoodies.forms.layout.FormLayout
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.WindowConstants
import javax.swing.text.DefaultCaret
import java.awt.Dialog.ModalExclusionType
import eu.eyan.util.awt.AwtHelper
import java.awt.Component

object LogWindow {
  val window = new LogWindow

  def show(origin: Component = null) = {
    window.frame.setVisible(true)
    AwtHelper.positionToRight(window.frame)
    if (origin != null) AwtHelper.positionToLeft(origin)
  }

  def add(text: String) = {
    if (window.frame.isVisible) {
      window.textArea.append(text + "\r\n")
      window.textArea.invalidate()
      window.textArea.repaint()
      window.textArea.validate()
    }
  }

  def close() = window.frame.dispose()
}

class LogWindow {
  val DEFAULT_WIDTH = 800
  val DEFAULT_HEIGHT = 600

  val content = new JPanelWithFrameLayout().newColumn("f:1px:g")
  val buttons = content.addPanelWithFormLayout().newColumn()
  val textArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown()

  buttons.addButton("Clear", e => textArea.setText(""))

  val frame = new JFrame("Debug")
  frame.add(content)
  frame.pack
  frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
  frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE)
}