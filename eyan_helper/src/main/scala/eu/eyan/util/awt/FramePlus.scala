package eu.eyan.util.awt

import eu.eyan.util.awt.WindowPlus.WindowPlusImplicit
import java.awt.Frame

object FramePlus {
  implicit class FramePlusImplicit[TYPE <: Frame](frame: TYPE) extends WindowPlusImplicit(frame) {
    def title(title: String) = { frame.setTitle(title); frame }
  }
}