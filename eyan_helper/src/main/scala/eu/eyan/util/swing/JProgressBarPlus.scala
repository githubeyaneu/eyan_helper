package eu.eyan.util.swing

import SwingPlus._
import eu.eyan.util.awt.AwtHelper
import javax.swing.JProgressBar

class JProgressBarPlus(min: Int, max: Int, var format: String) extends JProgressBar(min, max) {
	this.setNewValue(min)

	private var percentChangedConsumer = createPercentChangedConsumer
  private var finishedText = "Ready";


  def setNewValue(percent: Int) = {
    this.setString(String.format(format, percent: Integer))
    this.setValue(percent)
    this
  }

  def createPercentChangedConsumer: Int => Unit = percent => invokeLater(() => { this.setVisible(true); setNewValue(percent); })

  def percentChanged(value: Int) = { if (this.getValue() != value) this.percentChangedConsumer(value); this }

  def doneThenInvisible = () => invokeLater(() => setVisible(false))

  def setFormat(format: String) = {
    this.format = format
    setNewValue(this.getValue())
    this
  }

  def finished = { invokeLater(() => { this.setString(finishedText); this.setValue(this.getMaximum()) }); this }
}