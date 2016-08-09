package eu.eyan.util.awt.dnd

import java.awt.dnd.DropTarget
import java.util.function.Consumer
import java.io.File
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DnDConstants
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.DataFlavor

object DnD_FileDropTarget {
  def onDropFile(action: File=>Unit): DropTarget = new DnD_FileDropTarget(action)
}

class DnD_FileDropTarget(action: File=>Unit) extends DropTarget {
  override def drop(evt: DropTargetDropEvent): Unit = {
    this.synchronized {
      try {
        evt.acceptDrop(DnDConstants.ACTION_COPY)
        val transferable = evt.getTransferable()
        val transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor)
        val droppedFiles = transferData.asInstanceOf[java.util.List[File]]
        if (droppedFiles != null && !droppedFiles.isEmpty()) {
          val file = droppedFiles.get(droppedFiles.size() - 1);
          action(file);
        }
      } catch {
        case ex: Exception => //ex.printStackTrace();
      }
    }
  }
}