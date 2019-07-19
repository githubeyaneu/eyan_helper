package eu.eyan.util.swing

import javax.swing.JSeparator
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.SwingConstants
import javax.swing.plaf.SeparatorUI

object JSeparatorPlus {
  implicit class JSeparatorImplicit[TYPE <: JSeparator](jSeparator: TYPE) extends JComponentImplicit(jSeparator) {
    def orientation(orientation: Int) = { jSeparator.setOrientation(orientation); jSeparator }
    def orientation_Horizontal = orientation(SwingConstants.HORIZONTAL)
    def orientation_Vertical = orientation(SwingConstants.VERTICAL)
    
    def ui(ui: SeparatorUI) = { jSeparator.setUI(ui); jSeparator }
  }
}