package eu.eyan.util.swing

import eu.eyan.util.awt.MultiField
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit
import javax.swing.JTextField

class MultiFieldJTextField(columnName: String, columns:Int = 0) extends MultiField[String, JTextField](columnName) {

  protected def createEditor(fieldEdited: JTextField => Unit) = {
    val editor = new JTextField(columns)
    editor.onKeyReleased(fieldEdited(editor))
    editor
  }

  protected def getValue(editor: JTextField) = {
    val text = editor.getText.trim
    if (text.isEmpty) None else Some(text)
  }

  protected def setValueInEditor(editor: JTextField)(value: String) = editor.setText(value)
  
  protected def valueToString(value: String): String = value
  
  protected def stringToValue(string: String):String = string
}