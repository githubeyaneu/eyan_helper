package eu.eyan.util.awt.dnd

import java.awt.dnd.DropTarget
import java.util.function.Consumer
import java.io.File
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DnDConstants
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.DataFlavor

// FIXME: move to ComponentPlus setDropTarget
object DnD_FileDropTarget {
  
}

// TODO use listeners instead overriding
class DnD_FileDropTarget(action: File=>Unit) extends DropTarget {
  
}