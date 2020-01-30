package eu.eyan.util.swing

import java.awt.Component
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File

import com.jgoodies.forms.factories.CC

import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.jgoodies.FormLayoutPlus
import eu.eyan.util.scala.TryCatchFinally
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.SwingWorker
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import javax.swing.event.CaretEvent
import javax.swing.event.CaretListener
import javax.swing.event.CellEditorListener
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.event.InternalFrameAdapter
import javax.swing.event.InternalFrameEvent
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.MenuDragMouseEvent
import javax.swing.event.MenuDragMouseListener
import javax.swing.event.MenuEvent
import javax.swing.event.MenuKeyEvent
import javax.swing.event.MenuKeyListener
import javax.swing.event.MenuListener
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener
import javax.swing.event.RowSorterEvent
import javax.swing.event.RowSorterListener
import javax.swing.event.TableColumnModelEvent
import javax.swing.event.TableColumnModelListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.event.TreeWillExpandListener
import javax.swing.event.UndoableEditEvent
import javax.swing.event.UndoableEditListener
import javax.swing.filechooser.FileNameExtensionFilter
import eu.eyan.log.Log
import eu.eyan.util.scala.TryCatch
import eu.eyan.util.scala.TryCatchThrowable
import java.awt.event.MouseMotionListener
import java.awt.event.MouseMotionAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelEvent

object SwingPlus {
  //  AncestorEvent
  //  AncestorListener
  class AncestorAdapter extends AncestorListener {
    override def ancestorAdded(e: AncestorEvent) = {}
    override def ancestorRemoved(e: AncestorEvent) = {}
    override def ancestorMoved(e: AncestorEvent) = {}
  }
  def onAncestorAdded(action: AncestorEvent => Unit) = new AncestorAdapter() { override def ancestorAdded(e: AncestorEvent) = action(e) }
  def onAncestorRemoved(action: AncestorEvent => Unit) = new AncestorAdapter() { override def ancestorRemoved(e: AncestorEvent) = action(e) }
  def onAncestorMoved(action: AncestorEvent => Unit) = new AncestorAdapter() { override def ancestorMoved(e: AncestorEvent) = action(e) }

  //  CaretEvent
  //  CaretListener
  def onCaretUpdate(action: CaretEvent => Unit) = new CaretListener() { override def caretUpdate(e: CaretEvent) = action(e) }

  //  ChangeEvent
  //  CellEditorListener
  class CellEditorAdapter extends CellEditorListener {
    override def editingStopped(e: ChangeEvent) = {}
    override def editingCanceled(e: ChangeEvent) = {}
  }
  def onEditingStopped(action: ChangeEvent => Unit) = new CellEditorAdapter() { override def editingStopped(e: ChangeEvent) = action(e) }
  def onEditingCanceled(action: ChangeEvent => Unit) = new CellEditorAdapter() { override def editingCanceled(e: ChangeEvent) = action(e) }

  //  ChangeListener
  def onStateChanged(action: ChangeEvent => Unit) = new ChangeListener() { override def stateChanged(e: ChangeEvent) = action(e) }

  //  ItemListener
  def onItemStateChanged(action: ItemEvent => Unit) = new ItemListener() { override def itemStateChanged(e: ItemEvent) = action(e) }

  //  DocumentEvent
  //  DocumentListener
  class DocumentAdapter extends DocumentListener {
    override def insertUpdate(e: DocumentEvent) = {}
    override def removeUpdate(e: DocumentEvent) = {}
    override def changedUpdate(e: DocumentEvent) = {}
  }
  def onInsertUpdate(action: DocumentEvent => Unit) = new DocumentAdapter() { override def insertUpdate(e: DocumentEvent) = action(e) }
  def onRemoveUpdate(action: DocumentEvent => Unit) = new DocumentAdapter() { override def removeUpdate(e: DocumentEvent) = action(e) }
  def onChangedUpdate(action: DocumentEvent => Unit) = new DocumentAdapter() { override def changedUpdate(e: DocumentEvent) = action(e) }

  //  HyperlinkEvent
  //  HyperlinkListener
  def onHyperlinkUpdate(action: HyperlinkEvent => Unit) = new HyperlinkListener() { override def hyperlinkUpdate(e: HyperlinkEvent) = action(e) }

