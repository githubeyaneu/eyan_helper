package eu.eyan.util.awt

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.Component
import java.awt.Toolkit
import javax.swing.SwingWorker
import java.awt.event.WindowListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.Window
import eu.eyan.log.Log
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

object AwtHelper {

  def newActionListener(action: ActionEvent => Unit) = new ActionListener() { override def actionPerformed(e: ActionEvent) = action(e) }

  def newActionListener(action: () => Unit) = new ActionListener() { override def actionPerformed(e: ActionEvent) = action() }

  def newRunnable(runnable: () => Unit) = new Runnable() { override def run() = runnable() }

  def newWindowClosingEvent(action: WindowEvent => Unit) = new WindowAdapter() { override def windowClosing(e: WindowEvent) = action(e) }

  def mouseClick(action: () => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if(e.getClickCount==1)  action() }

  def doubleClick(action: () => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if(e.getClickCount==2)  action() }

  def tripleClick(action: () => Unit) = new MouseAdapter { override def mouseClicked(e: MouseEvent) = if(e.getClickCount==3)  action() }

  def mouseReleased(action: () => Unit) = new MouseAdapter { override def mouseReleased(e: MouseEvent) = action() }
  
  def mousePressed(action: () => Unit) = new MouseAdapter { override def mousePressed(e: MouseEvent) = action() }
  
  def componentMoved(action: () => Unit) = new ComponentAdapter { override def componentMoved(e: ComponentEvent) = action() }
  
  def chooseFile(action: File => Unit, extension:String ="") = { 
    val fc = new JFileChooser
    if(!extension.isEmpty) fc.setFileFilter(new FileNameExtensionFilter(extension + " files", extension))
    if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) action(fc.getSelectedFile) 
  }
  
  def docListener(action: () => Unit) = new DocumentListener() { 
    override def insertUpdate(e: DocumentEvent) = action()
		override def removeUpdate(e: DocumentEvent) = {}
		override def changedUpdate(e: DocumentEvent) = action()
  }

  def positionToCenter[A<:Component](component: A):A  = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    val width = component.getSize().width
    val height = component.getSize().height
    component.setSize(width, height)
    component.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2)
    component
  }

  def positionToLeft(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    component.setSize(screenSize.width / 2, screenSize.height - 30)
    component.setLocation(0, 0)
  }

  def positionToRight(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    component.setSize(screenSize.width / 2, screenSize.height - 30)
    component.setLocation(screenSize.width / 2, 0)
  }

  def runInWorker(work: () => Unit, doAtDone: () => Unit) = {
    new SwingWorker[Void, Void]() {
      override def doInBackground() = { work.apply(); null }
      override def done() = doAtDone.apply()
    }.execute()
  }

  def tryToEnlargeWindow(window: Window) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    val origPos = window.getBounds
    Log.debug("orig: " + origPos)
    window.pack
    val newPos = window.getBounds
    // Fuck hack :(
    if (newPos.y + newPos.height > screenSize.height - 40) {
      newPos.height = screenSize.height - 40 - newPos.y
      newPos.width = newPos.width + 30
      window.setBounds(newPos)
    }
    Log.debug("new: " + newPos)
  }
}