package eu.eyan.util.awt

import javax.swing.JLabel
import java.awt.Component

object ComponentPlus {

  implicit class ComponentPlusImplicit[TYPE <: Component](component: TYPE) {
    def withName(name: String) = {component.setName(name); component}
    def withEnabled(enabled: Boolean) = {component.setEnabled(enabled); component}
  }
}