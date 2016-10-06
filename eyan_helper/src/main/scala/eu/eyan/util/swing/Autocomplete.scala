package eu.eyan.util.swing

import java.awt.Color
import java.awt.Graphics
import java.awt.Window
import java.awt.Window.Type
import java.awt.event.ComponentAdapter
import java.awt.event.KeyEvent

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout

import eu.eyan.log.Log
import javax.swing.BorderFactory
import javax.swing.JWindow
import javax.swing.SwingUtilities
import javax.swing.plaf.basic.BasicTextFieldUI
import java.awt.event.ComponentEvent
import java.awt.event.ContainerListener
import javax.swing.event.AncestorListener
import javax.swing.event.AncestorEvent
import java.awt.Container
import java.awt.event.WindowStateListener
import java.awt.event.WindowListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.text.Normalizer
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.util.concurrent.ConcurrentMap
import eu.eyan.util.awt.AwtHelper

object Autocomplete {
  val NAME_LIST = "autocompleteList"
  val NAME_POPUP = "autocompletePopup"
  val reg = "[\\p{InCombiningDiacriticalMarks}]".r

  private val normalizedautocompleteValues = new java.util.HashMap[String, String]()

  def getNormalized(text: String) = {
    def normalize(text: String) = reg.replaceAllIn(Normalizer.normalize(text, Normalizer.Form.NFD), "").replaceAll("ß", "s").toLowerCase

    if (text == null) null
    else {
      normalizedautocompleteValues.synchronized {
        if (!normalizedautocompleteValues.containsKey(text)) {
          normalizedautocompleteValues.put(text, normalize(text))
        }
        normalizedautocompleteValues.get(text)
      }
    }
  }

  def sortAlgo(searchString: String) = (s1: String, s2: String) => {
    val l1 = s1.toLowerCase
    val l2 = s2.toLowerCase
    val lsearch = searchString.toLowerCase
    val s1Starts = l1.startsWith(lsearch)
    val s2Starts = l2.startsWith(lsearch)
    val s1Contains = l1.contains(lsearch)
    val s2Contains = l2.contains(lsearch)
    val s1NormalizedStarts = getNormalized(l1).startsWith(getNormalized(lsearch))
    val s2NormalizedStarts = getNormalized(l2).startsWith(getNormalized(lsearch))

    if (s1Starts && !s2Starts) true
    else if (s2Starts && !s1Starts) false
    else if (s1NormalizedStarts && !s2NormalizedStarts) true
    else if (s2NormalizedStarts && !s1NormalizedStarts) false
    else if (s1Contains && !s2Contains) true
    else false
  }
}

class Autocomplete extends JTextFieldPlus(10) {

  private var isPopupVisible = false
  private lazy val parentFrame = SwingUtilities.getWindowAncestor(this)
  private val hintTextUI = new HintTextFieldUI
  setUI(hintTextUI)
  def setHintText(hintText: String) = { hintTextUI.hint = hintText; repaint(); this }
  def getHintText = hintTextUI.hint

  val autocompleteList = new JListPlus[String].withName(Autocomplete.NAME_LIST)
  autocompleteList.addDoubleClickListener(e => selectTextFromList)
  private var autocompleteValues = List[String]()
  private var noItemsFoundText = "No items found"
  private var maxElementsVisible = 4

  lazy val popup = {
    val popup = new JWindow(parentFrame)
    parentFrame.addComponentListener(
      new ComponentAdapter {
        override def componentMoved(e: ComponentEvent) = { if (isPopupVisible) setPopupLocation }
      })

    popup.setName(Autocomplete.NAME_POPUP)
    popup.setLayout(new FormLayout("f:p:g", "f:p:g"))
    popup.add(autocompleteList, CC.xy(1, 1))
    popup.setType(Type.POPUP)
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
    if (isPopupVisible && autocompleteList.isEnabled && -1 < autocompleteList.getSelectedIndex) setText(autocompleteList.getSelectedValue)
    hidePopup
  }

  def hidePopup = {
    autocompleteList.clearSelection
    popup.setVisible(false)
    isPopupVisible = false
  }

  def selectNextInList = {
    showPopUp
    if (autocompleteList.isEnabled) autocompleteList.setSelectedIndex(autocompleteList.getSelectedIndex + 1)
  }

  def selectPreviousInList = {
    if (autocompleteList.isEnabled)
      autocompleteList.setSelectedIndex(if (autocompleteList.getSelectedIndex < 0) -1 else autocompleteList.getSelectedIndex - 1)
  }

  def showPopUp = {
    val selectedBefore = autocompleteList.getSelectedIndex
    val valuesBefore = autocompleteList.getValues

    val list = findElementsToShow(autocompleteValues, getText).take(maxElementsVisible)

    Log.debug("new list: " + list + ", old list: " + valuesBefore)

    if (!list.equals(valuesBefore) || !isPopupVisible) {
      if (list.isEmpty) {
        autocompleteList.withValues(List(noItemsFoundText))
        autocompleteList.setEnabled(false)
      } else {
        autocompleteList.withValues(list)
        autocompleteList.setEnabled(true)
      }

      autocompleteList.setSelectedIndex(selectedBefore)
      popup.pack
      setPopupWidth
      setPopupLocation
      popup.setFocusableWindowState(false)
      popup.setAlwaysOnTop(true)
      popup.setVisible(true)
      isPopupVisible = true
    }
  }

  def normalized(text: String) = Autocomplete.getNormalized(text)

  private def findElementsToShow(autocompleteValues: List[String], searchString: String) = {
    val searchNormalized = normalized(searchString)
    autocompleteValues
      .filter("".ne(_))
      .filter(normalized(_).contains(searchNormalized))
      .sortWith(Autocomplete.sortAlgo(searchString))
  }

  def getValues = autocompleteValues
  def setValues(values: List[String]) = {
    Log.debug(values.toList.mkString("\",\""))
    autocompleteValues = values.filter { _ != null }
    if (isPopupVisible) showPopUp
    SwingUtilities.invokeLater(AwtHelper.newRunnable { () => values.foreach { normalized(_) } })
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

  def setPopupLocation: Autocomplete = {
    Log.debug("getWidth: " + getWidth + ", popup.getHeight: " + popup.getHeight)
    popup.setLocation(getLocationOnScreen.x, getLocationOnScreen.y + getHeight)
    Log.debug(popup.getLocation.toString)
    this
  }

  def setPopupWidth = {
    popup.setSize(getWidth, popup.getHeight)
    popup.validate
    this
  }

}

class HintTextFieldUI extends BasicTextFieldUI {

  var hint = ""

  override def paintSafely(g: Graphics) = {
    super.paintSafely(g)
    val comp = getComponent
    if (hint != null && comp.getText.length == 0) {
      g.setColor(comp.getForeground.brighter.brighter.brighter)
      val padding = (comp.getHeight - comp.getFont.getSize) / 2
      g.drawString(hint, 2, comp.getHeight - padding - 1)
    }
  }
}