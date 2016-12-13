package eu.eyan.util.swing

import java.awt.Color
import java.awt.Graphics
import java.awt.Window.Type
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyEvent
import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout
import eu.eyan.log.Log
import javax.swing.BorderFactory
import javax.swing.JWindow
import javax.swing.SwingUtilities
import javax.swing.plaf.basic.BasicTextFieldUI
import java.awt.Window
import javax.swing.JTextField

object JTextFieldAutocomplete {
  val NAME_LIST = "_autocompleteList"
}
/**
 * Autocomplete function to a TextField based on strings.
 * It also supports a hint text function that is shown if no text is in the textfield
 */
class JTextFieldAutocomplete extends JTextField(10) {
  val autocomplete = AutocompleteDecorator.decorate(this)

  def setHintText(hintText: String) = { autocomplete.hintTextUI.hint = hintText; repaint(); this }
  def getHintText = autocomplete.hintTextUI.hint

  def setValues(values: List[String]) = {
    Log.debug(values.toList.mkString("\",\""))
    autocomplete.hints.setAutocompleteValues(values)
    autocomplete.refreshPopup
    this
  }

  def setMaxElementsVisible(maxElementsVisible: Int) = {
    autocomplete.maxElementsVisible = maxElementsVisible
    autocomplete.refreshPopup
    this
  }

  def getNoItemsFoundText = autocomplete.noItemsFoundText
  def setNoItemsFoundText(noItemsFoundText: String) = {
    autocomplete.noItemsFoundText = noItemsFoundText
    autocomplete.refreshPopup
    this
  }
}