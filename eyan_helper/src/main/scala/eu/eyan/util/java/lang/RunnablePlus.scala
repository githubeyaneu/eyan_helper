package eu.eyan.util.java.lang

import eu.eyan.util.awt.AwtHelper

object RunnablePlus {
  //TODO: refactor :)
  def runnable(action: => Unit) = AwtHelper.runnable(action)
}