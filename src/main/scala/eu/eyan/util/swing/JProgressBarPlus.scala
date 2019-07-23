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

  private def setNewValue(newValue: Int) = { setString(String.format(format, newValue:Integer)); setValue(newValue); this }

  def valueChanged(newValue: Int) = { if (this.getValue != newValue) invokeLater { this.setVisible(true); setNewValue(newValue) }; this }

  // refactor it.
  def doneThenInvisible = { finished; invokeLater(setVisible(false)); this }

  def finished = { invokeLater({ this.setString(finishedText); this.setValue(this.getMaximum) }); this }

  def setFormat(newFormat: String) = { format = newFormat; setNewValue(this.getValue); this }
}