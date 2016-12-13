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

object JTextFieldPlus {
  implicit class JTextFieldPlusImplicit[A <: JTextComponent](val textField: A) {
    def onFocusLost(action: () => Unit) = {
      textField.addFocusListener(new FocusAdapter { override def focusLost(e: FocusEvent) = { action() } })
      textField
    }

    def addComponentMovedListener(action: ComponentEvent => Unit) = {
      textField.addComponentListener(new ComponentAdapter { override def componentMoved(e: ComponentEvent) = { action(e) } })
      textField
    }

    def addComponentResizedListener(action: ComponentEvent => Unit) = {
      textField.addComponentListener(new ComponentAdapter { override def componentResized(e: ComponentEvent) = { action(e) } })
      textField
    }

    def onComponentResized(action: () => Unit) = {
      textField.addComponentListener(new ComponentAdapter { override def componentResized(e: ComponentEvent) = { action() } })
      textField
    }

    def addKeyTypedListener(keyTypedAction: KeyEvent => Unit) = {
      textField.addKeyListener(new KeyAdapter { override def keyTyped(e: KeyEvent) { keyTypedAction(e) } })
      textField
    }
    def addKeyReleasedListener(keyReleasedAction: KeyEvent => Unit) = {
      textField.addKeyListener(new KeyAdapter { override def keyReleased(e: KeyEvent) { keyReleasedAction(e) } })
      textField
    }
    def addKeyPressedListener(keyPressedAction: KeyEvent => Unit) = {
      textField.addKeyListener(new KeyAdapter { override def keyPressed(e: KeyEvent) { keyPressedAction(e) } })
      textField
    }

    def addFocusLostListener(action: FocusEvent => Unit) = {
      textField.addFocusListener(new FocusAdapter { override def focusLost(e: FocusEvent) = { action(e) } })
      textField
    }

    def clickSelectAll = { textField.addMouseListener(AwtHelper.mouseClick { () => textField.selectAll }); textField }
  }
}