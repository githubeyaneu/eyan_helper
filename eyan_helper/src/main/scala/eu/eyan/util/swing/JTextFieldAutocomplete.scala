package eu.eyan.util.swing

import eu.eyan.log.Log
import javax.swing.JTextField

object JTextFieldAutocomplete {
  val NAME_LIST = "_autocompleteList"
  val TEXTFIELD_DEFAULT_SIZE = 15
}
/**
 * Autocomplete function to a TextField based on strings.
 * It also supports a hint text function that is shown if no text is in the textfield
 */
class JTextFieldAutocomplete() extends JTextField(JTextFieldAutocomplete.TEXTFIELD_DEFAULT_SIZE) {
  val autocomplete = AutocompleteDecorator.decorate(this)

  def getAutocompleteList = autocomplete.getAutocompleteValues
  def setAutocompleteList(autocompleteList: List[String]) = {
    Log.debug(autocompleteList.mkString("\",\""))
    autocomplete.setAutocompleteValues(autocompleteList)
    this
  }

  def getMaxElementsVisible = autocomplete.getMaxElementsVisible
  def setMaxElementsVisible(maxElementsVisible: Int) = { autocomplete.setMaxElementsVisible(maxElementsVisible); this }

  def getNoItemsFoundText = autocomplete.getNoItemsFoundText
  def setNoItemsFoundText(noItemsFoundText: String) = { autocomplete.setNoItemsFoundText(noItemsFoundText); this }

  def setHintText(hintText: String) = { autocomplete.setHintText(hintText); repaint(); this }
  def getHintText = autocomplete.getHintText
}