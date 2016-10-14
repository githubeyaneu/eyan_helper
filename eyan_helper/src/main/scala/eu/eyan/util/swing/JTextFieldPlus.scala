package eu.eyan.util.swing

import javax.swing.JTextField
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusAdapter
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import eu.eyan.util.awt.AwtHelper

class JTextFieldPlus(cols: Int) extends JTextField(cols) {

  def addKeyTypedListener(keyTypedAction: KeyEvent => Unit): JTextFieldPlus = {
    addKeyListener(new KeyAdapter { override def keyTyped(e: KeyEvent) { keyTypedAction(e) } })
    this
  }
  def addKeyReleasedListener(keyReleasedAction: KeyEvent => Unit): JTextFieldPlus = {
    addKeyListener(new KeyAdapter { override def keyReleased(e: KeyEvent) { keyReleasedAction(e) } })
    this
  }
  def addKeyPressedListener(keyPressedAction: KeyEvent => Unit): JTextFieldPlus = {
    addKeyListener(new KeyAdapter { override def keyPressed(e: KeyEvent) { keyPressedAction(e) } })
    this
  }

  def addFocusLostListener(action: FocusEvent => Unit) = {
    addFocusListener(new FocusAdapter { override def focusLost(e: FocusEvent) = { action(e) } })
    this
  }

  def addComponentMovedListener(componentMovedEvent: ComponentEvent => Unit) = {
    addComponentListener(new ComponentAdapter { override def componentMoved(e: ComponentEvent) = { componentMovedEvent(e) } })
    this
  }

  def addComponentResizedListener(componentResizedEvent: ComponentEvent => Unit) = {
    addComponentListener(new ComponentAdapter { override def componentResized(e: ComponentEvent) = { componentResizedEvent(e) } })
    this
  }
  
  def clickSelectAll = {addMouseListener(AwtHelper.mouseClick { () => this.selectAll }); this}
}