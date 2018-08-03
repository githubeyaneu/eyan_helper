package eu.eyan.util.swing

import javax.swing.JOptionPane

object Alert {
  def alert(msg: String) = alertFalse(msg)
  def alertFalse(msg: String) = {JOptionPane.showMessageDialog(null, msg); false}
}