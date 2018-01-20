package eu.eyan.util.awt

import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.AWTEventListener
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ContainerAdapter
import java.awt.event.ContainerEvent
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.HierarchyBoundsAdapter
import java.awt.event.HierarchyEvent
import java.awt.event.HierarchyListener
import java.awt.event.InputMethodEvent
import java.awt.event.InputMethodListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.awt.event.TextEvent
import java.awt.event.TextListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import eu.eyan.log.Log
import java.awt.event.WindowFocusListener
import java.awt.event.WindowStateListener
import java.awt.event.WindowListener

// TODO: rename to AwtPlus...
// TODO: set private[awt]
object AwtHelper {
  //ActionListener
  def onActionPerformed(action: ActionEvent => Unit) = new ActionListener() { override def actionPerformed(e: ActionEvent) = action(e) }

  //AdjustmentListener
  def onAdjustmentValueChanged(action: AdjustmentEvent => Unit) = new AdjustmentListener() { override def adjustmentValueChanged(e: AdjustmentEvent) = action(e) }

  //AWTEventListener
  def onEventDispatched(action: AWTEvent => Unit) = new AWTEventListener() { override def eventDispatched(e: AWTEvent) = action(e) }

  //ComponentAdapter
  //ComponentListener
  def onComponentResized(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentResized(e: ComponentEvent) = action(e) }
  def onComponentMoved(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentMoved(e: ComponentEvent) = action(e) }
  def onComponentShown(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentShown(e: ComponentEvent) = action(e) }
  def onComponentHidden(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentHidden(e: ComponentEvent) = action(e) }

  //ContainerAdapter
  //ContainerListener
  def onComponentAdded(action: ContainerEvent => Unit) = new ContainerAdapter { override def componentAdded(e: ContainerEvent) = action(e) }
  def onComponentRemoved(action: ContainerEvent => Unit) = new ContainerAdapter { override def componentRemoved(e: ContainerEvent) = action(e) }

  //FocusAdapter
  //FocusListener
  def onFocusGained(action: FocusEvent => Unit) = new FocusAdapter { override def focusGained(e: FocusEvent) = action(e) }
  def onFocusLost(action: FocusEvent => Unit) = new FocusAdapter { override def focusLost(e: FocusEvent) = action(e) }

  //HierarchyBoundsAdapter
  //HierarchyBoundsListener
  def onAncestorMoved(action: HierarchyEvent => Unit) = new HierarchyBoundsAdapter { override def ancestorMoved(e: HierarchyEvent) = action(e) }
  def onAncestorResized(action: HierarchyEvent => Unit) = new HierarchyBoundsAdapter { override def ancestorResized(e: HierarchyEvent) = action(e) }

  //HierarchyListener
  def onHierarchyChanged(action: HierarchyEvent => Unit) = new HierarchyListener { override def hierarchyChanged(e: HierarchyEvent) = action(e) }

  //InputMethodListener
  abstract class InputMethodAdapter extends InputMethodListener {
    override def inputMethodTextChanged(e: InputMethodEvent) = {}
    override def caretPositionChanged(e: InputMethodEvent) = {}
  }
  def onInputMethodTextChanged(action: InputMethodEvent => Unit) = new InputMethodAdapter { override def inputMethodTextChanged(e: InputMethodEvent) = action(e) }
  def onCaretPositionChanged(action: InputMethodEvent => Unit) = new InputMethodAdapter { override def caretPositionChanged(e: InputMethodEvent) = action(e) }

  //ItemListener
  def onItemStateChanged(action: ItemEvent => Unit) = new ItemListener() { override def itemStateChanged(e: ItemEvent) = action(e) }

  //KeyAdapter
  //KeyListener
  def onKeyTyped(action: KeyEvent => Unit) = new KeyAdapter { override def keyTyped(e: KeyEvent) = action(e) }
  def onKeyPressed(action: KeyEvent => Unit) = new KeyAdapter { override def keyPressed(e: KeyEvent) = action(e) }
  def onKeyReleased(action: KeyEvent => Unit) = new KeyAdapter { override def keyReleased(e: KeyEvent) = action(e) }

