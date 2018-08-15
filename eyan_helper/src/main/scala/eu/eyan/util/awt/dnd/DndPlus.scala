package eu.eyan.util.awt.dnd

import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.Component
import java.awt.dnd.DropTargetListener
import java.awt.dnd.DropTarget
import java.io.StringReader
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import eu.eyan.log.Log
import eu.eyan.util.scala.Try
import scala.util.Failure
import eu.eyan.util.awt.dnd.DropTargetDragEventPlus.DropTargetDragEventImplicit
import eu.eyan.util.awt.dnd.DropTargetDropEventPlus.DropTargetDropEventImplicit
import scala.io.Source
import java.io.InputStream
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import java.nio.charset.Charset
import scala.io.Codec
import scala.util.Success
import java.io.File
import scala.collection.JavaConversions._

object DndPlus {
  //DropTargetListener
  class DropTargetAdapter extends java.awt.dnd.DropTargetAdapter { def drop(e: DropTargetDropEvent) = {} }

  private def onDragEnter(component: Component, action: DropTargetDragEvent => Unit): Unit = addDndListener(component, onDragEnter(action))
  private def onDragOver(component: Component, action: DropTargetDragEvent => Unit): Unit = addDndListener(component, onDragOver(action))
  private def onDropActionChanged(component: Component, action: DropTargetDragEvent => Unit): Unit = addDndListener(component, onDropActionChanged(action))
  private def onDragExit(component: Component, action: DropTargetEvent => Unit): Unit = addDndListener(component, onDragExit(action))
  private def onDrop(component: Component, action: DropTargetDropEvent => Unit): Unit = addDndListener(component, onDrop(action))

  private def onDragEnter(action: DropTargetDragEvent => Unit): DropTargetListener = new DropTargetAdapter() { override def dragEnter(e: DropTargetDragEvent) = action(e) }
  private def onDragOver(action: DropTargetDragEvent => Unit): DropTargetListener = new DropTargetAdapter() { override def dragOver(e: DropTargetDragEvent) = action(e) }
  private def onDropActionChanged(action: DropTargetDragEvent => Unit): DropTargetListener = new DropTargetAdapter() { override def dropActionChanged(e: DropTargetDragEvent) = action(e) }
  private def onDragExit(action: DropTargetEvent => Unit): DropTargetListener = new DropTargetAdapter() { override def dragExit(e: DropTargetEvent) = action(e) }
  private def onDrop(action: DropTargetDropEvent => Unit): DropTargetListener = new DropTargetAdapter() { override def drop(e: DropTargetDropEvent) = action(e) }

  // TODO: what to do if both string and file can be dropped?

  def onDropString(component: Component, action: String => Unit) = onDropType(component, DataFlavor.stringFlavor, _.map(_.asInstanceOf[String]) match {
    case Success(string) => action(string)
    case Failure(t)      => Log.error("Getting String from drop data was not successful", t)
  })

  def onDropFile(component: Component, action: File => Unit) = onDropType(component, DataFlavor.javaFileListFlavor, _.map(_.asInstanceOf[java.util.List[File]]) match {
    case Success(droppedFiles) => action(droppedFiles.toList.last)
    case Failure(t)            => Log.error("Getting one file from drop data was not successful", t)
  })

  def onDropFiles(component: Component, action: List[File] => Unit) = onDropType(component, DataFlavor.javaFileListFlavor, _.map(_.asInstanceOf[java.util.List[File]]) match {
    case Success(droppedFiles) => action(droppedFiles.toList)
    case Failure(t)            => Log.error("Getting files from drop data was not successful", t)
  })

  //TODO: should be tested!!!
  private class ExtendableDropTarget() extends DropTarget {
    var listeners = List[DropTargetListener]()
    super.addDropTargetListener(new DropTargetListener() {
      override def dragEnter(e: DropTargetDragEvent) = listeners foreach { _.dragEnter(e) }
      override def dragOver(e: DropTargetDragEvent) = listeners foreach { _.dragOver(e) }
      override def dropActionChanged(e: DropTargetDragEvent) = listeners foreach { _.dropActionChanged(e) }
      override def dragExit(e: DropTargetEvent) = listeners foreach { _.dragExit(e) }
      override def drop(e: DropTargetDropEvent) = listeners foreach { _.drop(e) }
    })

    override def addDropTargetListener(dtl: DropTargetListener) = listeners = listeners :+ dtl
    override def removeDropTargetListener(dtl: DropTargetListener) = listeners = listeners.filter(_ == dtl)
  }

  def addDndListener(component: Component, listener: DropTargetListener) = {
    if (component.getDropTarget == null) component.setDropTarget(new ExtendableDropTarget())
    val dropTarget = component.getDropTarget.addDropTargetListener(listener)
  }

  private def onDropType(component: Component, dataFlavor: DataFlavor, callback: scala.util.Try[Object] => Unit) = {

    //FIXME duplicate of log
    def stackElementsNotToLog(stackTraceElement: StackTraceElement) = !stackTraceElement.getClassName.contains("Plus")
		def stackElementWhereLogWasCalled = Thread.currentThread.getStackTrace.filter(stackElementsNotToLog)(1) // TODO use lift
    def stackClassAndMethod = stackElementWhereLogWasCalled.getClassName.substring(stackElementWhereLogWasCalled.getClassName.lastIndexOf(".") + 1) + "." + stackElementWhereLogWasCalled.getMethodName
    val codeLocationDropWasSet = stackClassAndMethod

    
    onDragEnter(component, dragEvent => {
      Log.debug(s"onDragEnter $codeLocationDropWasSet")
      Log.debug(dragEvent)
      Log.debug(dragEvent.getCurrentDataFlavors.mkString("\r\n"))
      Log.debug(dragEvent.getDropAction + "")
      Log.debug(dragEvent.getDropTargetContext.getComponent)
      Log.debug(dragEvent.getDropTargetContext.getDropTarget)
      Log.debug(dragEvent.getTransferable)
      Log.debug(dragEvent.getTransferable.getTransferDataFlavors.mkString("\r\n"))
      dragEvent.acceptDragBasedOnDataFlavor(dataFlavor)
    })

    onDragOver(component, dragEvent => { Log.trace(s"onDragOver $codeLocationDropWasSet") })
    onDropActionChanged(component, dragEvent => { Log.debug(s"onDropActionChanged $codeLocationDropWasSet") })
    onDrop(component, dropEvent => { Log.info(s"onDrop $codeLocationDropWasSet"); callback(getTransferableData(dataFlavor)(dropEvent)) })
    onDragExit(component, dropTargetEvent => { Log.debug(s"onDragExit $codeLocationDropWasSet") })
  }

  def getTransferableData(dataFlavor: DataFlavor)(dropEvent: DropTargetDropEvent) = Try {
    if (dropEvent.acceptDragBasedOnDataFlavor(dataFlavor)) dropEvent.getTransferable.getTransferData(dataFlavor)
    else Failure(new DataFlavorNotSupported("DataFlavor.plainTextFlavor not supported"))
  }

  class DataFlavorNotSupported(msg: String) extends Exception(msg)
}