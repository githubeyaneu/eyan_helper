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

object JTextFieldPlus {
  //TODO remove Plus from name
  implicit class JTextFieldPlusImplicit[A <: JTextField](val jTextField: A) extends JTextComponentImplicit(jTextField){
    
    //addActionListener(ActionListener)
    //isValidateRoot()
    //postActionEvent()
    //removeActionListener(ActionListener)
    //scrollRectToVisible(Rectangle)
    //setAction(Action)
    //setActionCommand(String)
    //setColumns(int)
    //setDocument(Document)
    //setFont(Font)
    //setHorizontalAlignment(int)
    //setScrollOffset(int)    
    
    def onFocusLost(action: () => Unit) = {
      jTextField.addFocusListener(new FocusAdapter { override def focusLost(e: FocusEvent) = { action() } })
      jTextField
    }

    def addComponentMovedListener(action: ComponentEvent => Unit) = {
      jTextField.addComponentListener(new ComponentAdapter { override def componentMoved(e: ComponentEvent) = { action(e) } })
      jTextField
    }

    def addComponentResizedListener(action: ComponentEvent => Unit) = {
      jTextField.addComponentListener(new ComponentAdapter { override def componentResized(e: ComponentEvent) = { action(e) } })
      jTextField
    }

    def onComponentResized(action: () => Unit) = {
      jTextField.addComponentListener(new ComponentAdapter { override def componentResized(e: ComponentEvent) = { action() } })
      jTextField
    }

    def addKeyTypedListener(keyTypedAction: KeyEvent => Unit) = {
      jTextField.addKeyListener(new KeyAdapter { override def keyTyped(e: KeyEvent) = { keyTypedAction(e) } })
      jTextField
    }
    def addKeyReleasedListener(keyReleasedAction: KeyEvent => Unit) = {
      jTextField.addKeyListener(new KeyAdapter { override def keyReleased(e: KeyEvent) = { keyReleasedAction(e) } })
      jTextField
    }
    def addKeyPressedListener(keyPressedAction: KeyEvent => Unit) = {
      jTextField.addKeyListener(new KeyAdapter { override def keyPressed(e: KeyEvent) = { keyPressedAction(e) } })
      jTextField
    }

    def addFocusLostListener(action: FocusEvent => Unit) = {
      jTextField.addFocusListener(new FocusAdapter { override def focusLost(e: FocusEvent) = { action(e) } })
      jTextField
    }

    def clickSelectAll = { jTextField.addMouseListener(AwtHelper.onClicked { e => jTextField.selectAll }); jTextField }
  }
}