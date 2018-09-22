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
      button.setIcon(new ImageIcon("icons/tabs/close_inactive.png".toResourceFile.get.toURL))
      button.setRolloverIcon(new ImageIcon("icons/tabs/close_onmouse.png".toResourceFile.get.toURL))
      button.setRolloverSelectedIcon(new ImageIcon("icons/tabs/close_activetab.png".toResourceFile.get.toURL))
      button.onClicked(onCloseAction(component))
      panel.newRow("1px")
      

      jTabbedPane.add(component)
      def idx = jTabbedPane.indexOfComponent(component)
      jTabbedPane.setTabComponentAt(idx, panel)
      jTabbedPane
    }
  }
}

trait WithComponent{
  def getComponent:Component
}
class JTabbedPanePlus[TYPE <: WithComponent] extends JTabbedPane {
  private val tabsMap = ListBuffer[TYPE]()
  
  val activeTab = BehaviorSubject[Option[TYPE]](None)
  
  def getActiveTab = activeTab.get[Option[TYPE]]
  
  this.onSelectionChanged(activeTab.onNext(tabsMap.lift(getSelectedIndex)))
  
  def addTab(item:TYPE, title: Text, toolTip: String, onCloseAction: TYPE => Unit) = {
    tabsMap.+=(item)
    this.addTabWithCloseButton(item.getComponent, title, toolTip, component => onCloseAction(item))
    setSelectedComponent(item.getComponent)
  }
  
  def removeTab(itemToRemove: TYPE) = {
	  tabsMap  -=  itemToRemove
    remove(itemToRemove.getComponent)
  }
  
  def notContainsTab(item: TYPE) = !containsTab(item)
  
  def containsTab(item: TYPE) = tabsMap.contains(item)
  
  def items = tabsMap
}