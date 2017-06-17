package eu.eyan.util.swing

import javax.swing.JOptionPane
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import java.awt.Component
import javax.swing.Icon
import javax.swing.plaf.OptionPaneUI

object JOptionPanePlus {
  implicit class JOptionPaneImplicit[TYPE <: JOptionPane](jOptionPane: TYPE) extends JComponentImplicit(jOptionPane) {

    def selectInitialValue = { jOptionPane.selectInitialValue(); jOptionPane }
    def icon(icon: Icon) = { jOptionPane.setIcon(icon); jOptionPane }
    def initialSelectionValue(value: Object) = { jOptionPane.setInitialSelectionValue(value); jOptionPane }
    def initialValue(initialValue: Object) = { jOptionPane.setInitialValue(initialValue); jOptionPane }
    def inputValue(value: Object) = { jOptionPane.setInputValue(value); jOptionPane }
    def message(message: Object) = { jOptionPane.setMessage(message); jOptionPane }
    def messageType(messageType: Int) = { jOptionPane.setMessageType(messageType); jOptionPane }
    def messageType_Error = messageType(JOptionPane.ERROR_MESSAGE)
    def messageType_Information = messageType(JOptionPane.INFORMATION_MESSAGE)
    def messageType_Warning = messageType(JOptionPane.WARNING_MESSAGE)
    def messageType_Question = messageType(JOptionPane.QUESTION_MESSAGE)
    def messageType_Plain = messageType(JOptionPane.PLAIN_MESSAGE)
    def options(options: Array[Object]) = { jOptionPane.setOptions(options); jOptionPane }
    def optionType(optionType: Int) = { jOptionPane.setOptionType(optionType); jOptionPane }
    def optionType_Default = optionType(JOptionPane.DEFAULT_OPTION)
    def optionType_YesNo = optionType(JOptionPane.YES_NO_OPTION)
    def optionType_YesNoCancel = optionType(JOptionPane.YES_NO_CANCEL_OPTION)
    def optionType_OkCancel = optionType(JOptionPane.OK_CANCEL_OPTION)
    def selectionValues(values: Array[Object]) = { jOptionPane.setSelectionValues(values); jOptionPane }
    def ui(ui: OptionPaneUI) = { jOptionPane.setUI(ui); jOptionPane }
    def value(value: Object) = { jOptionPane.setValue(value); jOptionPane }
    def wantsInput(enabled: Boolean) = { jOptionPane.setWantsInput(enabled); jOptionPane }
    def wantsInput: TYPE = wantsInput(true)
    def wantsInputDisabled = wantsInput(false)
  }
}