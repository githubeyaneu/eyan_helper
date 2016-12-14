package eu.eyan.util.swing

import java.awt.event.ActionEvent

import eu.eyan.util.awt.AwtHelper.newActionListener
import eu.eyan.util.awt.AwtHelper.runInWorker
import javax.swing.JButton

class JButtonPlus(text: String) extends JButton(text) {
  def addAction(action: ActionEvent => Unit) = {
    this.addActionListener(newActionListener(e => {
      setEnabled(false)
      runInWorker(() => action.apply(e), () => setEnabled(true))
    }))
  }

  def disabled = {
    this.setEnabled(false)
    this
  }
}