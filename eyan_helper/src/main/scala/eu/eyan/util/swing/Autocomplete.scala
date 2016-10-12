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

/**
 * Autocomplete function to a TextField based on strings.
 * It also supports a hint text function that is shown if no text is in the textfield
 *
 */
class Autocomplete extends JTextFieldPlus(10) {

  private val hints = new AutocompleteHints
  private var isPopupVisible = false
  private lazy val autocompleteOwnerWindow = SwingUtilities.getWindowAncestor(this)

  private val hintTextUI = new HintTextFieldUI
  setUI(hintTextUI)
  def setHintText(hintText: String) = { hintTextUI.hint = hintText; repaint(); this }
  def getHintText = hintTextUI.hint

  val autocompleteList = new JListPlus[String].withName(AutocompletePopupWindow.NAME_LIST)
  autocompleteList.addDoubleClickListener(e => selectTextFromList)

  private var noItemsFoundText = "No items found"
  private var maxElementsVisible = 4

  lazy val popup = {
    val popup = new AutocompletePopupWindow(autocompleteOwnerWindow)
    autocompleteOwnerWindow.addComponentListener(
      new ComponentAdapter {
        override def componentMoved(e: ComponentEvent) = { if (isPopupVisible) setPopupLocation }
      })

    popup.add(autocompleteList, CC.xy(1, 1))

    popup
  }
  autocompleteList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY))

  addKeyReleasedListener(event => {
    Log.debug("addKeyReleasedListener: " + getName + " " + event.getKeyChar)
    event.getKeyCode match {
      case KeyEvent.VK_DOWN   => selectNextInList
      case KeyEvent.VK_UP     => selectPreviousInList
      case KeyEvent.VK_ENTER  => selectTextFromList
      case KeyEvent.VK_ESCAPE => hidePopup
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

  addFocusLostListener(e => hidePopup)

  addComponentResizedListener(e => { if (isPopupVisible) setPopupWidth })

  def selectTextFromList = {
    if (isPopupVisible && isListEnabled && -1 < autocompleteList.getSelectedIndex) setText(autocompleteList.getSelectedValue)
    hidePopup
  }

  var isListEnabled = false

  def hidePopup = {
    autocompleteList.clearSelection
    popup.setVisible(false)
    isPopupVisible = false
  }

  def selectNextInList = {
    if (isPopupVisible) {
      if (isListEnabled) autocompleteList.setSelectedIndex(autocompleteList.getSelectedIndex + 1)
    } else {
      showPopUp
    }
  }

  def selectPreviousInList = {
    if (isListEnabled)
      autocompleteList.setSelectedIndex(if (autocompleteList.getSelectedIndex < 0) -1 else autocompleteList.getSelectedIndex - 1)
  }

  def showPopUp = {
    val selectedElement = if (autocompleteList.getSelectedIndex < 0) 0 else autocompleteList.getSelectedIndex
    val valuesBefore = autocompleteList.getValues

    val list = hints.findElementsToShow(getText).take(maxElementsVisible)

    Log.debug("new list: " + list + ", old list: " + valuesBefore)

    if (!list.equals(valuesBefore) || !isPopupVisible) {
      if (list.isEmpty) {
        autocompleteList.withValues(List(noItemsFoundText))
        isListEnabled = false
      } else {
        autocompleteList.withValues(list)
        isListEnabled = true
        autocompleteList.setSelectedIndex(selectedElement)
      }
      autocompleteList.setEnabled(isListEnabled)

      popup.pack
      setPopupWidth
      setPopupLocation
      popup.setFocusableWindowState(false)
      popup.setAlwaysOnTop(true)
      popup.setVisible(true)
      isPopupVisible = true
    }
  }

  def getValues = hints.getAutocompleteValues
  def setValues(values: List[String]) = {
    Log.debug(values.toList.mkString("\",\""))
    hints.setAutocompleteValues(values)
    if (isPopupVisible) showPopUp
    this
  }

  def getMaxElementsVisible = maxElementsVisible
  def setMaxElementsVisible(maxElementsVisible: Int) = {
    this.maxElementsVisible = maxElementsVisible
    if (isPopupVisible) showPopUp
    this
  }

  def getNoItemsFoundText = noItemsFoundText
  def setNoItemsFoundText(noItemsFoundText: String) = {
    this.noItemsFoundText = noItemsFoundText
    if (isPopupVisible) showPopUp
    this
  }

  def setPopupLocation: Unit = popup.setLocation(getLocationOnScreen.x, getLocationOnScreen.y + getHeight)

  def setPopupWidth = { popup.setSize(getWidth, popup.getHeight); popup.validate }
}

object AutocompletePopupWindow {
  val NAME_LIST = "_autocompleteList"
  val NAME_POPUP = "_autocompletePopup"
}
class AutocompletePopupWindow(owner: Window) extends JWindow(owner) {
  setName(AutocompletePopupWindow.NAME_POPUP)
  setLayout(new FormLayout("f:p:g", "f:p:g"))
  setType(Type.POPUP)
}