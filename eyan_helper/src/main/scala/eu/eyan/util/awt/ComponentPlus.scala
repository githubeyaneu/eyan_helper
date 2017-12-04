package eu.eyan.util.awt

import javax.swing.JLabel
import java.awt.Component
import java.awt.AWTEvent
import java.awt.event.MouseEvent
import eu.eyan.util.swing.JFramePlus
import java.awt.Toolkit
import java.awt.event.HierarchyListener
import java.awt.event.MouseWheelListener
import java.awt.event.InputMethodEvent
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseAdapter
import java.awt.event.HierarchyEvent
import java.awt.event.MouseMotionAdapter
import java.awt.event.KeyEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.FocusEvent
import java.awt.dnd.DropTargetDragEvent
import java.awt.event.ComponentEvent
import java.awt.event.HierarchyBoundsAdapter
import java.awt.event.FocusAdapter
import java.awt.event.ComponentAdapter
import java.awt.event.KeyAdapter
import eu.eyan.util.java.beans.BeansPlus
import java.beans.PropertyChangeEvent
import eu.eyan.util.awt.dnd.DndPlus
import java.awt.dnd.DropTarget
import eu.eyan.util.awt.dnd.DnD_FileDropTarget
import java.io.File
import java.awt.dnd.DnDConstants
import java.awt.datatransfer.DataFlavor
import eu.eyan.log.Log
import java.awt.Color
import java.awt.Rectangle
import java.awt.ComponentOrientation
import java.awt.Cursor
import java.awt.Font
import java.util.Locale
import java.awt.Point
import java.awt.Dimension
import java.awt.AWTKeyStroke
import scala.collection.JavaConverters._
import javax.swing.SwingUtilities
import javax.swing.JFrame
import javax.swing.SwingUtilities
import java.awt.Container
import java.io.StringReader
import org.apache.commons.compress.utils.IOUtils

object ComponentPlus {

  implicit class ComponentPlusImplicit[TYPE <: Component](component: TYPE) {
    def size = component.getSize
    def width = size.width
    def height = size.height
    def screenSize = AwtHelper.screenSize

    def positionToCenter = {
      component.setSize(width, height)
      component.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2)
      component
    }

    val SCREEN_LEFT = 0
    val SCREEN_TOP = 0

    def positionToLeft = {
      component.setSize(screenSize.width / 2, screenSize.height - 30)
      component.setLocation(SCREEN_LEFT, SCREEN_TOP)
    }

    def positionToRight = {
      component.setSize(screenSize.width / 2, screenSize.height - 30)
      component.setLocation(screenSize.width / 2, SCREEN_TOP)
    }

    ////////////////////   METHODS   //////////////////////////
    def backgroundColor(color: Color) = { component.setBackground(color); component }
    def bounds(x: Int, y: Int, width: Int, height: Int) = { component.setBounds(x, y, width, height); component }
    def bounds(rectangle: Rectangle) = { component.setBounds(rectangle); component }
    def componentOrientation(orientation: ComponentOrientation) = { component.setComponentOrientation(orientation); component }
    def cursor(cursor: Cursor) = { component.setCursor(cursor); component }

