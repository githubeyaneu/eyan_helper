package eu.eyan.util.awt.dnd

import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.Component
import java.awt.dnd.DropTargetListener
import java.awt.dnd.DropTarget

object DndPlus {
  //DropTargetListener
  class DropTargetAdapter extends java.awt.dnd.DropTargetAdapter { def drop(e: DropTargetDropEvent) = {} }
  
  def onDragEnter(action: DropTargetDragEvent => Unit):DropTargetListener = new DropTargetAdapter() { override def dragEnter(e: DropTargetDragEvent) = action(e) }
  def onDragOver(action: DropTargetDragEvent => Unit):DropTargetListener = new DropTargetAdapter() { override def dragOver(e: DropTargetDragEvent) = action(e) }
  def onDropActionChanged(action: DropTargetDragEvent => Unit):DropTargetListener = new DropTargetAdapter() { override def dropActionChanged(e: DropTargetDragEvent) = action(e) }
  def onDragExit(action: DropTargetEvent => Unit):DropTargetListener = new DropTargetAdapter() { override def dragExit(e: DropTargetEvent) = action(e) }
  def onDrop(action: DropTargetDropEvent => Unit):DropTargetListener = new DropTargetAdapter() { override def drop(e: DropTargetDropEvent) = action(e) }

  def onDragEnter(component: Component, action: DropTargetDragEvent => Unit):Unit = addDndListener(component, onDragEnter(action))
  def onDragOver(component: Component, action: DropTargetDragEvent => Unit):Unit = addDndListener(component, onDragOver(action))
  def onDropActionChanged(component: Component, action: DropTargetDragEvent => Unit):Unit =addDndListener(component, onDropActionChanged(action))
  def onDragExit(component: Component, action: DropTargetEvent => Unit):Unit = addDndListener(component, onDragExit(action))
  def onDrop(component: Component, action: DropTargetDropEvent => Unit):Unit = addDndListener(component, onDrop(action))
  
  
  //TODO: must be tested!!!
  private class ExtendableDropTarget() extends DropTarget{
    var listeners = List[DropTargetListener]()
    super.addDropTargetListener(new DropTargetListener(){
     override def dragEnter(e: DropTargetDragEvent) = listeners foreach {_.dragEnter(e)}                 
     override def dragOver(e: DropTargetDragEvent) = listeners foreach {_.dragOver(e)}                    
     override def dropActionChanged(e: DropTargetDragEvent) = listeners foreach {_.dropActionChanged(e)}
     override def dragExit(e: DropTargetEvent) = listeners foreach {_.dragExit(e)}                            
     override def drop(e: DropTargetDropEvent) = listeners foreach {_.drop(e)}
    })
    
    override def addDropTargetListener(dtl: DropTargetListener) = listeners = listeners :+ dtl
    override def removeDropTargetListener(dtl: DropTargetListener) = listeners = listeners.filter(_==dtl)
  }
  
  def addDndListener(component: Component, listener: DropTargetListener) = {
    if (component.getDropTarget == null) component.setDropTarget(new ExtendableDropTarget()) 
    val dropTarget = component.getDropTarget.addDropTargetListener(listener)
  }
}