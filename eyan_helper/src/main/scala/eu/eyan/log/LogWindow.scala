package eu.eyan.log

import java.awt.Component
import java.awt.Dialog.ModalExclusionType

import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.JFrame
import javax.swing.WindowConstants
import eu.eyan.util.swing.JTextAreaPlus.JTextAreaImplicit
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit

object LogWindow {
  val window = new LogWindow

  def show(origin: Component = null) = {
    window.frame.packAndSetVisible
    window.frame.positionToRight
    if (origin != null) origin.positionToLeft  
  }

  def add(text: String) = {
    if (window!=null && window.frame != null /*&& window.frame.isVisible*/) {
      window.textArea.append(text + "\n")
      window.textArea.invalidate()
      window.textArea.repaint()
      window.textArea.validate()
    }
  }
  
  def addToOut(b:Int)= window.outTextArea.append(b.toChar.toString)
  def addToErr(b:Int)= window.errTextArea.append(b.toChar.toString)
  
  def setLevel(level: LogLevel) = if(window!=null) window.level(level)

  def close() = window.frame.dispose
  
  def logs = window.textArea.getText
  def logsOut = window.outTextArea
  def logsErr = window.errTextArea.getText
}

class LogWindow {
  val DEFAULT_WIDTH = 800
  val DEFAULT_HEIGHT = 600
  def level(level:LogLevel) = frame.title("Log "+level)

  val content = new JPanelWithFrameLayout().withBorders.withSeparators.newColumn("f:1px:g")

  val buttons = content.addPanelWithFormLayout().withSeparators.newColumn()
  buttons.addButton("Clear").onAction_disableEnable({textArea.setText(""); outTextArea.setText(""); errTextArea.setText("")})
  List(None, Fatal, Error, Warn, Info, Debug, Trace).foreach( 
   level => buttons.newColumn.addButton(level.toString).onAction_disableEnable(Log.activate(level))
  )
  
  content.newRow.addSeparatorWithTitle("Logs")
  val textArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown
  content.newRow.addSeparatorWithTitle("Console out")
  val outTextArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown
  content.newRow.addSeparatorWithTitle("Console err")
  val errTextArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown

  val frame = new JFrame("").withComponent(content).size(DEFAULT_WIDTH, DEFAULT_HEIGHT).onCloseDispose.modalExclusionType_ApplicationExclude
}