package eu.eyan.util.awt

import java.awt.Window
import eu.eyan.util.awt.ContainerPlus.ContainerPlusImplicit
import java.awt.event.WindowEvent
import java.awt.Dialog.ModalExclusionType
import java.awt.BufferCapabilities
import java.awt.Color
import java.awt.Rectangle
import java.awt.Cursor
import java.awt.Image
import scala.collection.JavaConverters._
import java.awt.Component
import java.awt.Dimension
import java.awt.Dialog
import java.awt.Shape
import java.awt.Window.Type
import java.awt.Frame
import java.awt.GraphicsEnvironment

object WindowPlus {
  implicit class WindowPlusImplicit[TYPE <: Window](window: TYPE) extends ContainerPlusImplicit(window) {

    // Listeners
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
    def onWindowStateChanged_ICONIFIED(action: => Unit) =  onWindowStateChangedEvent { e => if (e.getNewState == Frame.ICONIFIED) action }
    def onWindowStateChanged_NORMAL(action: => Unit) =  onWindowStateChangedEvent { e => if (e.getNewState == Frame.NORMAL) action }
    def onWindowStateChanged_MAXIMIZED_BOTH(action: => Unit) =  onWindowStateChangedEvent { e => if (e.getNewState == Frame.MAXIMIZED_BOTH) action }
    def onWindowStateChangedEvent(action: WindowEvent => Unit) = { window.addWindowStateListener(AwtHelper.onWindowStateChanged(action)); window }
    //TODO: others.... maximized vert...
    

    def onWindowGainedFocus(action: => Unit) = onWindowGainedFocusEvent { e => action }
    def onWindowGainedFocusEvent(action: WindowEvent => Unit) = { window.addWindowFocusListener(AwtHelper.onWindowGainedFocus(action)); window }
    def onWindowLostFocus(action: => Unit) = onWindowLostFocusEvent { e => action }
    def onWindowLostFocusEvent(action: WindowEvent => Unit) = { window.addWindowFocusListener(AwtHelper.onWindowLostFocus(action)); window }

    // setters
    def bufferStrategy(numBuffers: Int) = { window.createBufferStrategy(numBuffers); window }
    def bufferStrategy(numBuffers: Int, capa: BufferCapabilities) = { window.createBufferStrategy(numBuffers, capa); window }
    def pack = { window.pack(); window }
    def alwaysOnTop(enabled: Boolean) = { window.setAlwaysOnTop(enabled); window }
    def alwaysOnTop: TYPE = alwaysOnTop(true)
    def alwaysOnTopDisabled = alwaysOnTop(false)
    def autoRequestFocus(enabled: Boolean) = { window.setAutoRequestFocus(enabled); window }
    def autoRequestFocus: TYPE = autoRequestFocus(true)
    def autoRequestFocusDiasabled = autoRequestFocus(false)
    def background(color: Color) = { window.setBackground(color); window }
    override def bounds(x: Int, y: Int, width: Int, height: Int) = { window.setBounds(x, y, width, height); window }
    override def bounds(rect: Rectangle) = { window.setBounds(rect); window }
    override def cursor(cursor: Cursor) = { window.setCursor(cursor); window }
    def focusableWindowState(enabled: Boolean) = { window.setFocusableWindowState(enabled); window }
    def focusableWindowState: TYPE = focusableWindowState(true)
    def focusableWindowStateDisabled = focusableWindowState(false)
    def iconImage(image: Image) = { window.setIconImage(image); window }
    def iconImages(images: List[Image]) = { window.setIconImages(images.asJava); window }
    def locationByPlatform(enabled: Boolean) = { window.setLocationByPlatform(enabled); window }
    def locationByPlatform: TYPE = locationByPlatform(true)
    def locationByPlatformDisabled = locationByPlatform(false)
    def locationRelativeTo(component: Component) = { window.setLocationRelativeTo(component); window }
    override def minimumSize(dimension: Dimension) = { window.setMinimumSize(dimension); window }
    def modalExclusionType(modalExclusionType: ModalExclusionType) = { window.setModalExclusionType(modalExclusionType); window }
    def modalExclusionType_NoExclude = modalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE)
    def modalExclusionType_ApplicationExclude = modalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)
    def modalExclusionType_ToolkitExclude = modalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)
    def opacity(opacity:Float) = {window.setOpacity(opacity); window}
    def shape(shape:Shape) = {window.setShape(shape); window}
    def typee(typee: Type) = {window.setType(typee); window}

    // own
    def packAndSetVisible = { window.pack; window.visible; window }
    
    def maximize = {
      window.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment.getMaximumWindowBounds)
      window
    }
  }
}