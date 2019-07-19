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
import javax.swing.DropMode
import javax.swing.ListSelectionModel
import javax.swing.plaf.ListUI
import java.awt.Color
import javax.swing.ListCellRenderer
import java.awt.Point

object JListPlus {
  implicit class JListImplicit[TYPE <: JList[E], E](jList: TYPE) extends JComponentImplicit(jList) {
    //    ListSelectionListener
    def onSelectionChanged(action: => Unit) = onValueChangedEvent(e => action)
    def onSelectionChangedEvent(action: ListSelectionEvent => Unit) = onValueChangedEvent(action)
    def onValueChanged(action: => Unit) = onValueChangedEvent(e => action)
    def onValueChangedEvent(action: ListSelectionEvent => Unit) = { jList.getSelectionModel.addListSelectionListener(SwingPlus.onValueChanged(action)); jList }

    def addSelectionInterval(anchor: Int, lead: Int) = { jList.addSelectionInterval(anchor, lead); jList }
    def clearSelection = { jList.clearSelection(); jList }
    def ensureIndexIsVisible(index: Int) = { jList.ensureIndexIsVisible(index); jList }
    def locationToIndex(location: Point) = { jList.locationToIndex(location); jList }
    def cellRenderer(cellRenderer: ListCellRenderer[E]) = { jList.setCellRenderer(cellRenderer); jList }
    def dragEnabled(b: Boolean) = { jList.setDragEnabled(b); jList }
    def dragEnabled :TYPE = dragEnabled(true)
		def dragDisabled:TYPE = dragEnabled(false)
    def dropMode(dropMode: DropMode) = { jList.setDropMode(dropMode); jList }//TODO
    def fixedCellHeight(height: Int) = { jList.setFixedCellHeight(height); jList }
    def fixedCellWidth(width: Int) = { jList.setFixedCellWidth(width); jList }
    def layoutOrientation(layoutOrientation: Int) = { jList.setLayoutOrientation(layoutOrientation); jList }
    def listData(array: Array[E with Object]): TYPE = { jList.setListData(array); jList }
    def listData(listData: java.util.Vector[_ <: E]) = {jList.setListData(listData);jList}
    def model(model: ListModel[E]) = { jList.setModel(model); jList }
    def prototypeCellValue(prototypeCellValue: E) = { jList.setPrototypeCellValue(prototypeCellValue); jList }
    def selectedIndex(index: Int) = { jList.setSelectedIndex(index); jList }
    def selectedIndices(array: Array[Int]) = { jList.setSelectedIndices(array); jList }
    def selectedValue(anObject: Object, shouldScroll: Boolean) = { jList.setSelectedValue(anObject, shouldScroll); jList }
    def selectionBackground(selectionBackground: Color) = { jList.setSelectionBackground(selectionBackground); jList }
    def selectionForeground(selectionForeground: Color) = { jList.setSelectionForeground(selectionForeground); jList }
    def selectionInterval(anchor: Int, lead: Int) = { jList.setSelectionInterval(anchor, lead); jList }
    def selectionMode(selectionMode: Int) = { jList.setSelectionMode(selectionMode); jList }//TODO
    def selectionModel(selectionModel: ListSelectionModel) = { jList.setSelectionModel(selectionModel); jList }
    def uI(ui: ListUI) = { jList.setUI(ui); jList }
    def valueIsAdjusting(b: Boolean) = { jList.setValueIsAdjusting(b); jList }
    def visibleRowCount(visibleRowCount: Int) = { jList.setVisibleRowCount(visibleRowCount); jList }
    def updateUI = { jList.updateUI(); jList }
  }
}
class JListPlus[TYPE] extends JList[TYPE] {

  private var values = List[TYPE]()
  def getValues = values

  def withValues(values: List[TYPE]): JListPlus[TYPE] = {
    this.values = values.toList
    setModel(new AbstractListModel[TYPE]() {
      def getSize() = values.length
      def getElementAt(i: Int) = values(i)
    })
    this
  }
}