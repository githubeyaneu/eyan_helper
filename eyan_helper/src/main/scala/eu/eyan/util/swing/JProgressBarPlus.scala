package eu.eyan.util.swing

import SwingPlus.invokeLater
import javax.swing.JProgressBar
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object JProgressBarPlus {
  implicit class JProgressBarImplicit[TYPE <: JProgressBar](jProgressBar: TYPE) extends JComponentImplicit(jProgressBar) {
    //    addChangeListener(ChangeListener)
    //    removeChangeListener(ChangeListener)
    //    setBorderPainted(boolean)
    //    setIndeterminate(boolean)
    //    setMaximum(int)
    //    setMinimum(int)
    //    setModel(BoundedRangeModel)
    //    setOrientation(int)
    //    setString(String)
    //    setStringPainted(boolean)
    //    setUI(ProgressBarUI)
    //    setValue(int)
    //    updateUI()
  }
}
class JProgressBarPlus(min: Int = 0, max: Int = 100, var format: String = "%d%%", finishedText: String = "Ready") extends JProgressBar(min, max) {

  setNewValue(min)

  def setNewValue(percent: Int) = { setString(String.format(format, percent: Integer)); setValue(percent); this }

  def percentChanged(percent: Int) = { if (this.getValue() != percent) invokeLater { this.setVisible(true); setNewValue(percent) }; this }

  // refactor it.
  def doneThenInvisible = { finished; invokeLater(setVisible(false)); this }

  def finished = { invokeLater({ this.setString(finishedText); this.setValue(this.getMaximum()) }); this }

  def setFormat(newFormat: String) = {format = newFormat; this}
}