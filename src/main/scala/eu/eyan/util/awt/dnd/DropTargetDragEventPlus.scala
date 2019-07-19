package eu.eyan.util.awt.dnd

import java.awt.dnd.DropTargetDragEvent
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants

object DropTargetDragEventPlus {
  implicit class DropTargetDragEventImplicit(dragEvent: DropTargetDragEvent) {
    def acceptDragBasedOnDataFlavor(dataFlavor: DataFlavor): Boolean =
      if (dragEvent.isDataFlavorSupported(dataFlavor)) { dragEvent.acceptDrag(DnDConstants.ACTION_COPY); true } else { dragEvent.rejectDrag; false }
  }
}