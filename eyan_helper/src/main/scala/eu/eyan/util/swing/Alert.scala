package eu.eyan.util.swing

import javax.swing.JOptionPane
import java.awt.Component
import javax.swing.JDialog
import eu.eyan.log.Log

object Alert {
  def alert(msg: String) = alertFalse(msg)
  def alertFalse(msg: String) = { JOptionPane.showMessageDialog(null, msg); false }
  def alertOptions[TYPE <: Object](msg: String, options: Array[TYPE]): Option[TYPE] = {
    val idx = JOptionPane.showOptionDialog(null, msg, null, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options.asInstanceOf[Array[Object]], "a")
    Log.debug("Idx: " + idx)
    val result = options.lift(idx)
    Log.debug("Result: " + result)
    result
  }
}