    def cursor_DEFAULT_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
    def cursor_CROSSHAIR_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR))
    def cursor_TEXT_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR))
    def cursor_WAIT_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
    def cursor_SW_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))
    def cursor_SE_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))
    def cursor_NW_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))
    def cursor_NE_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))
    def cursor_N_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
    def cursor_S_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
    def cursor_W_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))
    def cursor_E_RESIZE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
    def cursor_HAND_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
    def cursor_MOVE_CURSOR = cursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))

    def dropTarget(dropTarget: DropTarget) = { component.setDropTarget(dropTarget); component }

    def enabled(enabled: Boolean) = { component.setEnabled(enabled); component }
    def enabled: TYPE = enabled(true)
    def disabled = enabled(false)

    def focusable(focusable: Boolean) = { component.setFocusable(focusable); component }
    def focusable: TYPE = focusable(true)
    def notFocusable = focusable(false)

    def focusTraversalKeys(id: Int, keystrokes: Set[AWTKeyStroke]) = { component.setFocusTraversalKeys(id, keystrokes.asJava); component }
    def focusTraversalKeysEnabled(enabled: Boolean) = { component.setFocusTraversalKeysEnabled(enabled); component }

    def font(font: Font) = { component.setFont(font); component }
    def foreground(color: Color) = { component.setForeground(color); component }
    def ignoreRepaint(ignore: Boolean) = { component.setIgnoreRepaint(ignore); component }
    def locale(locale: Locale) = { component.setLocale(locale); component }
    def location(x: Int, y: Int) = { component.setLocation(x, y); component }
    def location(point: Point) = { component.setLocation(point); component }
    def maximumSize(dimension: Dimension) = { component.setMaximumSize(dimension); component }
    def minimumSize(dimension: Dimension) = { component.setMinimumSize(dimension); component }

    def name(name: String) = { component.setName(name); component }

    def preferredSize(dimension: Dimension) = { component.setPreferredSize(dimension); component }
    def size(width: Int, height: Int) = { component.setSize(width, height); component }
    def size(dimension: Dimension) = { component.setSize(dimension); component }

    def visible(visible: Boolean) = { component.setVisible(visible); component }
    def visible: TYPE = visible(true)
    def invisible = visible(false)

    //////////////////////////////////////////    Awt Listeners     //////////////////////////////////////////
    def onComponentResized(action: => Unit) = onComponentResizedEvent { e => action }
    def onComponentResizedEvent(action: ComponentEvent => Unit) = { component.addComponentListener(AwtHelper.onComponentResized(action)); component }
    def onComponentMoved(action: => Unit) = onComponentMovedEvent { e => action }
    def onComponentMovedEvent(action: ComponentEvent => Unit) = { component.addComponentListener(AwtHelper.onComponentMoved(action)); component }
    def onComponentShown(action: => Unit) = onComponentShownEvent { e => action }
    def onComponentShownEvent(action: ComponentEvent => Unit) = { component.addComponentListener(AwtHelper.onComponentShown(action)); component }
    def onComponentHidden(action: => Unit) = onComponentHiddenEvent { e => action }
    def onComponentHiddenEvent(action: ComponentEvent => Unit) = { component.addComponentListener(AwtHelper.onComponentHidden(action)); component }

    def onFocusGained(action: => Unit) = onFocusGainedEvent { e => action }
    def onFocusGainedEvent(action: FocusEvent => Unit) = { component.addFocusListener(AwtHelper.onFocusGained(action)); component }
    def onFocusLost(action: => Unit) = onFocusLostEvent { e => action }
    def onFocusLostEvent(action: FocusEvent => Unit) = { component.addFocusListener(AwtHelper.onFocusLost(action)); component }

    def onHierarchyBoundsAncestorMoved(action: => Unit) = onHierarchyBoundsAncestorMovedEvent { e => action }
    def onHierarchyBoundsAncestorMovedEvent(action: HierarchyEvent => Unit) = { component.addHierarchyBoundsListener(AwtHelper.onAncestorMoved(action)); component }
    def onHierarchyBoundsAncestorResized(action: => Unit) = onHierarchyBoundsAncestorResizedEvent { e => action }
    def onHierarchyBoundsAncestorResizedEvent(action: HierarchyEvent => Unit) = { component.addHierarchyBoundsListener(AwtHelper.onAncestorResized(action)); component }

    def onHierarchyChanged(action: => Unit) = onHierarchyChangedEvent { e => action }
    def onHierarchyChangedEvent(action: HierarchyEvent => Unit) = { component.addHierarchyListener(AwtHelper.onHierarchyChanged(action)); component }

    def onInputMethodTextChanged(action: => Unit) = onInputMethodTextChangedEvent { e => action }
    def onInputMethodTextChangedEvent(action: InputMethodEvent => Unit) = { component.addInputMethodListener(AwtHelper.onInputMethodTextChanged(action)); component }
    def onCaretPositionChanged(action: => Unit) = onCaretPositionChangedEvent { e => action }
    def onCaretPositionChangedEvent(action: InputMethodEvent => Unit) = { component.addInputMethodListener(AwtHelper.onCaretPositionChanged(action)); component }

    def onKeyTyped(action: => Unit) = onKeyTypedEvent { e => action }
    def onKeyTypedEvent(action: KeyEvent => Unit) = { component.addKeyListener(AwtHelper.onKeyTyped(action)); component }
    def onKeyPressed(action: => Unit) = onKeyPressedEvent { e => action }
    def onKeyPressedEvent(action: KeyEvent => Unit) = { component.addKeyListener(AwtHelper.onKeyPressed(action)); component }
    def onKeyReleased(action: => Unit) = onKeyReleasedEvent { e => action }
    def onKeyReleasedEvent(action: KeyEvent => Unit) = { component.addKeyListener(AwtHelper.onKeyReleased(action)); component }

    def onMouseClicked(action: => Unit, clickCount: Int = 1) = onMouseClickedEvent { e => action }
    def onMouseClickedEvent(action: MouseEvent => Unit, clickCount: Int = 1) = { component.addMouseListener(AwtHelper.onMouseClicked(action)); component }
    def onMousePressed(action: => Unit) = onMousePressedEvent { e => action }
    def onMousePressedEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onMousePressed(action)); component }
    def onMouseReleased(action: => Unit) = onMouseReleasedEvent { e => action }
    def onMouseReleasedEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onMouseReleased(action)); component }
    def onMouseEntered(action: => Unit) = onMouseEnteredEvent { e => action }
    def onMouseEnteredEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onMouseEntered(action)); component }
    def onMouseExited(action: => Unit) = onMouseExitedEvent { e => action }
    def onMouseExitedEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onMouseExited(action)); component }

    def onClicked(action: => Unit) = onClickedEvent { e => action }
    def onClickedEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onClicked(action)); component }
    def onDoubleClick(action: => Unit) = onDoubleClickEvent { e => action }
    def onDoubleClickEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onDoubleClick(action)); component }
    def onTripleClick(action: => Unit) = onTripleClickEvent { e => action }
    def onTripleClickEvent(action: MouseEvent => Unit) = { component.addMouseListener(AwtHelper.onTripleClick(action)); component }

    def onMouseDragged(action: => Unit) = onMouseDraggedEvent { e => action }
    def onMouseDraggedEvent(action: MouseEvent => Unit) = { component.addMouseMotionListener(AwtHelper.onMouseDragged(action)); component }
    def onMouseMoved(action: => Unit) = onMouseMovedEvent { e => action }
    def onMouseMovedEvent(action: MouseEvent => Unit) = { component.addMouseMotionListener(AwtHelper.onMouseMoved(action)); component }

    def onMouseWheelMoved(action: => Unit) = onMouseWheelMovedEvent { e => action }
    def onMouseWheelMovedEvent(action: MouseWheelEvent => Unit) = { component.addMouseWheelListener(AwtHelper.onMouseWheelMoved(action)); component }

    //////////////////////////////////////////    Bean Listeners     //////////////////////////////////////////
    def onPropertyChange(action: => Unit) = onPropertyChangeEvent { e => action }
    def onPropertyChangeEvent(action: PropertyChangeEvent => Unit) = { component.addPropertyChangeListener(BeansPlus.onPropertyChange(action)); component }
    def onPropertyChange(propertyName: String, action: => Unit) = onPropertyChangeEvent(propertyName, { e => action })
    def onPropertyChangeEvent(propertyName: String, action: PropertyChangeEvent => Unit) = { component.addPropertyChangeListener(propertyName, BeansPlus.onPropertyChange(action)); component }

    //////////////////////////////////////////    DND Listeners     //////////////////////////////////////////
    def onDragEnter(action: => Unit) = onDragEnterEvent { e => action }
    def onDragEnterEvent(action: DropTargetDragEvent => Unit) = { DndPlus.onDragEnter(component, action); component }

    def onDragOver(action: => Unit) = onDragOverEvent { e => action }
    def onDragOverEvent(action: DropTargetDragEvent => Unit) = { DndPlus.onDragOver(component, action); component }

    def onDropActionChanged(action: => Unit) = onDropActionChangedEvent { e => action }
    def onDropActionChangedEvent(action: DropTargetDragEvent => Unit) = { DndPlus.onDropActionChanged(component, action); component }

    def onDragExit(action: => Unit) = onDragExitEvent { e => action }
    def onDragExitEvent(action: DropTargetEvent => Unit) = { DndPlus.onDragExit(component, action); component }

    def onDrop(action: => Unit) = onDropEvent { e => action }
    def onDropEvent(action: DropTargetDropEvent => Unit) = { DndPlus.onDrop(component, action); component }

    def onDropFile(action: File => Unit) = {
      onDropEvent { evt =>
        this.synchronized {
          try {
            evt.acceptDrop(DnDConstants.ACTION_COPY)
            val transferable = evt.getTransferable()
            val transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor)
            val droppedFiles = transferData.asInstanceOf[java.util.List[File]]
            if (droppedFiles != null && !droppedFiles.isEmpty()) {
              val file = droppedFiles.get(droppedFiles.size() - 1);
              action(file);
            }
          } catch {
            case ex: Exception => Log.error(ex); ex.printStackTrace
          }
        }
      }
      component
    }

    def onDropString(action: String => Unit) = {
      onDropEvent { evt =>
        this.synchronized {
          try {
            evt.acceptDrop(DnDConstants.ACTION_COPY)
            val transferable = evt.getTransferable()
            val transferData = transferable.getTransferData(DataFlavor.plainTextFlavor)
            val reader = transferData.asInstanceOf[StringReader]
            val array = new Array[Char](100)
            reader.read(array)
            action(new String(array))
          } catch {
            case ex: Exception => Log.error(ex); ex.printStackTrace
          }
        }
      }
      component
    }

    def window = SwingUtilities.windowForComponent(component)

    def windowName =
      if (window != null)
        if (window.isInstanceOf[JFrame]) window.asInstanceOf[JFrame].getTitle
        else window.getName
      else null

    def parentPath: String = if (component.getParent != null) component.getParent.componentPath + component.getParent.asInstanceOf[Container].getComponents.indexOf(component) else ""

    def componentPath: String = parentPath + "/" + component.getClass.getSimpleName + "." + component.getName

    // TODO implement file list and...
    //TODO implement other DND Flavors...
  }
}