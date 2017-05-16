package eu.eyan.testutil.swing.fixture

import java.awt.event.KeyEvent
import org.fest.assertions.Assertions
import org.fest.swing.core.KeyPressInfo
import org.fest.swing.exception.ComponentLookupException
import org.fest.swing.fixture.JTextComponentFixture
import org.fest.swing.fixture.WindowFixture
import eu.eyan.util.swing.JTextFieldAutocomplete
import eu.eyan.util.swing.PopupWindow

class AutocompleteFixture(frameFixture: WindowFixture[_], componentName: String) extends JTextComponentFixture(frameFixture.robot, componentName) {
  private def autocompleteComponent = frameFixture.textBox(componentName).target.asInstanceOf[JTextFieldAutocomplete]
  def popup = new JWindowFixture(frameFixture, PopupWindow.NAME_POPUP)
  def list = popup.list(JTextFieldAutocomplete.NAME_LIST)

  def requirePopupNotVisible = {
    try { new JWindowFixture(frameFixture, PopupWindow.NAME_POPUP).requireNotVisible() }
    catch {
      case cle: ComponentLookupException => // ok
      case t: Throwable                  => throw t
    }
  }

  def setMaxElementsVisible(maxElementsVisible: Int) = autocompleteComponent.setMaxElementsVisible(maxElementsVisible)
  def setAutocompleteList(values: String*) = autocompleteComponent.setAutocompleteList(values.toList)
  def setAutocompleteList(list: List[String]) = autocompleteComponent.setAutocompleteList(list)
  def setNoItemsFoundText(noItemsFoundText: String) = autocompleteComponent.setNoItemsFoundText(noItemsFoundText)

  def requireItems(expectedItems: String*) = Assertions.assertThat(list.contents().toList).isEqualTo(expectedItems.toList)
  def noItemsFoundText = target.asInstanceOf[JTextFieldAutocomplete].getNoItemsFoundText

  def pressUp = pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_UP))
  def pressDown = pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_DOWN))
  def pressEnter = pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_ENTER))
  def pressEscape = pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_ESCAPE))
  def pressBackspace = pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_BACK_SPACE))
  def pressTab = pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_TAB))
}