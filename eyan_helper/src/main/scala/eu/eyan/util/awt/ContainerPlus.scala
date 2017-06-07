package eu.eyan.util.awt

import java.awt.Container
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import java.awt.event.ContainerEvent

object ContainerPlus {
  implicit class ContainerPlusImplicit[TYPE <: Container](container: TYPE) extends ComponentPlusImplicit(container) {
    def onComponentAdded(action: => Unit) = onComponentAddedEvent { e => action }
    def onComponentAddedEvent(action: ContainerEvent => Unit) = { container.addContainerListener(AwtHelper.onComponentAdded(action)); container }
    def onComponentRemoved(action: => Unit) = onComponentRemovedEvent { e => action }
    def onComponentRemovedEvent(action: ContainerEvent => Unit) = { container.addContainerListener(AwtHelper.onComponentRemoved(action)); container }
  }
}