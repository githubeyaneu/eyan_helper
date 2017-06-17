package eu.eyan.util.swing

import javax.swing.JToggleButton
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit

object JToggleButtonPlus {
  implicit class JToggleButtonImplicit[TYPE <: JToggleButton](jToggleButton: TYPE) extends AbstractButtonImplicit(jToggleButton){
//    updateUI()
  }
}