package eu.eyan.util.awt

import java.awt.Component
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File

import eu.eyan.log.Log

// TODO delete Swing stuff or move it to SwingPlus
import javax.swing.JFileChooser
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.event.ItemListener
import java.awt.event.ItemEvent
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent

object AwtHelper {
  val DESKTOP_LEFT = 0
  val DESKTOP_TOP = 0

  //DropTargetListener
  class DropTargetAdapter extends java.awt.dnd.DropTargetAdapter { def drop(e: DropTargetDropEvent) = {} }
  def onDragEnter(action: DropTargetDragEvent => Unit) = new DropTargetAdapter() { override def dragEnter(e: DropTargetDragEvent) = action(e) }
  def onDragOver(action: DropTargetDragEvent => Unit) = new DropTargetAdapter() { override def dragOver(e: DropTargetDragEvent) = action(e) }
  def onDropActionChanged(action: DropTargetDragEvent => Unit) = new DropTargetAdapter() { override def dropActionChanged(e: DropTargetDragEvent) = action(e) }
  def onDragExit(action: DropTargetEvent => Unit) = new DropTargetAdapter() { override def dragExit(e: DropTargetEvent) = action(e) }
  def onDrop(action: DropTargetDropEvent => Unit) = new DropTargetAdapter() { override def drop(e: DropTargetDropEvent) = action(e) }

  //ActionListener
  def onActionPerformed(action: ActionEvent => Unit) = new ActionListener() { override def actionPerformed(e: ActionEvent) = action(e) }

  //AdjustmentListener
  //AWTEventListener

  //ComponentAdapter
  //ComponentListener
  def onComponentResized(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentResized(e: ComponentEvent) = action(e) }
  def onComponentMoved(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentMoved(e: ComponentEvent) = action(e) }
  def onComponentShown(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentShown(e: ComponentEvent) = action(e) }
  def onComponentHidden(action: ComponentEvent => Unit) = new ComponentAdapter { override def componentHidden(e: ComponentEvent) = action(e) }

  //ContainerAdapter
  //ContainerListener
  //FocusAdapter
  //FocusListener
  //HierarchyBoundsAdapter
  //HierarchyBoundsListener
  //HierarchyListener
  //InputMethodListener

  //ItemListener
  def onItemStateChanged(action: ItemEvent => Unit) = new ItemListener() { override def itemStateChanged(e: ItemEvent) = action(e) }

  //KeyAdapter
  //KeyListener
  //MouseAdapter
  //MouseListener
  def mouseClick(action: () => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount == 1) action() }
  def doubleClick(action: MouseEvent => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount == 2) action(e) }
  def tripleClick(action: MouseEvent => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount == 3) action(e) }
  def mouseReleased(action: () => Unit) = new MouseAdapter { override def mouseReleased(e: MouseEvent) = action() }
  def mousePressed(action: () => Unit) = new MouseAdapter { override def mousePressed(e: MouseEvent) = action() }

  //MouseMotionAdapter
  //MouseMotionListener
  //MouseWheelListener
  //TextEvent
  //TextListener

  //WindowAdapter
  //WindowListener
  def newWindowClosingEvent(action: WindowEvent => Unit) = new WindowAdapter() { override def windowClosing(e: WindowEvent) = action(e) }

  //WindowFocusListener
  //WindowStateListener

  def newRunnable(runnable: () => Unit) = new Runnable() { override def run() = runnable() }

  def chooseFile(action: File => Unit, extension: String = "") = {
    val fc = new JFileChooser
    if (!extension.isEmpty) fc.setFileFilter(new FileNameExtensionFilter(extension + " files", extension))
    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) action(fc.getSelectedFile)
  }

  def docListener(action: () => Unit) = new DocumentListener() {
    override def insertUpdate(e: DocumentEvent) = action()
    override def removeUpdate(e: DocumentEvent) = {}
    override def changedUpdate(e: DocumentEvent) = action()
  }

  def positionToCenter[A <: Component](component: A): A = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    val width = component.getSize().width
    val height = component.getSize().height
    component.setSize(width, height)
    component.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2)
    component
  }

  def positionToLeft(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    component.setSize(screenSize.width / 2, screenSize.height - 30)
    component.setLocation(DESKTOP_LEFT, DESKTOP_TOP)
  }

  def positionToRight(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    component.setSize(screenSize.width / 2, screenSize.height - 30)
    component.setLocation(screenSize.width / 2, DESKTOP_TOP)
  }

  def runInWorker(work: () => Unit, doAtDone: () => Unit) = {
    new SwingWorker[Void, Void]() {
      override def doInBackground() = { work.apply(); null }
      override def done() = doAtDone.apply()
    }.execute()
  }

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