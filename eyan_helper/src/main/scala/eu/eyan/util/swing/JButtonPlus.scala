package eu.eyan.util.swing

import java.awt.event.ActionEvent
import eu.eyan.util.awt.AwtHelper.onActionPerformed
import eu.eyan.util.awt.AwtHelper.runInWorker
import javax.swing.JButton
import javax.swing.AbstractButton
import eu.eyan.util.awt.AwtHelper

object JButtonPlus {
  implicit class JButtonImplicit[TYPE <: AbstractButton](button: TYPE) {
    def onAction[A](action: () => A): TYPE = { button.addActionListener(AwtHelper.onActionPerformed(e => action())); button }
  }
}

class JButtonPlus(text: String) extends JButton(text) {
  def addAction(action: ActionEvent => Unit) = {
    this.addActionListener(onActionPerformed(e => {
      setEnabled(false)
      runInWorker(() => action.apply(e), () => setEnabled(true))
    }))
    this
  }


  def disabled = {
    this.setEnabled(false)
    this
  }
}