package eu.eyan.util.swing

import javax.swing.JOptionPane
import java.awt.Component
import javax.swing.JDialog
import eu.eyan.log.Log
import eu.eyan.util.text.Text
import rx.lang.scala.Subscription

object Alert {
  def alert(title: Text, txt: Text, confirmText: Text) = alertOptions(title.get, txt.get, Array(confirmText.get))
  def alert(msg: String): Boolean = alertFalse(msg)
  def alertFalse(msg: String) = { JOptionPane.showMessageDialog(null, msg); false }
  def alertOptions[TYPE <: Object](title: String, msg: String, options: Array[TYPE]): Option[TYPE] = {
    val idx = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options.asInstanceOf[Array[Object]], "a")
    Log.debug("Idx: " + idx)
    val result = options.lift(idx)
    Log.debug("Result: " + result)
    result
  }
}