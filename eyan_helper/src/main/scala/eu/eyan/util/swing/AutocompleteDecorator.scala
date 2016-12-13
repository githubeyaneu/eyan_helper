package eu.eyan.util.swing

import javax.swing.text.JTextComponent
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import eu.eyan.log.Log
import java.awt.Color
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit


object AutocompleteDecorator {
  def decorate(component: JTextComponent) = {
    new AutocompleteDecorator(component)
  }
}

class AutocompleteDecorator(component: JTextComponent) {
  /* FIXME private*/ val hints = new AutocompleteHints

  /* FIXME private*/ val hintTextUI = new HintTextFieldUI

  private val autocompleteList = new JListPlus[String]
    .withName(JTextFieldAutocomplete.NAME_LIST)
    .onDoubleClick(selectTextFromList)
    .withBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY))

  private val popup = new PopupWindow(autocompleteList, component)
    .onComponentMoved(setPopupLocation)

  /* FIXME private*/ var noItemsFoundText = "No items found"

  /* FIXME private*/ var maxElementsVisible = 4

  private var isListEnabled = false

  component.setUI(hintTextUI)
  component.onFocusLost(hidePopup)
  component.onComponentResized(setPopupWidth)

  component.addKeyReleasedListener(event => {
    Log.debug("addKeyReleasedListener: " + component.getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN   => selectNextInList
      case KeyEvent.VK_UP     => selectPreviousInList
      case KeyEvent.VK_ENTER  => selectTextFromList()
      case KeyEvent.VK_ESCAPE => hidePopup()
      case _                  => showPopUp
    }
  })

  component.addKeyPressedListener(event => {
    Log.debug("addKeyPressedListener: " + component.getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN => event.consume
      case KeyEvent.VK_UP   => event.consume
      case _                => { /*do nothing*/ }
    }
  })
  
  
  private def selectTextFromList: () => Unit = () => {
    if (popup.isVisible && isListEnabled && -1 < autocompleteList.getSelectedIndex) component.setText(autocompleteList.getSelectedValue)
    hidePopup()
  }

  private def hidePopup: () => Unit = () => {
    autocompleteList.clearSelection
    popup.setInvisible
  }

  private def selectNextInList = {
    if (popup.isVisible) {
      if (isListEnabled) autocompleteList.setSelectedIndex(autocompleteList.getSelectedIndex + 1)
    }
    else showPopUp
  }

  private def selectPreviousInList = {
    if (isListEnabled) autocompleteList.setSelectedIndex(if (autocompleteList.getSelectedIndex < 0) -1 else autocompleteList.getSelectedIndex - 1)
  }

  private def showPopUp = {
    val oldSelectedIndex = if (autocompleteList.getSelectedIndex < 0) 0 else autocompleteList.getSelectedIndex
    val oldValues = autocompleteList.getValues
    val newValues = hints.findElementsToShow(component.getText).take(maxElementsVisible)

    Log.debug("New list: " + newValues + ", old list: " + oldValues)

    if (!newValues.equals(oldValues) || popup.isInvisible) {
      if (newValues.isEmpty) {
        autocompleteList.withValues(List(noItemsFoundText))
        isListEnabled = false
      }
      else {
        autocompleteList.withValues(newValues)
        isListEnabled = true
        autocompleteList.setSelectedIndex(oldSelectedIndex)
      }
      autocompleteList.setEnabled(isListEnabled)

      popup.showPopup

      setPopupWidth()
      setPopupLocation()
    }
  }

  /* FIXME private*/ def refreshPopup = if (popup.isVisible) showPopUp

  private def setPopupLocation: () => Unit = () => popup.setLocation(component.getLocationOnScreen.x, component.getLocationOnScreen.y + component.getHeight)

  private def setPopupWidth: () => Unit = () => popup.setWidth(component.getWidth)
}