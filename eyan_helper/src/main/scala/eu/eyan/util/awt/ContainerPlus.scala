package eu.eyan.util.awt

import java.awt.Container
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import java.awt.event.ContainerEvent
import java.awt.ComponentOrientation
import java.awt.Component
import java.awt.Font
import java.awt.LayoutManager
import java.awt.FocusTraversalPolicy
import java.awt.AWTKeyStroke
import scala.collection.JavaConverters._

object ContainerPlus {
  implicit class ContainerPlusImplicit[TYPE <: Container](container: TYPE) extends ComponentPlusImplicit(container) {
    def onComponentAdded(action: => Unit) = onComponentAddedEvent { e => action }
    def onComponentAddedEvent(action: ContainerEvent => Unit) = { container.addContainerListener(AwtHelper.onComponentAdded(action)); container }
    def onComponentRemoved(action: => Unit) = onComponentRemovedEvent { e => action }
    def onComponentRemovedEvent(action: ContainerEvent => Unit) = { container.addContainerListener(AwtHelper.onComponentRemoved(action)); container }
    override def componentOrientation(orientation: ComponentOrientation) = { container.applyComponentOrientation(orientation); container }
    def componentZOrder(component: Component, index: Int) = { container.setComponentZOrder(component, index); container }
    def focusCycleRoot(focusCycleRoot: Boolean) = { container.setFocusCycleRoot(focusCycleRoot); container }

    override def focusTraversalKeys(id: Int, keystrokes: Set[AWTKeyStroke]) = { container.setFocusTraversalKeys(id, keystrokes.asJava); container }
    def focusTraversalPolicy(policy: FocusTraversalPolicy) = { container.setFocusTraversalPolicy(policy); container }
    
    def focusTraversalPolicyProviderEnabled: TYPE = focusTraversalPolicyProviderEnabled(true)
    def focusTraversalPolicyProviderDisabled = focusTraversalPolicyProviderEnabled(false)
    def focusTraversalPolicyProviderEnabled(enabled: Boolean) = { container.setFocusTraversalKeysEnabled(enabled); container }
    
    override def font(font: Font) = { container.setFont(font); container }
    def layout(layout: LayoutManager) = { container.setLayout(layout); container }
  }
}