  //  InternalFrameAdapter
  //  InternalFrameEvent
  //  InternalFrameListener
  def onInternalFrameOpened(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameOpened(e: InternalFrameEvent) = action(e) }
  def onInternalFrameClosing(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameClosing(e: InternalFrameEvent) = action(e) }
  def onInternalFrameClosed(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameClosed(e: InternalFrameEvent) = action(e) }
  def onInternalFrameIconified(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameIconified(e: InternalFrameEvent) = action(e) }
  def onInternalFrameDeiconified(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameDeiconified(e: InternalFrameEvent) = action(e) }
  def onInternalFrameActivated(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameActivated(e: InternalFrameEvent) = action(e) }
  def onInternalFrameDeactivated(action: InternalFrameEvent => Unit) = new InternalFrameAdapter() { override def internalFrameDeactivated(e: InternalFrameEvent) = action(e) }

  //  ListDataEvent
  //  ListDataListener
  class ListDataAdapter extends ListDataListener {
    override def intervalAdded(e: ListDataEvent) = {}
    override def intervalRemoved(e: ListDataEvent) = {}
    override def contentsChanged(e: ListDataEvent) = {}
  }
  def onIntervalAdded(action: ListDataEvent => Unit) = new ListDataAdapter() { override def intervalAdded(e: ListDataEvent) = action(e) }
  def onIntervalRemoved(action: ListDataEvent => Unit) = new ListDataAdapter() { override def intervalRemoved(e: ListDataEvent) = action(e) }
  def onContentsChanged(action: ListDataEvent => Unit) = new ListDataAdapter() { override def contentsChanged(e: ListDataEvent) = action(e) }
  
  def onListData(action: => Unit) = new ListDataListener {
	  override def intervalAdded(e: ListDataEvent) = action
	  override def intervalRemoved(e: ListDataEvent) = action
	  override def contentsChanged(e: ListDataEvent) = action
  }

  //  ListSelectionEvent
  //  ListSelectionListener
  def onValueChanged(action: ListSelectionEvent => Unit) = new ListSelectionListener() { override def valueChanged(e: ListSelectionEvent) = action(e) }

  //  MenuDragMouseEvent
  //  MenuDragMouseListener
  class MenuDragMouseAdapter extends MenuDragMouseListener {
    override def menuDragMouseEntered(e: MenuDragMouseEvent) = {}
    override def menuDragMouseExited(e: MenuDragMouseEvent) = {}
    override def menuDragMouseDragged(e: MenuDragMouseEvent) = {}
    override def menuDragMouseReleased(e: MenuDragMouseEvent) = {}
  }
  def onMenuDragMouseEntered(action: MenuDragMouseEvent => Unit) = new MenuDragMouseAdapter() { override def menuDragMouseEntered(e: MenuDragMouseEvent) = action(e) }
  def onMenuDragMouseExited(action: MenuDragMouseEvent => Unit) = new MenuDragMouseAdapter() { override def menuDragMouseExited(e: MenuDragMouseEvent) = action(e) }
  def onMenuDragMouseDragged(action: MenuDragMouseEvent => Unit) = new MenuDragMouseAdapter() { override def menuDragMouseDragged(e: MenuDragMouseEvent) = action(e) }
  def onMenuDragMouseReleased(action: MenuDragMouseEvent => Unit) = new MenuDragMouseAdapter() { override def menuDragMouseReleased(e: MenuDragMouseEvent) = action(e) }

  //  MenuKeyEvent
  //  MenuKeyListener
  class MenuKeyAdapter extends MenuKeyListener {
    override def menuKeyTyped(e: MenuKeyEvent) = {}
    override def menuKeyPressed(e: MenuKeyEvent) = {}
    override def menuKeyReleased(e: MenuKeyEvent) = {}
  }
  def onMenuKeyTyped(action: MenuKeyEvent => Unit) = new MenuKeyAdapter() { override def menuKeyTyped(e: MenuKeyEvent) = action(e) }
  def onMenuKeyPressed(action: MenuKeyEvent => Unit) = new MenuKeyAdapter() { override def menuKeyPressed(e: MenuKeyEvent) = action(e) }
  def onMenuKeyReleased(action: MenuKeyEvent => Unit) = new MenuKeyAdapter() { override def menuKeyReleased(e: MenuKeyEvent) = action(e) }

