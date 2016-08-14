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

object AwtHelper {

  def newActionListener(action: ActionEvent => Unit) = new ActionListener() { override def actionPerformed(e: ActionEvent) = action(e) }

  def newActionListener(action: () => Unit) = new ActionListener() { override def actionPerformed(e: ActionEvent) = action() }

  def newRunnable(runnable: () => Unit) = new Runnable() { override def run() = runnable() }

  def newWindowClosingEvent(action: WindowEvent => Unit) = new WindowAdapter() { override def windowClosing(e: WindowEvent) = action(e) }

  def positionToCenter(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    val width = component.getSize().width
    val height = component.getSize().height
    component.setSize(width, height)
    component.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2)
  }

  def positionToLeft(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    component.setSize(screenSize.width / 2, screenSize.height)
    component.setLocation(0, 0)
  }

  def positionToRight(component: Component) = {
    val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
    component.setSize(screenSize.width / 2, screenSize.height)
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