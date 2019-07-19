package eu.eyan.util.swing

import javax.swing.JOptionPane
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import java.awt.Component
import javax.swing.Icon
import javax.swing.plaf.OptionPaneUI
import javax.swing.JRootPane
import javax.swing.UIManager
import java.util.Locale
import java.awt.Container
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JPasswordField
import javax.swing.JFrame
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import javax.swing.JDialog

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
//  def showPasswordInputDialog(parentComponent: Component) = {
//
//    def locale = if (parentComponent == null) Locale.getDefault() else parentComponent.getLocale();
//    def title = UIManager.getString("OptionPane.inputDialogTitle", locale)
//
//    val pane = new JOptionPane("Password", JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null, null)
//
//    pane.setWantsInput(true)
//    pane.setComponentOrientation((if (parentComponent == null) JOptionPane.getRootFrame() else parentComponent).getComponentOrientation())
//
//    val dialog = pane.createDialog(parentComponent, title)
//
//    val textField = dialog.getContentPane
//      .getComponent(0).asInstanceOf[JOptionPane]
//      .getComponent(0).asInstanceOf[JPanel]
//      .getComponent(0).asInstanceOf[JPanel]
//      .getComponent(1).asInstanceOf[JPanel]
//      .getComponent(1).asInstanceOf[JTextField]
//    
//    textField
//
////    pane.selectInitialValue()
////    dialog.setVisible(true)
////    dialog.dispose()
////
////    val value = pane.getInputValue()
////
////    if (value == JOptionPane.UNINITIALIZED_VALUE) null else value
//    val pwdField = new JPasswordField
////    println(JOptionPane.showMessageDialog(null, pwdField))
//    println(pwdField.getText)
//    new JFrame("Pwd").withComponent(pwdField).packAndSetVisible
//  }
}