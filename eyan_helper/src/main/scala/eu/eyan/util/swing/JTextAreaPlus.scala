package eu.eyan.util.swing

import javax.swing.JTextArea
import javax.swing.text.DefaultCaret

class JTextAreaPlus extends JTextArea {
  def alwaysScrollDown() = {
    this.getCaret().asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)
    this
  }
}