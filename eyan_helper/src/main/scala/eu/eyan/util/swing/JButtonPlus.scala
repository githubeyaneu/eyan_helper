package eu.eyan.util.swing

import javax.swing.JButton
import java.awt.event.ActionEvent
import eu.eyan.util.awt.AwtHelper._

class JButtonPlus(text:String) extends JButton(text) {
  def addAction(action: ActionEvent => Unit) = {
    this.addActionListener( newActionListener( e => {
      setEnabled(false)
      runInWorker(() => action.apply(e), () => setEnabled(true))
    } )) 
  }
  
  def disabled = {
    this.setEnabled(false)
    this
  }
}