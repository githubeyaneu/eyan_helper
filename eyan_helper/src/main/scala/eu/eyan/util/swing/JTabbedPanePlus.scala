package eu.eyan.util.swing

import javax.swing.JTabbedPane
import eu.eyan.util.awt.remember.RememberInRegistry
import javax.swing.event.ChangeEvent

object JTabbedPanePlus {
  implicit class JTabbedPaneImplicit[TYPE <: JTabbedPane](jTabbedPane: TYPE) extends RememberInRegistry[TYPE] {
    
    def onSelectionChanged(action: => Unit): TYPE = onSelectionChangedEvent(e => action)
    def onSelectionChangedEvent(action: ChangeEvent => Unit) = { jTabbedPane.addChangeListener(SwingPlus.onStateChanged(action)); jTabbedPane }
    
    protected def rememberComponent = jTabbedPane
    protected def rememberEventListener(action: => Unit) = onSelectionChanged(action)
    protected def rememberValueGet = jTabbedPane.getSelectedIndex.toString
    protected def rememberValueSet(value: String) = jTabbedPane.setSelectedIndex(value.toInt)
  }
}