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
  val TEXTFIELD_DEFAULT_SIZE = 15
}
/**
 * Autocomplete function to a TextField based on strings.
 * It also supports a hint text function that is shown if no text is in the textfield
 */
class JTextFieldAutocomplete() extends JTextField(JTextFieldAutocomplete.TEXTFIELD_DEFAULT_SIZE) {
  private val autocomplete = AutocompleteDecorator.decorate(this)

  def getAutocompleteList = autocomplete.getAutocompleteValues
  def setAutocompleteList(autocompleteList: List[String]) = { Log.debug(autocompleteList.mkString("\",\"")); autocomplete.setAutocompleteValues(autocompleteList); this }

  def getMaxElementsVisible = autocomplete.getMaxElementsVisible
  def setMaxElementsVisible(maxElementsVisible: Int) = { autocomplete.setMaxElementsVisible(maxElementsVisible); this }

  def getNoItemsFoundText = autocomplete.getNoItemsFoundText
  def setNoItemsFoundText(noItemsFoundText: String) = { autocomplete.setNoItemsFoundText(noItemsFoundText); this }

  def setHintText(hintText: String) = { autocomplete.setHintText(hintText); repaint(); this }
  def getHintText = autocomplete.getHintText
}