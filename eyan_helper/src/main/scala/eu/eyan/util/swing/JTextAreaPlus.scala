package eu.eyan.util.swing

import java.awt.Font

import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit
import javax.swing.JTextArea

object JTextAreaPlus {
  implicit class JTextAreaImplicit[TYPE <: JTextArea](jTextArea: TYPE) extends JTextComponentImplicit(jTextArea) {
    def appendText(text: String) = { jTextArea.append(text); jTextArea }
    def insert(str: String, pos: Int) = { jTextArea.insert(str, pos); jTextArea }
    def replaceRange(str: String, start: Int, end: Int) = { jTextArea.replaceRange(str, start, end); jTextArea }
    def columns(columns: Int) = { jTextArea.setColumns(columns); jTextArea }
    override def font(font: Font) = { jTextArea.setFont(font); jTextArea }
    def lineWrap(wrap: Boolean) = { jTextArea.setLineWrap(wrap); jTextArea }
    def lineWrap: TYPE = lineWrap(true)
    def rows(rows: Int) = { jTextArea.setRows(rows); jTextArea }
    def tabSize(size: Int) = { jTextArea.setTabSize(size); jTextArea }
    def wrapStyleWord(word: Boolean) = { jTextArea.setWrapStyleWord(word); jTextArea }

    def onDocumentAction(documentAction: => Unit) = { jTextArea.getDocument.addDocumentListener(SwingPlus.docListener(documentAction)); jTextArea }
  }
}