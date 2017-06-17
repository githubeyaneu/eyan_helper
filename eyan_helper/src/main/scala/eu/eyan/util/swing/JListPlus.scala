package eu.eyan.util.swing

import javax.swing.JList
import javax.swing.ListModel
import javax.swing.AbstractListModel
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import javax.swing.border.Border
import javax.swing.event.ListSelectionListener
import javax.swing.event.ListSelectionEvent
import org.fest.swing.input.MouseInfo
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object JListPlus{
  implicit class JListImplicit[ITEM, TYPE <: JList[ITEM]](jList: TYPE) extends JComponentImplicit(jList){
    //addListSelectionListener(ListSelectionListener)
    //addSelectionInterval(int, int)
    //clearSelection()
    //ensureIndexIsVisible(int)
    //indexToLocation(int)
    //isSelectedIndex(int)
    //isSelectionEmpty()
    //locationToIndex(Point)
    //removeListSelectionListener(ListSelectionListener)
    //removeSelectionInterval(int, int)
    //setCellRenderer(ListCellRenderer<? super E>)
    //setDragEnabled(boolean)
    //setDropMode(DropMode)
    //setFixedCellHeight(int)
    //setFixedCellWidth(int)
    //setLayoutOrientation(int)
    //setListData(E[])
    //setListData(Vector<? extends E>)
    //setModel(ListModel<E>)
    //setPrototypeCellValue(E)
    //setSelectedIndex(int)
    //setSelectedIndices(int[])
    //setSelectedValue(Object, boolean)
    //setSelectionBackground(Color)
    //setSelectionForeground(Color)
    //setSelectionInterval(int, int)
    //setSelectionMode(int)
    //setSelectionModel(ListSelectionModel)
    //setUI(ListUI)
    //setValueIsAdjusting(boolean)
    //setVisibleRowCount(int)
    //updateUI()    
  }
}
class JListPlus[TYPE] extends JList[TYPE] {

  private var values = List[TYPE]()
  def getValues = values

  def withName(name: String): JListPlus[TYPE] = { setName(name); this }

  def withValues(values: List[TYPE]): JListPlus[TYPE] = {
    this.values = values.toList
    setModel(new AbstractListModel[TYPE]() {
      def getSize() = values.length
      def getElementAt(i: Int) = values(i)
    })
    this
  }

  def onDoubleClick(action: () => Unit) = {
    addMouseListener(new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount() == 2 && e.getButton==MouseEvent.BUTTON1) action() })
    this
  }

  def withBorder(border: Border) = { setBorder(border); this }

  def onSelectionChanged(action: () => Unit) = {
    getSelectionModel.addListSelectionListener(new ListSelectionListener { override def valueChanged(e: ListSelectionEvent) = { action() } })
  }
}