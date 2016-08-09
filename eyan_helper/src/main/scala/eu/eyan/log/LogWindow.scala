package eu.eyan.log

import javax.swing.JFrame
import com.jgoodies.forms.layout.FormLayout
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.WindowConstants

object LogWindow {
  val window = new LogWindow()
  def show() = window.frame.setVisible(true)
  def add(text: String) = window.textArea.append(text + "\r\n")
}

class LogWindow {
  val content = new JPanelWithFrameLayout("f:p:g")
  val textArea = content.newColumn("f:p:g").addTextArea()
  val frame = new JFrame("Debug")
  frame.add(content)
  frame.pack
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
}