  //MouseAdapter
  //MouseListener
  def onMouseClicked(action: MouseEvent => Unit, clickCount: Int = 1) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount == clickCount) action(e) }
  def onMousePressed(action: MouseEvent => Unit) = new MouseAdapter { override def mousePressed(e: MouseEvent) = action(e) }
  def onMouseReleased(action: MouseEvent => Unit) = new MouseAdapter { override def mouseReleased(e: MouseEvent) = action(e) }
  def onMouseEntered(action: MouseEvent => Unit) = new MouseAdapter { override def mouseEntered(e: MouseEvent) = action(e) }
  def onMouseExited(action: MouseEvent => Unit) = new MouseAdapter { override def mouseExited(e: MouseEvent) = action(e) }

  def onClicked(action: MouseEvent => Unit) = onMouseClicked(action, 1)
  def onDoubleClick(action: MouseEvent => Unit) = onMouseClicked(action, 2)
  def onTripleClick(action: MouseEvent => Unit) = onMouseClicked(action, 3)

  //MouseMotionAdapter
  //MouseMotionListener
  def onMouseDragged(action: MouseEvent => Unit) = new MouseMotionAdapter { override def mouseDragged(e: MouseEvent) = action(e) }
  def onMouseMoved(action: MouseEvent => Unit) = new MouseMotionAdapter { override def mouseMoved(e: MouseEvent) = action(e) }

  //MouseWheelListener
  def onMouseWheelMoved(action: MouseWheelEvent => Unit) = new MouseWheelListener { override def mouseWheelMoved(e: MouseWheelEvent) = action(e) }

  //TextListener
  def onTextValueChanged(action: TextEvent => Unit) = new TextListener { override def textValueChanged(e: TextEvent) = action(e) }

  //WindowAdapter
  //WindowListener
  //WindowStateListener
  //WindowFocusListener
  def onWindowOpened(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowOpened(e: WindowEvent) = action(e) }
  def onWindowClosing(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowClosing(e: WindowEvent) = action(e) }
  def onWindowClosed(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowClosed(e: WindowEvent) = action(e) }
  def onWindowIconified(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowIconified(e: WindowEvent) = action(e) }
  def onWindowDeiconified(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowDeiconified(e: WindowEvent) = action(e) }
  def onWindowActivated(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowActivated(e: WindowEvent) = action(e) }
  def onWindowDeactivated(action: WindowEvent => Unit):WindowListener = new WindowAdapter { override def windowDeactivated(e: WindowEvent) = action(e) }
  def onWindowStateChanged(action: WindowEvent => Unit):WindowStateListener = new WindowAdapter { override def windowStateChanged(e: WindowEvent) = action(e) }
  def onWindowGainedFocus(action: WindowEvent => Unit):WindowFocusListener = new WindowAdapter { override def windowGainedFocus(e: WindowEvent) = action(e) }
  def onWindowLostFocus(action: WindowEvent => Unit):WindowFocusListener = new WindowAdapter { override def windowLostFocus(e: WindowEvent) = action(e) }

  def screenSize = Toolkit.getDefaultToolkit.getScreenSize
  
  
  def newRunnable(runnable: () => Unit) = new Runnable() { override def run() = runnable() }
  def runnable(runnable: => Unit) = new Runnable() { override def run() = runnable }

  def tryToEnlargeWindow(window: Window) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    val origPos = window.getBounds
    Log.debug("orig: " + origPos)
    window.pack
    val newPos = window.getBounds
    // Fuck hack :(
    if (newPos.y + newPos.height > screenSize.height - 40) {
      newPos.height = screenSize.height - 40 - newPos.y
      newPos.width = newPos.width + 30
      window.setBounds(newPos)
    }
    Log.debug("new: " + newPos)
  }
}