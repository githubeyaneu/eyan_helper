package eu.eyan.util.swing

import java.awt.event.ActionEvent
import eu.eyan.util.awt.AwtHelper.newActionListener
import eu.eyan.util.awt.AwtHelper.runInWorker
import javax.swing.JButton
import javax.swing.AbstractButton
import eu.eyan.util.awt.AwtHelper

object JButtonPlus {
  implicit class JButtonImplicit[TYPE <: AbstractButton](button: TYPE) {
    def onAction[A](action: () => A): TYPE = { button.addActionListener(AwtHelper.newActionListener(() => action())); button }
  }
}

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