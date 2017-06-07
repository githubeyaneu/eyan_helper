package eu.eyan.util.awt

import java.awt.Window
import eu.eyan.util.awt.ContainerPlus.ContainerPlusImplicit
import java.awt.event.WindowEvent

object WindowPlus {
  implicit class WindowPlusImplicit[TYPE <: Window](window: TYPE) extends ContainerPlusImplicit(window) {

    def onWindowOpened(action: => Unit) = onWindowOpenedEvent { e => action }
    def onWindowOpenedEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowOpened(action)); window }
    def onWindowClosing(action: => Unit) = onWindowClosingEvent { e => action }
    def onWindowClosingEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowClosing(action)); window }
    def onWindowClosed(action: => Unit) = onWindowClosedEvent { e => action }
    def onWindowClosedEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowClosed(action)); window }
    def onWindowIconified(action: => Unit) = onWindowIconifiedEvent { e => action }
    def onWindowIconifiedEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowIconified(action)); window }
    def onWindowDeiconified(action: => Unit) = onWindowDeiconifiedEvent { e => action }
    def onWindowDeiconifiedEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowDeiconified(action)); window }
    def onWindowActivated(action: => Unit) = onWindowActivatedEvent { e => action }
    def onWindowActivatedEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowActivated(action)); window }
    def onWindowDeactivated(action: => Unit) = onWindowDeactivatedEvent { e => action }
    def onWindowDeactivatedEvent(action: WindowEvent => Unit) = { window.addWindowListener(AwtHelper.onWindowDeactivated(action)); window }

    def onWindowStateChanged(action: => Unit) = onWindowStateChangedEvent { e => action }
    def onWindowStateChangedEvent(action: WindowEvent => Unit) = { window.addWindowStateListener(AwtHelper.onWindowStateChanged(action)); window }

    def onWindowGainedFocus(action: => Unit) = onWindowGainedFocusEvent { e => action }
    def onWindowGainedFocusEvent(action: WindowEvent => Unit) = { window.addWindowFocusListener(AwtHelper.onWindowGainedFocus(action)); window }
    def onWindowLostFocus(action: => Unit) = onWindowLostFocusEvent { e => action }
    def onWindowLostFocusEvent(action: WindowEvent => Unit) = { window.addWindowFocusListener(AwtHelper.onWindowLostFocus(action)); window }

    def packAndSetVisible = { window.pack; window.visible; window }
  }
}