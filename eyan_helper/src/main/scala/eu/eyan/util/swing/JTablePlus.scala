package eu.eyan.util.swing

import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.EventObject

import org.jdesktop.swingx.JXTable

import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.DropMode
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.RowSorter
import javax.swing.border.Border
import javax.swing.event.ChangeEvent
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.RowSorterEvent
import javax.swing.event.TableColumnModelEvent
import javax.swing.event.TableModelEvent
import javax.swing.plaf.TableUI
import javax.swing.table.AbstractTableModel
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel
import eu.eyan.util.registry.RegistryValue
import eu.eyan.log.Log
import java.awt.Graphics2D
import java.awt.Graphics
import rx.lang.scala.Observable
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableImplicit
import eu.eyan.util.awt.Graphics2DPlus.Graphics2DImplicit
import javax.swing.ListModel
import com.jgoodies.binding.adapter.AbstractTableAdapter

object JTablePlus {
  implicit class JTableImplicit[TYPE <: JTable](jTable: TYPE) extends JComponentImplicit(jTable) {

    //	  TableModelListener
    def onTableChanged(action: => Unit) = onTableChangedEvent(e => action)
    def onTableChangedEvent(action: TableModelEvent => Unit) = { jTable.getModel.addTableModelListener(SwingPlus.onTableChanged(action)); jTable }
    //    TableColumnModelListener
    def onColumnAdded(action: => Unit) = onColumnAddedEvent(e => action)
    def onColumnAddedEvent(action: TableColumnModelEvent => Unit) = { jTable.getColumnModel.addColumnModelListener(SwingPlus.onColumnAdded(action)); jTable }
    def onColumnRemoved(action: => Unit) = onColumnRemovedEvent(e => action)
    def onColumnRemovedEvent(action: TableColumnModelEvent => Unit) = { jTable.getColumnModel.addColumnModelListener(SwingPlus.onColumnRemoved(action)); jTable }
    def onColumnMoved(action: => Unit) = onColumnMovedEvent(e => action)
    def onColumnMovedEvent(action: TableColumnModelEvent => Unit) = { jTable.getColumnModel.addColumnModelListener(SwingPlus.onColumnMoved(action)); jTable }
    def onColumnMarginChanged(action: => Unit) = onColumnMarginChangedEvent(e => action)
    def onColumnMarginChangedEvent(action: ChangeEvent => Unit) = { jTable.getColumnModel.addColumnModelListener(SwingPlus.onColumnMarginChanged(action)); jTable }
    def onColumnSelectionChanged(action: => Unit) = onColumnSelectionChangedEvent(e => action)
    def onColumnSelectionChangedEvent(action: ListSelectionEvent => Unit) = { jTable.getColumnModel.addColumnModelListener(SwingPlus.onColumnSelectionChanged(action)); jTable }
    //    ListSelectionListener
    def onSelectionChanged(action: => Unit) = onValueChangedEvent(e => action)
    def onSelectionChangedEvent(action: ListSelectionEvent => Unit) = onValueChangedEvent(action)
    def onValueChanged(action: => Unit) = onValueChangedEvent(e => action)
    def onValueChangedEvent(action: ListSelectionEvent => Unit) = { jTable.getSelectionModel.addListSelectionListener(SwingPlus.onValueChanged(action)); jTable }
    //    CellEditorListener
    def onEditingStopped(row: Int, column: Int, action: => Unit) = onEditingStoppedEvent(row, column, e => action)
    def onEditingStoppedEvent(row: Int, column: Int, action: ChangeEvent => Unit) = { jTable.getCellEditor(row, column).addCellEditorListener(SwingPlus.onEditingStopped(action)); jTable }
    def onEditingCanceled(row: Int, column: Int, action: => Unit) = onEditingCanceledEvent(row, column, e => action)
    def onEditingCanceledEvent(row: Int, column: Int, action: ChangeEvent => Unit) = { jTable.getCellEditor(row, column).addCellEditorListener(SwingPlus.onEditingCanceled(action)); jTable }
    def onEditingStopped(action: => Unit) = onEditingStoppedEvent(e => action)
    def onEditingStoppedEvent(action: ChangeEvent => Unit) = { jTable.getCellEditor.addCellEditorListener(SwingPlus.onEditingStopped(action)); jTable }
    def onEditingCanceled(action: => Unit) = onEditingCanceledEvent(e => action)
    def onEditingCanceledEvent(action: ChangeEvent => Unit) = { jTable.getCellEditor.addCellEditorListener(SwingPlus.onEditingCanceled(action)); jTable }
    //    RowSorterListener
    def onSorterChanged(action: => Unit) = onSorterChangedEvent(e => action)
    def onSorterChangedEvent(action: RowSorterEvent => Unit) = { jTable.getRowSorter.addRowSorterListener(SwingPlus.onSorterChanged(action)); jTable }

    def column(col: TableColumn) = { jTable.addColumn(col); jTable }
    def addNotify = { jTable.addNotify; jTable }
    def changeSelection(rowIndex: Int, columnIndex: Int, toggle: Boolean, extend: Boolean) = { jTable.changeSelection(rowIndex, columnIndex, toggle, extend); jTable }
    def clearSelection = { jTable.clearSelection; jTable }
    def columnAtPoint(point: Point) = { jTable.columnAtPoint(point); jTable }
    def createDefaultColumnsFromModel = { jTable.createDefaultColumnsFromModel(); jTable }
    def editCellAt(row: Int, column: Int) = { jTable.editCellAt(row, column); jTable }
    def editCellAt(row: Int, column: Int, e: EventObject) = { jTable.editCellAt(row, column, e); jTable }
    def moveColumn(column: Int, targetColumn: Int) = { jTable.moveColumn(column, targetColumn); jTable }
    def prepareEditor(editor: TableCellEditor, row: Int, column: Int) = { jTable.prepareEditor(editor, row, column); jTable }
    def prepareRenderer(renderer: TableCellRenderer, row: Int, column: Int) = { jTable.prepareRenderer(renderer, row, column); jTable }
    def rowAtPoInt(point: Point) = { jTable.rowAtPoint(point); jTable }
    def selectAll = { jTable.selectAll; jTable }
    def autoCreateColumnsFromModel(autoCreateColumnsFromModel: Boolean) = { jTable.setAutoCreateColumnsFromModel(autoCreateColumnsFromModel); jTable }
    def autoCreateRowSorter(autoCreateRowSorter: Boolean) = { jTable.setAutoCreateRowSorter(autoCreateRowSorter); jTable }
    def autoResizeMode(mode: Int) = { jTable.setAutoResizeMode(mode); jTable }
    def cellEditor(anEditor: TableCellEditor) = { jTable.setCellEditor(anEditor); jTable }
    def cellSelectionEnabled(cellSelectionEnabled: Boolean) = { jTable.setCellSelectionEnabled(cellSelectionEnabled); jTable }
    def columnModel(columnModel: TableColumnModel) = { jTable.setColumnModel(columnModel); jTable }
    def columnSelectionAllowed(columnSelectionAllowed: Boolean) = { jTable.setColumnSelectionAllowed(columnSelectionAllowed); jTable }
    def columnSelectionInterval(index0: Int, index1: Int) = { jTable.setColumnSelectionInterval(index0, index1); jTable }
    def defaultEditor(columnClass: Class[_], editor: TableCellEditor) = { jTable.setDefaultEditor(columnClass, editor); jTable }
    def defaultRenderer(columnClass: Class[_], renderer: TableCellRenderer) = { jTable.setDefaultRenderer(columnClass, renderer); jTable }
    def dragEnabled(b: Boolean) = { jTable.setDragEnabled(b); jTable }
    def dragEnabled: TYPE = dragEnabled(true)
    def dragDisabled: TYPE = dragEnabled(false)
    def dropMode(dropMode: DropMode) = { jTable.setDropMode(dropMode); jTable } //TODO
    def editingColumn(aColumn: Int) = { jTable.setEditingColumn(aColumn); jTable }
    def editingRow(aRow: Int) = { jTable.setEditingRow(aRow); jTable }
    def fillsViewportHeight(fillsViewportHeight: Boolean) = { jTable.setFillsViewportHeight(fillsViewportHeight); jTable }
    def gridColor(gridColor: Color) = { jTable.setGridColor(gridColor); jTable }
    def intercellSpacing(intercellSpacing: Dimension) = { jTable.setIntercellSpacing(intercellSpacing); jTable }
    def model(dataModel: TableModel) = { jTable.setModel(dataModel); jTable }
    def preferredScrollableViewportSize(size: Dimension) = { jTable.setPreferredScrollableViewportSize(size); jTable }
    def rowHeight(rowHeight: Int) = { jTable.setRowHeight(rowHeight); jTable }
    def rowHeight(row: Int, rowHeight: Int) = { jTable.setRowHeight(row, rowHeight); jTable }
    def rowMargin(rowMargin: Int) = { jTable.setRowMargin(rowMargin); jTable }
    def rowSelectionAllowed(rowSelectionAllowed: Boolean) = { jTable.setRowSelectionAllowed(rowSelectionAllowed); jTable }
    def rowSelectionInterval(index0: Int, index1: Int) = { jTable.setRowSelectionInterval(index0, index1); jTable }
    def rowSorter(sorter: RowSorter[_ <: TableModel]) = { jTable.setRowSorter(sorter); jTable }
    def selectionBackground(selectionBackground: Color) = { jTable.setSelectionBackground(selectionBackground); jTable }
    def selectionForeground(selectionForeground: Color) = { jTable.setSelectionForeground(selectionForeground); jTable }
    def selectionMode(selectionMode: Int) = { jTable.setSelectionMode(selectionMode); jTable } // TODO
    def selectionModel(newModel: ListSelectionModel) = { jTable.setSelectionModel(newModel); jTable }
    def showGrid(showGrid: Boolean) = { jTable.setShowGrid(showGrid); jTable }
    def showGrid: TYPE = showGrid(true)
    def showGridDisabled: TYPE = showGrid(false)
    def showHorizontalLines(showHorizontalLines: Boolean) = { jTable.setShowHorizontalLines(showHorizontalLines); jTable }
    def showHorizontalLines: TYPE = showHorizontalLines(true)
    def showHorizontalLinesDisabled: TYPE = showHorizontalLines(false)
    def showVerticalLines(showVerticalLines: Boolean) = { jTable.setShowVerticalLines(showVerticalLines); jTable }
    def showVerticalLines: TYPE = showVerticalLines(true)
    def showVerticalLinesDisabled: TYPE = showVerticalLines(false)
    def surrendersFocusOnKeystroke(surrendersFocusOnKeystroke: Boolean) = { jTable.setSurrendersFocusOnKeystroke(surrendersFocusOnKeystroke); jTable }
    def tableHeader(tableHeader: JTableHeader) = { jTable.setTableHeader(tableHeader); jTable }
    def ui(ui: TableUI) = { jTable.setUI(ui); jTable }
    def updateSelectionOnSort(update: Boolean) = { jTable.setUpdateSelectionOnSort(update); jTable }
    def updateSelectionOnSort: TYPE = updateSelectionOnSort(true)
    def updateSelectionOnSortDisabled: TYPE = updateSelectionOnSort(false)
    def valueAt(aValue: Object, row: Int, column: Int) = { jTable.setValueAt(aValue, row, column); jTable }
    def sizeColumnsToFit(lastColumnOnly: Int) = { jTable.sizeColumnsToFit(lastColumnOnly); jTable }
    def updateUI = { jTable.updateUI; jTable }

    def getWidth = jTable.getWidth
    def getColumnCount = jTable.getColumnCount
    def getColumn(idx: Int) = jTable.getColumnModel.getColumn(idx)
    def getColumns = for (i <- 0 until getColumnCount) yield getColumn(i)

    def rememberColumnWidhts(columnWidhtsInRegistry: RegistryValue) = {
      var lastWidths = ""
      jTable.getTableHeader.onMouseReleased(columnWidhtsInRegistry.save(lastWidths))
      jTable.getTableHeader.onDoubleClick(columnWidhtsInRegistry.save(lastWidths))
      jTable onColumnMarginChanged columnWidthChanged
      jTable onComponentResized resetColumnWidths
      jTable onTableChanged resetColumnWidths
      def columnWidthChanged = {
        Log.info("ColCount: " + jTable.getColumnCount)
        if (getWidth > 0 && getColumnCount > 0) lastWidths = getColumns.map(_.getWidth).map(_ * 100 / getWidth).mkString(",")
      }
      def resetColumnWidths = if (getWidth > 0 && getColumnCount > 0) columnWidhtsInRegistry.read.map(_.split(",").map(_.toInt * getWidth / 100)).foreach(setColumnWidths)
      def setColumnWidths(columnWidths: Array[Int]) = if (columnWidths.size == getColumnCount) for (i <- 0 until getColumnCount) getColumn(i).setPreferredWidth(columnWidths(i))
      jTable
    }
  }
}

