package eu.eyan.util.swing

import java.awt.Component
import java.awt.Insets

import scala.collection.mutable.Map

import eu.eyan.util.awt.remember.RememberInRegistry
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.swing.JLabelPlus.JLabelImplicit
import eu.eyan.util.swing.JTabbedPanePlus.JTabbedPaneImplicit
import javax.swing.ImageIcon
import javax.swing.JTabbedPane
import javax.swing.event.ChangeEvent
import scala.collection.mutable.MutableList
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit
import scala.collection.mutable.ListBuffer
import eu.eyan.util.text.Text
import javax.swing.Icon
import rx.lang.scala.Observable
import eu.eyan.log.Log

object JTabbedPanePlus {
  implicit class JTabbedPaneImplicit[TYPE <: JTabbedPane](jTabbedPane: TYPE) extends RememberInRegistry[TYPE] {

    def onSelectionChanged(action: => Unit): TYPE = onSelectionChangedEvent(e => action)
    def onSelectionChangedEvent(action: ChangeEvent => Unit) = { jTabbedPane.addChangeListener(SwingPlus.onStateChanged(action)); jTabbedPane }

    protected def rememberComponent = jTabbedPane
    protected def rememberEventListener(action: => Unit) = onSelectionChanged(action)
    protected def rememberValueGet = jTabbedPane.getSelectedIndex.toString
    protected def rememberValueSet(value: String) = jTabbedPane.setSelectedIndex(value.toInt)

    def addTabWithCloseButton(component: Component, title: Text, toolTip: String, onCloseAction: Component => Unit) = {
      val panel = new JPanelWithFrameLayout()
      panel.newRow("1px")
      panel.newRowFPG
      panel.newColumn.addLabel("").text(title).tooltipText(toolTip).onClicked(jTabbedPane.setSelectedComponent(component))
      panel.newColumn("3px")
      panel.setOpaque(false)
      val button = panel.newColumn.addButton("")
      button.setMargin(new Insets(0, 0, 0, 0))
      button.setIcon("icons/tabs/close_inactive.png".toIconAsResource)
      button.setRolloverIcon("icons/tabs/close_onmouse.png".toIconAsResource)
      button.setRolloverSelectedIcon("icons/tabs/close_activetab.png".toIconAsResource)
      button.onMouseReleased(onCloseAction(component))
      panel.newRow("1px")

      jTabbedPane.add(component)
      def idx = jTabbedPane.indexOfComponent(component)
      jTabbedPane.setTabComponentAt(idx, panel)
      jTabbedPane
    }

    def addTabWithCondition(condition: Observable[Boolean], title: String, icon: Icon, component: Component, tip: String) = {
      condition.foreach(tabVisible => {
        val componentAlreadyAdded = jTabbedPane.getComponents.contains(component)
        Log.debug(s"title=$title, tabVisible=$tabVisible, componentAlreadyAdded=$componentAlreadyAdded")

        if (tabVisible) {
          if (!componentAlreadyAdded) {
            Log.debug(s"add tab")
            SwingPlus.invokeLater(jTabbedPane.addTab(title, icon, component, tip))
          } else {
            Log.debug(s"already added")
          }
        } else {
          if (componentAlreadyAdded) {
            Log.debug(s"remove tab")
            SwingPlus.invokeLater(jTabbedPane.remove(component))
          } else {
            Log.debug(s"already removed")
          }
        }
      })
      jTabbedPane
    }
  }
}

class JTabbedPanePlus[TYPE <: WithComponent] extends JTabbedPane with RememberInRegistry[JTabbedPanePlus[TYPE]] { //TODO rename? new class
  def tabsObservable = tabs.distinctUntilChanged

  def activeTabObservable = activeTab.distinctUntilChanged

  def getActiveTab = activeTab.take1Synchronous[Option[TYPE]]

  def addTab(item: TYPE, title: Text, toolTip: String, onCloseAction: TYPE => Unit) = {
    tabsMap.+=(item)
    tabs.onNext(tabsMap.toList)
    this.addTabWithCloseButton(item.getComponent, title, toolTip, component => onCloseAction(item))
    setSelectedComponent(item.getComponent)
  }

  def removeTab(itemToRemove: TYPE) = {
    tabsMap -= itemToRemove
    tabs.onNext(tabsMap.toList)
    remove(itemToRemove.getComponent)
  }

  def notContainsTab(item: TYPE) = !containsTab(item)

  def containsTab(item: TYPE) = tabsMap.contains(item)

  def items = tabsMap.toList

  private val tabsMap = ListBuffer[TYPE]() //TODO refactor: let as list in the tabs BehaviorSubject

  private val tabs = BehaviorSubject[List[TYPE]](List[TYPE]())
  private val activeTab = BehaviorSubject[Option[TYPE]](None)

  this.onSelectionChanged(activeTab.onNext(tabsMap.lift(getSelectedIndex)))
  
  protected def rememberComponent: JTabbedPanePlus[TYPE] = this
  protected def rememberEventListener(action: => Unit): JTabbedPanePlus[TYPE] = this.onSelectionChanged(action)
  protected def rememberValueGet: String = this.getSelectedIndex+""
  protected def rememberValueSet(value: String): Unit = this.setSelectedIndex(value.toInt)

}