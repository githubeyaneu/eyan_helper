package eu.eyan.util.swing

import javax.swing.JComponent
import eu.eyan.util.awt.ContainerPlus.ContainerPlusImplicit
import eu.eyan.util.java.beans.BeansPlus
import java.beans.PropertyChangeEvent
import javax.swing.event.AncestorEvent

object JComponentPlus {
  implicit class JComponentImplicit[TYPE <: JComponent](jComponent: TYPE) extends ContainerPlusImplicit(jComponent) {
    
	  def onAncestorAdded(action: => Unit) = onAncestorAddedEvent { e => action }
	  def onAncestorAddedEvent(action: AncestorEvent => Unit) = { jComponent.addAncestorListener(SwingPlus.onAncestorAdded(action)); jComponent }
	  def onAncestorRemoved(action: => Unit) = onAncestorRemovedEvent { e => action }
	  def onAncestorRemovedEvent(action: AncestorEvent => Unit) = { jComponent.addAncestorListener(SwingPlus.onAncestorRemoved(action)); jComponent }
	  def onAncestorMoved(action: => Unit) = onAncestorMovedEvent { e => action }
	  def onAncestorMovedEvent(action: AncestorEvent => Unit) = { jComponent.addAncestorListener(SwingPlus.onAncestorMoved(action)); jComponent }
	  
	  def onVetoableChange(action: => Unit) = onVetoableChangeEvent { e => action }
	  def onVetoableChangeEvent(action: PropertyChangeEvent => Unit) = { jComponent.addVetoableChangeListener(BeansPlus.onVetoableChange(action)); jComponent }
    
    
//disable()
//enable()
//grabFocus()
//hide()
//putClientProperty(Object, Object)
//registerKeyboardAction(ActionListener, String, KeyStroke, int)
//registerKeyboardAction(ActionListener, KeyStroke, int)
//requestDefaultFocus()
//requestFocus()
//requestFocus(boolean)
//requestFocusInWindow()
//resetKeyboardActions()
//scrollRectToVisible(Rectangle)
//setActionMap(ActionMap)
//setAlignmentX(float)
//setAlignmentY(float)
//setAutoscrolls(boolean)
//setBackground(Color)
//setBorder(Border)
//setComponentPopupMenu(JPopupMenu)
//setDebugGraphicsOptions(int)
//setDoubleBuffered(boolean)
//setEnabled(boolean)
//setFocusTraversalKeys(int, Set<? extends AWTKeyStroke>)
//setFont(Font)
//setForeground(Color)
//setInheritsPopupMenu(boolean)
//setInputMap(int, InputMap)
//setInputVerifier(InputVerifier)
//setMaximumSize(Dimension)
//setMinimumSize(Dimension)
//setNextFocusableComponent(Component)
//setOpaque(boolean)
//setPreferredSize(Dimension)
//setRequestFocusEnabled(boolean)
//setToolTipText(String)
//setTransferHandler(TransferHandler)
//setVerifyInputWhenFocusTarget(boolean)
//setVisible(boolean)
  }
}