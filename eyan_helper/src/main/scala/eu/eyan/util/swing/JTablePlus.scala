package eu.eyan.util.swing

import javax.swing.JList
import javax.swing.ListModel
import javax.swing.AbstractListModel
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import javax.swing.border.Border
import javax.swing.event.ListSelectionListener
import javax.swing.event.ListSelectionEvent
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.table.JTableHeader
import javax.swing.table.TableColumnModel
import javax.swing.table.DefaultTableColumnModel
import org.jdesktop.swingx.JXTable
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object JTablePlus {
  implicit class JTableImplicit[TYPE <: JTable](jTable: TYPE) extends JComponentImplicit(jTable) {
//    addColumn(TableColumn)
//    addColumnSelectionInterval(int, int)
//    addNotify()
//    addRowSelectionInterval(int, int)
//    changeSelection(int, int, boolean, boolean)
//    clearSelection()
//    columnAdded(TableColumnModelEvent)
//    columnAtPoint(Point)
//    columnMarginChanged(ChangeEvent)
//    columnMoved(TableColumnModelEvent)
//    columnRemoved(TableColumnModelEvent)
//    columnSelectionChanged(ListSelectionEvent)
//    createDefaultColumnsFromModel()
//    editCellAt(int, int)
//    editCellAt(int, int, EventObject)
//    editingCanceled(ChangeEvent)
//    editingStopped(ChangeEvent)
//    moveColumn(int, int)
//    prepareEditor(TableCellEditor, int, int)
//    prepareRenderer(TableCellRenderer, int, int)
//    print()
//    print(PrintMode)
//    print(PrintMode, MessageFormat, MessageFormat)
//    print(PrintMode, MessageFormat, MessageFormat, boolean, PrintRequestAttributeSet, boolean)
//    print(PrintMode, MessageFormat, MessageFormat, boolean, PrintRequestAttributeSet, boolean, PrintService)
//    removeColumn(TableColumn)
//    removeColumnSelectionInterval(int, int)
//    removeEditor()
//    removeNotify()
//    removeRowSelectionInterval(int, int)
//    rowAtPoint(Point)
//    selectAll()
//    setAutoCreateColumnsFromModel(boolean)
//    setAutoCreateRowSorter(boolean)
//    setAutoResizeMode(int)
//    setCellEditor(TableCellEditor)
//    setCellSelectionEnabled(boolean)
//    setColumnModel(TableColumnModel)
//    setColumnSelectionAllowed(boolean)
//    setColumnSelectionInterval(int, int)
//    setDefaultEditor(Class<?>, TableCellEditor)
//    setDefaultRenderer(Class<?>, TableCellRenderer)
//    setDragEnabled(boolean)
//    setDropMode(DropMode)
//    setEditingColumn(int)
//    setEditingRow(int)
//    setFillsViewportHeight(boolean)
//    setGridColor(Color)
//    setIntercellSpacing(Dimension)
//    setModel(TableModel)
//    setPreferredScrollableViewportSize(Dimension)
//    setRowHeight(int)
//    setRowHeight(int, int)
//    setRowMargin(int)
//    setRowSelectionAllowed(boolean)
//    setRowSelectionInterval(int, int)
//    setRowSorter(RowSorter<? extends TableModel>)
//    setSelectionBackground(Color)
//    setSelectionForeground(Color)
//    setSelectionMode(int)
//    setSelectionModel(ListSelectionModel)
//    setShowGrid(boolean)
//    setShowHorizontalLines(boolean)
//    setShowVerticalLines(boolean)
//    setSurrendersFocusOnKeystroke(boolean)
//    setTableHeader(JTableHeader)
//    setUI(TableUI)
//    setUpdateSelectionOnSort(boolean)
//    setValueAt(Object, int, int)
//    sizeColumnsToFit(boolean)
//    sizeColumnsToFit(int)
//    sorterChanged(RowSorterEvent)
//    tableChanged(TableModelEvent)
//    updateUI()
//    valueChanged(ListSelectionEvent)    
  }
}

class JTablePlus[TYPE] extends JXTable {

  private var values = List[TYPE]()
  private var columns = List[String]()
  private var valueGetter: (TYPE, String) => String = (value, column) => (value, column).toString

  def getValues = values

  def withName(name: String): JTable = { setName(name); this }

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

  def onDoubleClick(action: () => Unit) = {
    addMouseListener(new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount() == 2) action() })
    this
  }

  def withBorder(border: Border) = { setBorder(border); this }

  def onSelectionChanged(action: () => Unit) = {
    getSelectionModel.addListSelectionListener(new ListSelectionListener { override def valueChanged(e: ListSelectionEvent) = { action() } })
    this
  }
}