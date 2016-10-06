package eu.eyan.util.swing

import javax.swing.JList
import javax.swing.ListModel
import javax.swing.AbstractListModel
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter

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

  def addDoubleClickListener(action: MouseEvent => Unit) =
    addMouseListener(new MouseAdapter { override def mouseClicked(e: MouseEvent) = if (e.getClickCount() == 2) action(e) })

}