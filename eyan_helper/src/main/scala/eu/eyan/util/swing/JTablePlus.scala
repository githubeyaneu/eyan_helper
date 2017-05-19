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