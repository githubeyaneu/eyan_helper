package eu.eyan.util.swing

import eu.eyan.log.Log
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit
import eu.eyan.util.swing.JListPlus.JListImplicit
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.awt.MultiField

class MultiFieldAutocomplete(columnName: String, hintText: String, noItemsFoundText: String, autocompleteList: List[String]) extends MultiField[String, JTextFieldAutocomplete](columnName) {

//  var  = List() // TODO: move to constructor. ac wo aclist makes no sense

  Log.debug(columnName)

  protected def createEditor(fieldEdited: JTextFieldAutocomplete => Unit): JTextFieldAutocomplete = {
    val editor = new JTextFieldAutocomplete().setAutocompleteList(autocompleteList).setHintText(hintText).setNoItemsFoundText(noItemsFoundText)
    def addIfNotEmpty = if (editor.getText.nonEmpty) fieldEdited(editor)
    editor.onKeyReleased(addIfNotEmpty)
    editor.autocomplete.autocompleteList.onDoubleClick(addIfNotEmpty)
    editor
  }

  protected def getValue(editor: JTextFieldAutocomplete) = if (editor.getText.isEmpty) None else Some(editor.getText)

  protected def setValueInEditor(editor: JTextFieldAutocomplete)(value: String): Unit = {
    editor.setText(value)
    Log.debug(editor.getName + " " + value)
  }
  
  protected def valueToString(value: String): String = value
  protected def stringToValue(string: String):String = string

  //def setAutoCompleteList(autocompleteList: List[String]) = { this.autocompleteList = autocompleteList; this }
}