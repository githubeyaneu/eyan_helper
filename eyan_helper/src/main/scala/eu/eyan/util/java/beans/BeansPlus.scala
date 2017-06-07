package eu.eyan.util.java.beans

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent

object BeansPlus {
  //PropertyChangeListener
  def onPropertyChange(action: PropertyChangeEvent => Unit) = new PropertyChangeListener() { override def propertyChange(e: PropertyChangeEvent) = action(e) }
}