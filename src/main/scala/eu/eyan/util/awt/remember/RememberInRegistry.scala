package eu.eyan.util.awt.remember

import java.awt.Component
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.registry.RegistryPlus
import eu.eyan.log.Log
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableImplicit

trait RememberInRegistry[T <: Component] {
  def rememberValueInRegistry(name: String, saveEnabledObservable: Observable[Boolean] = BehaviorSubject(true)): T = {
    def saveEnabled: Boolean = saveEnabledObservable.take1Synchronous
    val comp = rememberComponent
    def nameInRegistry = if (name == null || name == "") comp.componentPath else name
    def regValue = {
      if (comp.windowName != null) RegistryPlus.readOption(comp.windowName, nameInRegistry)
      else None
    }

    def saveValueToRegistry = if (saveEnabled) {
      val windowName = comp.windowName
      if (windowName != null) RegistryPlus.write(windowName, nameInRegistry, rememberValueGet)
      //else Log.warn("windowName null " + nameInRegistry+", isShowing:"+comp.isShowing) //log not needed, because it can oft happen that a non showing element is changed programmatically. in this case we dont save it.
    }
    def deleteValueFromRegistry = {
      val windowName = comp.windowName
      if (windowName != null) RegistryPlus.delete(windowName, nameInRegistry)
    }
    def readValueFromRegistryAndSetInTheComponent = {
      if (saveEnabled) {
        val valueInRegistry = regValue
        if (valueInRegistry.nonEmpty) rememberValueSet(valueInRegistry.get)
      }
    }

    def saveOrDeleteFromRegistry(enabled: Boolean) = {
      if (enabled) { readValueFromRegistryAndSetInTheComponent; saveValueToRegistry }
      else deleteValueFromRegistry
    }

    saveEnabledObservable.foreach(saveOrDeleteFromRegistry)

    rememberEventListener(saveValueToRegistry)

    comp.onHierarchyChanged({ if (!saveEnabled) { deleteValueFromRegistry }; readValueFromRegistryAndSetInTheComponent })

    comp
  }

  protected def rememberComponent: T
  protected def rememberEventListener(action: => Unit): T
  protected def rememberValueGet: String
  protected def rememberValueSet(value: String)
}