  //  MenuEvent
  //  MenuListener
  class MenuAdapter extends MenuListener {
    override def menuSelected(e: MenuEvent) = {}
    override def menuDeselected(e: MenuEvent) = {}
    override def menuCanceled(e: MenuEvent) = {}
  }
  def onMenuSelected(action: MenuEvent => Unit) = new MenuAdapter() { override def menuSelected(e: MenuEvent) = action(e) }
  def onMenuDeselected(action: MenuEvent => Unit) = new MenuAdapter() { override def menuDeselected(e: MenuEvent) = action(e) }
  def onMenuCanceled(action: MenuEvent => Unit) = new MenuAdapter() { override def menuCanceled(e: MenuEvent) = action(e) }

  //  MouseInputAdapter
  //  MouseInputListener
  // MouseAdapter
  // MouseListener
  // MouseMotionListener
  // MouseWheelListener
  def onMouseClicked(action: MouseEvent => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = action(e) }
  def onMousePressed(action: MouseEvent => Unit) = new MouseAdapter { override def mousePressed(e: MouseEvent) = action(e) }
  def onMouseReleased(action: MouseEvent => Unit) = new MouseAdapter { override def mouseReleased(e: MouseEvent) = action(e) }
  def onMouseEntered(action: MouseEvent => Unit) = new MouseAdapter { override def mouseEntered(e: MouseEvent) = action(e) }
  def onMouseExited(action: MouseEvent => Unit) = new MouseAdapter { override def mouseExited(e: MouseEvent) = action(e) }
  def onMouseWheelMoved(action: MouseWheelEvent => Unit) = new MouseAdapter { override def mouseWheelMoved(e: MouseWheelEvent) = action(e) }
  def onMouseDragged(action: MouseEvent => Unit) = new MouseAdapter { override def mouseDragged(e: MouseEvent) = action(e) }
  def onMouseMoved(action: MouseEvent => Unit) = new MouseAdapter { override def mouseMoved(e: MouseEvent) = action(e) }

  //  PopupMenuEvent
  //  PopupMenuListener
  class PopupMenuAdapter extends PopupMenuListener {
    override def popupMenuWillBecomeVisible(e: PopupMenuEvent) = {}
    override def popupMenuWillBecomeInvisible(e: PopupMenuEvent) = {}
    override def popupMenuCanceled(e: PopupMenuEvent) = {}
  }
  def onPopupMenuWillBecomeVisible(action: PopupMenuEvent => Unit) = new PopupMenuAdapter() { override def popupMenuWillBecomeVisible(e: PopupMenuEvent) = action(e) }
  def onPopupMenuWillBecomeInvisible(action: PopupMenuEvent => Unit) = new PopupMenuAdapter() { override def popupMenuWillBecomeInvisible(e: PopupMenuEvent) = action(e) }
  def onPopupMenuCanceled(action: PopupMenuEvent => Unit) = new PopupMenuAdapter() { override def popupMenuCanceled(e: PopupMenuEvent) = action(e) }

  //  RowSorterEvent
  //  RowSorterListener
  def onSorterChanged(action: RowSorterEvent => Unit) = new RowSorterListener() { override def sorterChanged(e: RowSorterEvent) = action(e) }

  //  TableColumnModelEvent
  //  TableColumnModelListener
  class TableColumnModelAdapter extends TableColumnModelListener {
    override def columnAdded(e: TableColumnModelEvent) = {}
    override def columnRemoved(e: TableColumnModelEvent) = {}
    override def columnMoved(e: TableColumnModelEvent) = {}
    override def columnMarginChanged(e: ChangeEvent) = {}
    override def columnSelectionChanged(e: ListSelectionEvent) = {}
  }
  def onColumnAdded(action: TableColumnModelEvent => Unit) = new TableColumnModelAdapter() { override def columnAdded(e: TableColumnModelEvent) = action(e) }
  def onColumnRemoved(action: TableColumnModelEvent => Unit) = new TableColumnModelAdapter() { override def columnRemoved(e: TableColumnModelEvent) = action(e) }
  def onColumnMoved(action: TableColumnModelEvent => Unit) = new TableColumnModelAdapter() { override def columnMoved(e: TableColumnModelEvent) = action(e) }
  def onColumnMarginChanged(action: ChangeEvent => Unit) = new TableColumnModelAdapter() { override def columnMarginChanged(e: ChangeEvent) = action(e) }
  def onColumnSelectionChanged(action: ListSelectionEvent => Unit) = new TableColumnModelAdapter() { override def columnSelectionChanged(e: ListSelectionEvent) = action(e) }

