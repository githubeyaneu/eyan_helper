package eu.eyan.util.awt;

import java.awt.Component

import scala.collection.mutable.ListBuffer

import eu.eyan.log.Log
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.awt.remember.RememberInRegistry
import scala.collection.mutable.MutableList

abstract class MultiField[INPUT, EDITOR <: Component](name: String) extends JPanel with RememberInRegistry[MultiField[INPUT, EDITOR]] {

  def getValues: List[INPUT] = editors.map(_.editor).map(getValue).toList.filter(_.nonEmpty).map(_.get)

  def setValues(values: List[INPUT]) = {
    Log.debug(getName + ": " + values.mkString(", "))
    removeAll
    editors.clear

    values foreach addEditorWithValue
    addEditorEmpty
  }
  
  val changedListeners = MutableList[() => Unit]()
  def onChanged(action: () => Unit) = changedListeners += action
  private def onChange = changedListeners.foreach(_())

  protected def createEditor(fieldEdited: EDITOR => Unit): EDITOR
  protected def getValue(editor: EDITOR): Option[INPUT]
  protected def setValueInEditor(editor: EDITOR)(value: INPUT): Unit
  protected def valueToString(value: INPUT): String
  protected def stringToValue(string: String):INPUT

  protected def rememberComponent: MultiField[INPUT, EDITOR] = this 
  
  protected def rememberEventListener(action: => Unit): MultiField[INPUT, EDITOR] = {
    rememberEventListenerAction = () => action
    this
  }
  
  protected def rememberValueGet: String = {
    val values = editors.toList.map(_.editor).map(getValue).flatten.map(valueToString).mkString(" + ")
    Log.debug(values)
    values
  }
  
  protected def rememberValueSet(value: String) = {
    Log.debug(value)
    val values = value.split(" \\+ ").toList.map(stringToValue)
    Log.debug(values)
    setValues(values)
  }

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  private val editors: ListBuffer[Editor[EDITOR]] = ListBuffer()
  private var counter = 0
  private var rememberEventListenerAction: () => Unit = () => {}

  private case class Editor[EDITOR](val editor: EDITOR, val deleteButton: JButton)

  private def addEditorEmpty = addEditor(None)
  private def addEditorWithValue(input: INPUT) = addEditor(Some(input))

  private def addEditor(input: Option[INPUT]): Unit = {
    counter = counter + 1
    val editor = createEditor(addEditorIfLastAndRemember).name(name + counter)
    input.foreach(setValueInEditor(editor))

    val fieldPanel = new JPanelWithFrameLayout()
      .withSeparators
      .newColumn("f:p:g")
      .newRow("f:p:g")
      .name(name + ".panel." + counter)
      .addFluent(editor)
      .newColumn("30dlu")

    val deleteButton = fieldPanel
      .addButton("x")
      .name(name + ".delete." + counter)
      .enabled(input.nonEmpty)
      .onAction(removeEditor(editor))

    editors += new Editor[EDITOR](editor, deleteButton)
    add(fieldPanel)

    Log.debug(name + counter)
  }

  private def removeEditor(editor: EDITOR) = {
    editors.find(_.editor == editor).foreach(field => editors -= field)
    remove(editor.getParent)
    onChange
  }

  private def addEditorIfLastAndRemember(editor: EDITOR) = {
    if (editors.last.editor == editor) {
      editors.last.deleteButton.enabled
      addEditorEmpty
    }
    rememberEventListenerAction()
    onChange
  }
}