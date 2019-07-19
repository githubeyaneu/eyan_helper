package eu.eyan.util.awt.dnd

import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DnDConstants

object DropTargetDropEventPlus {
  implicit class DropTargetDropEventImplicit(dropEvent: DropTargetDropEvent) {
    def acceptDragBasedOnDataFlavor(dataFlavor: DataFlavor) =
      if (dropEvent.isDataFlavorSupported(dataFlavor)) {
        dropEvent.acceptDrop(DnDConstants.ACTION_COPY)
        true
      } else {
        dropEvent.rejectDrop
        false
      }
  }
}