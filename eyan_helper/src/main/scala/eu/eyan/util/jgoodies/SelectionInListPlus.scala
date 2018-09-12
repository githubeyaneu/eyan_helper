package eu.eyan.util.jgoodies

import eu.eyan.util.swing.SwingPlus
import com.jgoodies.binding.list.SelectionInList

object SelectionInListPlus {
  implicit class SelectionInListImplicit[T <: SelectionInList[_]](selection: T) {
    def onListData(action: => Unit) = selection.addListDataListener(SwingPlus.onListData(action))
  }
}