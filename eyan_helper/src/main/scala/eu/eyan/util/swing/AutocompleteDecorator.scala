package eu.eyan.util.swing

import javax.swing.text.JTextComponent
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import eu.eyan.log.Log
import java.awt.Color
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit
import java.awt.event.ComponentEvent
import eu.eyan.util.swing.JListPlus.JListImplicit
import javax.swing.JList
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object AutocompleteDecorator {
  def decorate(component: JTextComponent): AutocompleteDecorator = {
    new AutocompleteDecorator(component)
  }
}

class AutocompleteDecorator(component: JTextComponent) {
  val NO_SELECTION = -1
  val FIRST_LINE = 0
  val DFAULT_MAX_ELEMENTS_VISIBLE = 4
  private val hints = new AutocompleteHints

  private val hintTextUI = new HintTextFieldUI

  val autocompleteList = new JListPlus[String]
    .name(JTextFieldAutocomplete.NAME_LIST)
    .onDoubleClick(selectTextFromList)
    .border(BorderFactory.createLineBorder(Color.LIGHT_GRAY))

  private val popup = new PopupWindow(autocompleteList, component)
    .onComponentMoved(setPopupLocation)

  private var noItemsFoundText = "No items found"

  private var maxElementsVisible = DFAULT_MAX_ELEMENTS_VISIBLE

  private var isListEnabled = false

  component setUI hintTextUI
  component onFocusLost hidePopup
  component onComponentResized setPopupWidth

  component.onKeyReleasedEvent(event => {
    Log.debug("addKeyReleasedListener: " + component.getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN   => selectNextInList
      case KeyEvent.VK_UP     => selectPreviousInList
      case KeyEvent.VK_ENTER  => selectTextFromList()
      case KeyEvent.VK_ESCAPE => hidePopup
      case _                  => showPopUp
    }
  })

  component.onDoubleClick { () => { if (popup.isInvisible) showPopUp } }

  component.onKeyPressedEvent(event => {
    Log.debug("addKeyPressedListener: " + component.getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN => event.consume
      case KeyEvent.VK_UP   => event.consume
      case _                => { /* do nothing */ }
    }
  })

  private def selectTextFromList: () => Unit = () => {
    if (popup.isVisible && isListEnabled && NO_SELECTION < autocompleteList.getSelectedIndex) component.setText(autocompleteList.getSelectedValue)
    hidePopup
  }

  private def hidePopup = { autocompleteList.clearSelection; popup.setInvisible }

  private def selectNextInList = {
    if (popup.isVisible) {
      if (isListEnabled) autocompleteList.setSelectedIndex(autocompleteList.getSelectedIndex + 1)
    } else showPopUp
  }

  private def selectPreviousInList = {
    if (isListEnabled) autocompleteList.setSelectedIndex(if (autocompleteList.getSelectedIndex < 0) NO_SELECTION else autocompleteList.getSelectedIndex - 1)
  }

  private def showPopUp = {
    val oldSelectedIndex = if (autocompleteList.getSelectedIndex <= NO_SELECTION) FIRST_LINE else autocompleteList.getSelectedIndex
    val oldValues = autocompleteList.getValues
    val newValues = hints.findElementsToShow(component.getText).take(maxElementsVisible)

    Log.debug("New list: " + newValues + ", old list: " + oldValues)

    if (!newValues.equals(oldValues) || popup.isInvisible) {
      if (newValues.isEmpty) {
        autocompleteList.withValues(List(noItemsFoundText))
        isListEnabled = false
      } else {
        autocompleteList.withValues(newValues)
        isListEnabled = true
        autocompleteList.setSelectedIndex(oldSelectedIndex)
      }
      autocompleteList.setEnabled(isListEnabled)

      popup.showPopup

      setPopupWidth
      setPopupLocation(null)
    }
  }

  private def refreshPopup = { if (popup.isVisible) showPopUp; this }

  private def setPopupLocation: ComponentEvent => Unit = e => popup.setLocation(component.getLocationOnScreen.x, component.getLocationOnScreen.y + component.getHeight)

  private def setPopupWidth = popup.setWidth(component.getWidth)

  def getAutocompleteValues = hints.getAutocompleteValues
  def setAutocompleteValues(values: List[String]) = { hints.setAutocompleteValues(values); refreshPopup }

  def getHintText = hintTextUI.hint
  def setHintText(hintText: String) = hintTextUI.hint = hintText

  def getNoItemsFoundText = noItemsFoundText
  def setNoItemsFoundText(text: String) = { noItemsFoundText = text; refreshPopup }

  def getMaxElementsVisible = maxElementsVisible
  def setMaxElementsVisible(max: Int) = { maxElementsVisible = max; refreshPopup }
}