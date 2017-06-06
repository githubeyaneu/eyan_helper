package eu.eyan.util.awt

import javax.swing.JLabel
import java.awt.Component
import java.awt.AWTEvent
import java.awt.event.MouseEvent
import eu.eyan.util.swing.JFramePlus

object ComponentPlus {

  implicit class ComponentPlusImplicit[TYPE <: Component](component: TYPE) {
    //  setAutoFocusTransferOnDisposal(boolean)
    //  setBackground(Color)
    //  setBounds(int, int, int, int)
    //  setBounds(Rectangle)
    //  setBoundsOp(int)
    //  setComponentOrientation(ComponentOrientation)
    //  setCursor(Cursor)
    //  setDropTarget(DropTarget)

    //  setEnabled(boolean)
    def enabled(enabled: Boolean) = { component.setEnabled(enabled); component }
    def enabled: TYPE = enabled(true)
    def disabled = enabled(false)

    //  setFocusable(boolean)
    def focusable(focusable: Boolean) = { component.setFocusable(focusable); component }
    def focusable: TYPE = focusable(true)
    def notFocusable = focusable(false)

    //  setFocusTraversalKeys(int, Set<? extends AWTKeyStroke>)
    //  setFocusTraversalKeys_NoIDCheck(int, Set<? extends AWTKeyStroke>)
    //  setFocusTraversalKeysEnabled(boolean)
    //  setFont(Font)
    //  setForeground(Color)
    //  setGraphicsConfiguration(GraphicsConfiguration)
    //  setIgnoreRepaint(boolean)
    //  setLocale(Locale)
    //  setLocation(int, int)
    //  setLocation(Point)
    //  setMaximumSize(Dimension)
    //  setMinimumSize(Dimension)

    //  setName(String)
    def name(name: String) = { component.setName(name); component }
    def withName(name: String) = this.name(name)

    //  setPreferredSize(Dimension)
    //  setSize(int, int)
    //  setSize(Dimension)

    //  setVisible(boolean)
    def visible(visible: Boolean) = { component.setVisible(visible); component }
    def visible: TYPE = visible(true)
    def invisible = visible(false)

    // TODO listeners
    def onDoubleClick(action: => Unit) = onDoubleClickEvent { e => action }
    def onDoubleClickEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.doubleClick(action)); component }

    def onTripleClick(action: => Unit) = onTripleClickEvent { e => action }
    def onTripleClickEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.tripleClick(action)); component }
  }
}