  //  TableModelEvent
  //  TableModelListener
  def onTableChanged(action: TableModelEvent => Unit) = new TableModelListener() { override def tableChanged(e: TableModelEvent) = action(e) }

  //  TreeExpansionEvent
  //  TreeExpansionListener
  class TreeExpansionAdapter extends TreeExpansionListener {
    override def treeExpanded(e: TreeExpansionEvent) = {}
    override def treeCollapsed(e: TreeExpansionEvent) = {}
  }
  def onTreeExpanded(action: TreeExpansionEvent => Unit) = new TreeExpansionAdapter() { override def treeExpanded(e: TreeExpansionEvent) = action(e) }
  def onTreeCollapsed(action: TreeExpansionEvent => Unit) = new TreeExpansionAdapter() { override def treeCollapsed(e: TreeExpansionEvent) = action(e) }

  //  TreeModelEvent
  //  TreeModelListener
  class TreeModelAdapter extends TreeModelListener {
    override def treeNodesChanged(e: TreeModelEvent) = {}
    override def treeNodesInserted(e: TreeModelEvent) = {}
    override def treeNodesRemoved(e: TreeModelEvent) = {}
    override def treeStructureChanged(e: TreeModelEvent) = {}
  }
  def onTreeNodesChanged(action: TreeModelEvent => Unit) = new TreeModelAdapter() { override def treeNodesChanged(e: TreeModelEvent) = action(e) }
  def onTreeNodesInserted(action: TreeModelEvent => Unit) = new TreeModelAdapter() { override def treeNodesInserted(e: TreeModelEvent) = action(e) }
  def onTreeNodesRemoved(action: TreeModelEvent => Unit) = new TreeModelAdapter() { override def treeNodesRemoved(e: TreeModelEvent) = action(e) }
  def onTreeStructureChanged(action: TreeModelEvent => Unit) = new TreeModelAdapter() { override def treeStructureChanged(e: TreeModelEvent) = action(e) }

  //  TreeSelectionEvent
  //  TreeSelectionListener
  def onValueChanged(action: TreeSelectionEvent => Unit) = new TreeSelectionListener() { override def valueChanged(e: TreeSelectionEvent) = action(e) }

  //  TreeWillExpandListener
  class TreeWillExpandAdapter extends TreeWillExpandListener {
    override def treeWillExpand(e: TreeExpansionEvent) = {}
    override def treeWillCollapse(e: TreeExpansionEvent) = {}
  }
  def onTreeWillExpand(action: TreeExpansionEvent => Unit) = new TreeWillExpandAdapter() { override def treeWillExpand(e: TreeExpansionEvent) = action(e) }
  def onTreeWillCollapse(action: TreeExpansionEvent => Unit) = new TreeWillExpandAdapter() { override def treeWillCollapse(e: TreeExpansionEvent) = action(e) }

  //  UndoableEditEvent
  //  UndoableEditListener
  def onUndoableEditHappened(action: UndoableEditEvent => Unit) = new UndoableEditListener() { override def undoableEditHappened(e: UndoableEditEvent) = action(e) }

  //////////////////////////////////////////////////////////////////

  def showErrorDialog(msg: String, e: Throwable, shown: Set[Throwable] = Set()): Unit = {
    if (e.getCause != null && !shown.contains(e.getCause))
      showErrorDialog(msg + ", " + e.getLocalizedMessage, e.getCause, shown + e)
    else JOptionPane.showMessageDialog(null, msg + ", " + e.getLocalizedMessage)
  }

  def invokeAndWait(action: => Unit) = SwingUtilities.invokeAndWait(AwtHelper.newRunnable(() => action))

