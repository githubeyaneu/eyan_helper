package eu.eyan.util.system

import java.awt.Robot
import eu.eyan.util.awt.AwtHelper
import java.awt.MouseInfo
import eu.eyan.log.Log
import eu.eyan.util.scala.Try
import eu.eyan.util.scala.TryCatchFinally

object SystemPlus {

  def keepAlive(action: => Unit) = {
    val keepAlive = new KeepAlive
    try {
      keepAlive.start
      action
    } finally keepAlive.stop
  }

  class KeepAlive {
    var running = false
    def start = if (!running) { running = true; new Thread(keepAlive, "KeepAlive").start; this }
    def stop = running = false
		def mousePosition = MouseInfo.getPointerInfo.getLocation
    var lastMousePosition = mousePosition

    def keepAlive = AwtHelper.runnable {
      val hal = new Robot
      while (running) {
        Thread.sleep(3 * 60 * 1000)
        if (mousePosition == lastMousePosition) {
          Log.debug(s"Stay alive! Mouse:$mousePosition")
          hal.mouseMove(mousePosition.x + 1, mousePosition.y + 1)
          hal.mouseMove(mousePosition.x - 1, mousePosition.y - 1)
          hal.mouseMove(mousePosition.x, mousePosition.y)
        } else Log.debug(s"Stay alive  not needed, mouse moved.")
        lastMousePosition = mousePosition
      }
    }
  }
}