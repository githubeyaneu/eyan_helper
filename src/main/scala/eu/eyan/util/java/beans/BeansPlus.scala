package eu.eyan.util.java.beans

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import java.beans.VetoableChangeListener

object BeansPlus {
  //PropertyChangeListener
  def onPropertyChange(action: PropertyChangeEvent => Unit) = new PropertyChangeListener() { override def propertyChange(e: PropertyChangeEvent) = action(e) }
  //VetoableChangeListener
  def onVetoableChange(action: PropertyChangeEvent => Unit) = new VetoableChangeListener() { override def vetoableChange(e: PropertyChangeEvent) = action(e) }
}