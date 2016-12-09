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

object Autocomplete {
  val NAME_LIST = "_autocompleteList"
}
/**
 * Autocomplete function to a TextField based on strings.
 * It also supports a hint text function that is shown if no text is in the textfield
 */
class Autocomplete extends JTextFieldPlus(10) /* TODO: refactor to a decorator*/ {

  private val hints = new AutocompleteHints

  private val hintTextUI = new HintTextFieldUI

  private val autocompleteList = new JListPlus[String]
    .withName(Autocomplete.NAME_LIST)
    .onDoubleClick(selectTextFromList)
    .withBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY))

  private val popup = new PopupWindow(autocompleteList, this)
    .onComponentMoved(setPopupLocation)

  private var noItemsFoundText = "No items found"
  
  private var maxElementsVisible = 4

  private var isListEnabled = false

  setUI(hintTextUI)
  onFocusLost(hidePopup)
  onComponentResized(setPopupWidth)

  addKeyReleasedListener(event => {
    Log.debug("addKeyReleasedListener: " + getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN   => selectNextInList
      case KeyEvent.VK_UP     => selectPreviousInList
      case KeyEvent.VK_ENTER  => selectTextFromList()
      case KeyEvent.VK_ESCAPE => hidePopup()
      case _                  => showPopUp
    }
  })

  addKeyPressedListener(event => {
    Log.debug("addKeyPressedListener: " + getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN => event.consume
      case KeyEvent.VK_UP   => event.consume
      case _                => { /*do nothing*/ }
    }
  })


  def setHintText(hintText: String) = { hintTextUI.hint = hintText; repaint(); this }
  def getHintText = hintTextUI.hint

  def setValues(values: List[String]) = {
    Log.debug(values.toList.mkString("\",\""))
    hints.setAutocompleteValues(values)
    refreshPopup
    this
  }

  def setMaxElementsVisible(maxElementsVisible: Int) = {
    this.maxElementsVisible = maxElementsVisible
    refreshPopup
    this
  }

  def getNoItemsFoundText = noItemsFoundText
  def setNoItemsFoundText(noItemsFoundText: String) = {
    this.noItemsFoundText = noItemsFoundText
    refreshPopup
    this
  }

  private def selectTextFromList: () => Unit = () => {
    if (popup.isVisible && isListEnabled && -1 < autocompleteList.getSelectedIndex) setText(autocompleteList.getSelectedValue)
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
    if (isListEnabled)
      autocompleteList.setSelectedIndex(if (autocompleteList.getSelectedIndex < 0) -1 else autocompleteList.getSelectedIndex - 1)
  }

  private def showPopUp = {
    val oldSelectedIndex = if (autocompleteList.getSelectedIndex < 0) 0 else autocompleteList.getSelectedIndex
    val oldValues = autocompleteList.getValues
    val newValues = hints.findElementsToShow(getText).take(maxElementsVisible)

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

  private def refreshPopup = if (popup.isVisible) showPopUp

  private def setPopupLocation: () => Unit = () => popup.setLocation(getLocationOnScreen.x, getLocationOnScreen.y + getHeight)

  private def setPopupWidth: ()=>Unit = () => popup.setWidth(getWidth)
}