package eu.eyan.util.swing

import javax.swing.JWindow
import com.jgoodies.forms.layout.FormLayout
import java.awt.Window
import java.awt.Window
import java.awt.Window.Type
import com.jgoodies.forms.factories.CC
import java.awt.Component
import javax.swing.SwingUtilities
import java.awt.event.ComponentEvent
import java.awt.event.ComponentAdapter
import eu.eyan.util.awt.AwtHelper

object PopupWindow {
  val NAME_POPUP = "_Popup"
}

class PopupWindow(content: Component, owner: Component) {
  var wasCreated = false
  private var componentAdapter: () => Unit = null

  private var window: JWindow = null

  def showPopup = {
    if (!wasCreated) {
      val ancestor = SwingUtilities.getWindowAncestor(owner)

      window = new JWindow(ancestor)
      window.setName(PopupWindow.NAME_POPUP)
      window.setLayout(new FormLayout("f:p:g", "f:p:g"))
      window.setType(Type.POPUP)
      window.add(content, CC.xy(1, 1))

      // TODO later if it is necessary: create cleanup method to solve memory leak
      ancestor.addComponentListener(AwtHelper.componentMoved(componentAdapter))

      wasCreated = true
    }

    window.pack
    window.setFocusableWindowState(false)
    window.setAlwaysOnTop(true)
    window.setVisible(true)
    this
  }

  def setLocation(x: Int, y: Int) = { if (wasCreated) window.setLocation(x, y); this }

  def setWidth(width: Int) = { if (wasCreated) { window.setSize(width, window.getHeight); window.validate }; this }

  def isVisible = wasCreated && window.isVisible
  def isInvisible = !isVisible

  def setInvisible = { if (isVisible) window.setVisible(false); this }

  def onComponentMoved(action: () => Unit) = { componentAdapter = action; this }
}