package eu.eyan.util.swing

import javax.swing.JTextArea
import javax.swing.text.DefaultCaret
import eu.eyan.util.awt.AwtHelper

class JTextAreaPlus extends JTextArea {
  def alwaysScrollDown() = {
    this.getCaret().asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)
    this
  }

  def dontScroll() = {
    this.getCaret().asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.NEVER_UPDATE)
    this
  }

  def appendText(text: String) = { this.append(text); this }

  def clickSelectsAll = { this.addMouseListener(AwtHelper.mouseClick { () => { this.selectAll } }); this }

  def lines = this.getText.lines
}