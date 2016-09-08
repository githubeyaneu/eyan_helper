package eu.eyan.testutil.swing.fixture

import java.awt.Dimension
import java.awt.Point
import org.fest.swing.core.KeyPressInfo
import org.fest.swing.core.MouseButton
import org.fest.swing.core.MouseClickInfo
import org.fest.swing.driver.FrameDriver
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.fixture.JPopupMenuFixture
import org.fest.swing.timing.Timeout
import javax.swing.JWindow
import org.fest.swing.fixture.JListFixture
import java.awt.Component
import javax.swing.JWindow
import javax.swing.JList
import org.fest.swing.fixture.WindowFixture
import org.fest.assertions.Assertions

class JWindowFixture(frame: WindowFixture[_], name: String) /*extends WindowFixture[JWindow](frame.robot.finder().findByType(classOf[JWindow]))*/ {

  val target = frame.robot.finder().findByName(name, classOf[JWindow])
  val driver = new FrameDriver(frame.robot)
  val robot = frame.robot

  def findByName[C <: Component](name: String, t: Class[C]): C = robot.finder().findByName(target, name, t, robot.settings().componentLookupScope().requireShowing())

  def list(name: String): JListFixture = new JListFixture(robot, findByName(name, classOf[JList[_]]))

  def focus(): JWindowFixture = {
    driver.focus(target)
    this
  }

  def requireFocused(): JWindowFixture = {
    driver.requireFocused(target)
    this
  }

  def pressAndReleaseKeys(keyCodes: Int*): JWindowFixture = {
    driver.pressAndReleaseKeys(target, keyCodes: _*)
    this
  }

  def pressKey(keyCode: Int): JWindowFixture = {
    driver.pressKey(target, keyCode)
    this
  }

  def pressAndReleaseKey(keyPressInfo: KeyPressInfo): JWindowFixture = {
    driver.pressAndReleaseKey(target, keyPressInfo)
    this
  }

  def releaseKey(keyCode: Int): JWindowFixture = {
    driver.releaseKey(target, keyCode)
    this
  }

  def click(): JWindowFixture = {
    driver.click(target)
    this
  }

  def click(button: MouseButton): JWindowFixture = {
    driver.click(target, button)
    this
  }

  def click(mouseClickInfo: MouseClickInfo): JWindowFixture = {
    driver.click(target, mouseClickInfo)
    this
  }

  def doubleClick(): JWindowFixture = {
    driver.doubleClick(target)
    this
  }

  def rightClick(): JWindowFixture = {
    driver.rightClick(target)
    this
  }

  def requireDisabled(): JWindowFixture = {
    driver.requireDisabled(target)
    this
  }

  def requireEnabled(): JWindowFixture = {
    driver.requireEnabled(target)
    this
  }

  def requireEnabled(timeout: Timeout): JWindowFixture = {
    driver.requireEnabled(target, timeout)
    this
  }

  def requireNotVisible(): JWindowFixture = {
    driver.requireNotVisible(target)
    this
  }

  def requireVisible(): JWindowFixture = {
    driver.requireVisible(target)
    this
  }

  def close(): Unit = {
    driver.close(target)
  }

  def resizeWidthTo(width: Int): JWindowFixture = {
    driver.resizeWidthTo(target, width)
    this
  }

  def resizeHeightTo(height: Int): JWindowFixture = {
    driver.resizeHeightTo(target, height)
    this
  }

  def resizeTo(size: Dimension): JWindowFixture = {
    driver.resizeTo(target, size)
    this
  }

  def requireSize(size: Dimension): JWindowFixture = {
    driver.requireSize(target, size)
    this
  }

  def moveTo(p: Point): JWindowFixture = {
    driver.moveTo(target, p)
    this
  }

  def moveToFront(): JWindowFixture = {
    driver.moveToFront(target)
    this
  }

  def moveToBack(): JWindowFixture = {
    driver.moveToBack(target)
    this
  }

  def showPopupMenu(): JPopupMenuFixture = {
    return new JPopupMenuFixture(robot, driver.invokePopupMenu(target))
    return null;
  }

  def showPopupMenuAt(p: Point): JPopupMenuFixture = {
    return new JPopupMenuFixture(robot, driver.invokePopupMenu(target, p))
    return null;
  }

  protected def show(): JWindowFixture = {
    driver.show(target)
    this
  }

  protected def show(size: Dimension): JWindowFixture = {
    driver.show(target, size)
    this
  }

  def requireLocation(expectedX: Int, expectedY: Int) = {
    Assertions.assertThat(target.getLocationOnScreen.x).isEqualTo(expectedX)
    Assertions.assertThat(target.getLocationOnScreen.y).isEqualTo(expectedY)
    this
  }

  def requireWidth(expectedWidth: Int) = {
    Assertions.assertThat(target.getWidth).isEqualTo(expectedWidth)
    this
  }
  def requireHeight(expectedHeight: Int) = {
    Assertions.assertThat(target.getHeight).isEqualTo(expectedHeight)
    this
  }
}