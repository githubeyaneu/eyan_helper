package eu.eyan.util.swing

import javax.swing.JToggleButton
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit
import eu.eyan.util.registry.RegistryPlus
import eu.eyan.util.awt.remember.RememberInRegistry

object JToggleButtonPlus {
  implicit class JToggleButtonImplicit[TYPE <: JToggleButton](jToggleButton: TYPE) extends AbstractButtonImplicit(jToggleButton) with RememberInRegistry[TYPE] {
    //    updateUI()

    protected def rememberComponent = jToggleButton
    protected def rememberEventListener(action: => Unit) = onItemStateChange(action)
    protected def rememberValueGet = jToggleButton.isSelected().toString
    protected def rememberValueSet(value: String) = jToggleButton.setSelected(value.toLowerCase == "true")
  }
}