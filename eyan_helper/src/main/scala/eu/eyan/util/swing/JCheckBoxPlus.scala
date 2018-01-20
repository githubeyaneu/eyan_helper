package eu.eyan.util.swing

import eu.eyan.util.swing.JToggleButtonPlus.JToggleButtonImplicit
import javax.swing.JCheckBox

object JCheckBoxPlus {
  implicit class JCheckBoxImplicit[TYPE <: JCheckBox](jCheckBox: TYPE) extends JToggleButtonImplicit(jCheckBox) {
    //TODO implement the rest
  }
}