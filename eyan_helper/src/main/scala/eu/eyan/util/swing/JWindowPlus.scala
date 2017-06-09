package eu.eyan.util.swing

import javax.swing.JWindow
import eu.eyan.util.awt.WindowPlus.WindowPlusImplicit

object JWindowPlus {
  implicit class JWindowPlusImplicit[TYPE <: JWindow](jWindow: TYPE) extends WindowPlusImplicit(jWindow){ }
}