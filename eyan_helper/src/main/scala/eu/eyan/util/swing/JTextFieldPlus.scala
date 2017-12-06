package eu.eyan.util.swing

import javax.swing.JTextField
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusAdapter
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import eu.eyan.util.awt.AwtHelper
import javax.swing.text.JTextComponent
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit
import java.awt.event.ActionEvent
import java.awt.Rectangle
import javax.swing.Action
import javax.swing.text.Document
import java.awt.Font

object JTextFieldPlus {

  //TODO remove Plus from name
  implicit class JTextFieldPlusImplicit[A <: JTextField](val jTextField: A) extends JTextComponentImplicit(jTextField) {

    def onActionPerformed(action: => Unit) = onActionPerformedEvent { e => action }
    def onActionPerformedEvent(action: ActionEvent => Unit) = { jTextField.addActionListener(AwtHelper.onActionPerformed(action)); jTextField }

    override def scrollRectToVisible(r: Rectangle) = { jTextField.scrollRectToVisible(r); jTextField }
    def setAction(a: Action) = { jTextField.setAction(a); jTextField }
    def setActionCommand(command: String) = { jTextField.setActionCommand(command); jTextField }
    def setColumns(columns: Int) = { jTextField.setColumns(columns); jTextField }
    def setDocument(doc: Document) = { jTextField.setDocument(doc); jTextField }
    def setFont(f: Font) = { jTextField.setFont(f); jTextField }
    def setHorizontalAlignment(alignment: Int) = { jTextField.setHorizontalAlignment(alignment); jTextField }
    def setScrollOffset(scrollOffset: Int) = { jTextField.setScrollOffset(scrollOffset); jTextField }
  }
}