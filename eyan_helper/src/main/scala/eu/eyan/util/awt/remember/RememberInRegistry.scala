package eu.eyan.util.awt.remember

import java.awt.Component
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.registry.RegistryPlus

trait RememberInRegistry[T <: Component] {
  def rememberValueInRegistry(name: String): T =
    {
      val comp = rememberComponent
      def remember = if (comp.windowName != null) RegistryPlus.write(comp.windowName, comp.componentPath + name, rememberValueGet)
      rememberEventListener(remember)

      def regValue = RegistryPlus.read(comp.windowName, comp.componentPath + name)
      comp.onHierarchyChanged(
        if (regValue != null) rememberValueSet(regValue)
      //else Log.error(s"set name for the window to remember the value for component ${jTextComponent.windowName}, ${jTextComponent.componentPath}, $name")
      )

      comp
    }

  protected def rememberComponent: T
  protected def rememberEventListener(action: => Unit): T
  protected def rememberValueGet: String
  protected def rememberValueSet(value: String)
}