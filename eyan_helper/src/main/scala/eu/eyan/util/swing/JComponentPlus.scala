package eu.eyan.util.swing

import javax.swing.JComponent
import eu.eyan.util.awt.ContainerPlus.ContainerPlusImplicit
import eu.eyan.util.java.beans.BeansPlus
import java.beans.PropertyChangeEvent
import javax.swing.event.AncestorEvent
import javax.swing.KeyStroke
import java.awt.event.ActionListener
import java.awt.Rectangle
import javax.swing.ActionMap
import java.awt.Color
import javax.swing.border.Border
import javax.swing.JPopupMenu
import javax.swing.DebugGraphics
import javax.swing.DebugGraphics
import java.awt.AWTKeyStroke
import scala.collection.JavaConverters._
import java.awt.Font
import javax.swing.InputMap
import javax.swing.InputVerifier
import javax.swing.TransferHandler
import javax.swing.AbstractButton
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit

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

    def focus = { jComponent.requestFocus(); jComponent }
    def grabFocus = { jComponent.grabFocus(); jComponent }
    def clientProperty(key: Object, value: Object) = { jComponent.putClientProperty(key, value); jComponent }
    def keyboardAction(action: ActionListener, command: String, key: KeyStroke, condition: Int) = { jComponent.registerKeyboardAction(action, command, key, condition); jComponent }
    def keyboardAction(action: ActionListener, key: KeyStroke, condition: Int) = { jComponent.registerKeyboardAction(action, key, condition); jComponent }
    def resetKeyboardActions = { jComponent.resetKeyboardActions(); jComponent }
    def scrollRectToVisible(rect: Rectangle) = { jComponent.scrollRectToVisible(rect); jComponent }
    def actionMap(actionMap: ActionMap) = { jComponent.setActionMap(actionMap); jComponent }
    def alignmentX(alignmentX: Float) = { jComponent.setAlignmentX(alignmentX); jComponent }
    def alignmentY(alignmentY: Float) = { jComponent.setAlignmentY(alignmentY); jComponent }
    def autoscrolls(autoscrolls: Boolean) = { jComponent.setAutoscrolls(autoscrolls); jComponent }
    def autoscrolls: TYPE = autoscrolls(true)
    def autoscrollsDisabled: TYPE = autoscrolls(false)
    def background(color: Color) = { jComponent.setBackground(color); jComponent }
    //TODO default borders
    def border(border: Border) = { jComponent.setBorder(border); jComponent }
    def popupMenu(popup: JPopupMenu) = { jComponent.setComponentPopupMenu(popup); jComponent }
    def debugGraphicsOptions(debugOptions: Int) = { jComponent.setDebugGraphicsOptions(debugOptions); jComponent }
    def debugGraphics_Log(debugOptions: Int) = debugGraphicsOptions(DebugGraphics.LOG_OPTION)
    def debugGraphics_Buffered(debugOptions: Int) = debugGraphicsOptions(DebugGraphics.BUFFERED_OPTION)
    def debugGraphics_Flash(debugOptions: Int) = debugGraphicsOptions(DebugGraphics.FLASH_OPTION)
    def debugGraphics_None(debugOptions: Int) = debugGraphicsOptions(DebugGraphics.NONE_OPTION)
    def doubleBuffered(enabled: Boolean) = { jComponent.setDoubleBuffered(enabled); jComponent }
    def doubleBuffered: TYPE = doubleBuffered(true)
    def doubleBufferedDisabled = doubleBuffered(false)
    override def enabled(enabled: Boolean) = { jComponent.setEnabled(enabled); jComponent }
    override def enabled: TYPE = enabled(true)
    override def disabled = enabled(false)
    override def focusTraversalKeys(id: Int, keystrokes: Set[AWTKeyStroke]) = { jComponent.setFocusTraversalKeys(id, keystrokes.asJava); jComponent }
    override def font(font: Font) = { jComponent.setFont(font); jComponent }
    override def foreground(color: Color) = { jComponent.setForeground(color); jComponent }
    def inheritsPopupMenu(inherits: Boolean) = { jComponent.setInheritsPopupMenu(inherits); jComponent }
    def inheritsPopupMenu: TYPE = inheritsPopupMenu(true)
    def inheritsPopupMenuDisabled = inheritsPopupMenu(false)
    def inputMap(condition: Int, map: InputMap) = { jComponent.setInputMap(condition, map); jComponent }
    def inputVerifier(inputVerifier: InputVerifier) = { jComponent.setInputVerifier(inputVerifier); jComponent }
    def opaque(isOpaque: Boolean) = { jComponent.setOpaque(isOpaque); jComponent }
    def opaque: TYPE = opaque(true)
    def opaqueDisabled: TYPE = opaque(false)
    def requestFocusEnabled(requestFocusEnabled: Boolean) = { jComponent.setRequestFocusEnabled(requestFocusEnabled); jComponent }
    def requestFocusEnabled: TYPE = requestFocusEnabled(true)
    def requestFocusDisabled: TYPE = requestFocusEnabled(false)
    def tooltipText(text: String) = { jComponent.setToolTipText(text); jComponent }
    def transferHandler(handler: TransferHandler) = { jComponent.setTransferHandler(handler); jComponent }
    def f(verifyInputWhenFocusTarget: Boolean) = { jComponent.setVerifyInputWhenFocusTarget(verifyInputWhenFocusTarget); jComponent }
    override def visible(enabled: Boolean) = { jComponent.setVisible(enabled); jComponent }
    override def visible: TYPE = visible(true)
    def notVisible: TYPE = visible(false)
    def hide: TYPE = notVisible
    override def invisible = notVisible

    def enabledDependsOn(buttons: AbstractButton*) = { buttons.foreach(_.onStateChange(jComponent.setEnabled(!buttons.exists(_.notSelected)))); jComponent }
  }
}