  def invokeLater(action: => Unit) = SwingUtilities.invokeLater(AwtHelper.newRunnable(() => action))

  //TODO merge with other methods
  def invokeLaterTryCatchFinally[T](action: => T, error: Throwable => T, finaly: => Unit) = invokeLater(TryCatchFinally(action, error, finaly))

  //TODO merge with other methods
  def swingWorkerTryCatchFinally[T](action: => T, error: => Throwable => T, finaly: => Unit) = {
    new SwingWorker[T, T]() {
      override def doInBackground = try action catch { case t: Throwable => Log.error("Error in SwingWorker", t); error(t)}
      override def done = finaly
    }.execute
  }

  //TODO merge with other methods
  def runInWorker(work: => Unit, doAtDone: => Unit = ()=>{} ) = {
    new SwingWorker[Void, Void]() {
      override def doInBackground() = {
        //try { work } catch { case t: Throwable => Log.error("Error in SwingWorker", t) }
        TryCatchThrowable(work, t => Log.error("Error in SwingWorker", t)); null
      }
      override def done() = doAtDone
    }.execute()
  }

  def beforeActionErrorAfter[T](before: => Unit, action: => T, error: Throwable => T, finaly: => Unit) = { before; SwingPlus.swingWorkerTryCatchFinally(action, error, finaly) }

  def createListContentsChangedListener(listDataContentsChangedEventConsumer: ListDataEvent => Unit) =
    new ListDataListener() {
      override def intervalRemoved(e: ListDataEvent) = {}
      override def intervalAdded(e: ListDataEvent) = {}
      override def contentsChanged(e: ListDataEvent) = listDataContentsChangedEventConsumer(e)
    }

  def newLeftFlowPanel() = new FlowPanel(new FlowLayout(FlowLayout.LEFT))

  // CHECKBOX
  def newCheckBoxWithAction(text: String, action: () => Unit): JCheckBox = newCheckBoxWithAction(text, (cb, e) => action())

  def checkBox(text: String, action: JCheckBox => Unit) = newCheckBoxWithAction(text, (cb, e) => action(cb))

  def newCheckBoxWithAction(text: String, action: (JCheckBox, ActionEvent) => Unit) = {
    val cb = new JCheckBox(text)
    cb.addActionListener(AwtHelper.onActionPerformed(e => action(cb, e)))
    cb
  }

  // BUTTON
  def button(text: String, action: () => Unit) = newButtonWithAction(text, (b, e) => action())

  def newButtonWithAction(text: String, action: (JButton, ActionEvent) => Unit) = {
    val button = new JButton(text)
    button.addActionListener(AwtHelper.onActionPerformed(e => action(button, e)))
    button
  }

  // TEXTFIELD
  def textField(columns: Int, actionListener: JTextField => Unit) = newTextFieldWithAction(columns, (tf, e) => actionListener(tf))

  def newTextFieldWithAction(columns: Int, actionListener: (JTextField, ActionEvent) => Unit) = {
    val tf = new JTextField(columns)
    tf.addActionListener(AwtHelper.onActionPerformed(e => actionListener(tf, e)))
    tf
  }

  // LABEL
  def label(text: String) = new JLabel(text)

  // PROGRESSBAR
  def jProgressBarPercent(format: String) = {
    val progressBar = new JProgressBarPlus(0, 100, format)
    progressBar.setValue(0)
    progressBar.setStringPainted(true)
    progressBar.setVisible(false)
    invokeLater(progressBar.setString("..."))
    progressBar
  }

  def jPanelOneRow(rowSpec: String, col1Spec: String, col1Comp: Component, col2Spec: String, col2Comp: Component) = {
    val layout = FormLayoutPlus(new JPanel(), col1Spec + "," + col2Spec)
    layout.appendRow(rowSpec)
    layout.getComponent.add(col1Comp, CC.xy(1, 1))
    layout.getComponent.add(col2Comp, CC.xy(1 + 1, 1))
    layout.getComponent
  }

  def chooseFile(action: File => Unit, extension: String = "") = {
    val fc = new JFileChooser
    if (!extension.isEmpty) fc.setFileFilter(new FileNameExtensionFilter(extension + " files", extension))
    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) action(fc.getSelectedFile)
  }

}