case class TableRow(index: Int)
case class TableCol(index: Int)

class JTableModelPlus[T](listModel: ListModel[T], columns: List[String], cellValueGetter: (TableRow, TableCol) => String) extends AbstractTableAdapter[T](listModel, columns: _*) {
  def getValueAt(rowIndex: Int, columnIndex: Int) = cellValueGetter(TableRow(rowIndex), TableCol(columnIndex))
}

class JXTableWithEmptyText(emptyTextObs: Observable[String]) extends JXTable {
  override protected def paintComponent(g: Graphics) = paintOrTextIfEmpty(g.asInstanceOf[Graphics2D])
  private def paintOrTextIfEmpty(g2d: Graphics2D) = { super.paintComponent(g2d); if (getRowCount() == 0) g2d.drawString(emptyTextObs.get[String], Color.BLACK, 10, 20) }
  emptyTextObs.foreach(x=> repaint())
}

class JTablePlus[TYPE] extends JXTable {

  private var values = List[TYPE]()
  private var columns = List[String]()
  private var valueGetter: (TYPE, String) => String = (value, column) => (value, column).toString

  def getValues = values

  def withValueGetter(valueGetter: (TYPE, String) => String) = { this.valueGetter = valueGetter; this }

  def withColumns(columns: String*) = {
    this.columns = columns.toList
    this
  }

  def withValues(values: List[TYPE]) = {
    this.values = values.toList
    setModel(new AbstractTableModel {
      def getColumnCount(): Int = columns.size
      def getRowCount(): Int = values.size
      def getValueAt(rowIndex: Int, columnIndex: Int): Object = valueGetter(values(rowIndex), columns(columnIndex))
      override def getColumnName(index: Int) = columns(index)
    })
    this
  }
}