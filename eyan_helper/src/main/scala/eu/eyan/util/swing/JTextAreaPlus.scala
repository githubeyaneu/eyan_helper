package eu.eyan.util.swing

import javax.swing.JTextArea
import javax.swing.text.DefaultCaret
import eu.eyan.util.awt.AwtHelper
import javax.swing.event.DocumentEvent
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit

object JTextAreaPlus{
  implicit class JTextAreaImplicit[TYPE <: JTextArea](jTextArea: TYPE) extends JTextComponentImplicit(jTextArea){
    //append(String)
    //insert(String, int)
    //replaceRange(String, int, int)
    //setColumns(int)
    //setFont(Font)
    //setLineWrap(boolean)
    //setRows(int)
    //setTabSize(int)
    //setWrapStyleWord(boolean)    
  }
}
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

  def clickSelectsAll = { this.addMouseListener(AwtHelper.onClicked{ e => { this.selectAll } }); this }

  def lines = this.getText.lines
  
  def onDocumentAction(documentAction: () => Unit = null) = {
    if (documentAction != null) this.getDocument.addDocumentListener(SwingPlus.docListener(documentAction))
    this
  }
}