package eu.eyan.util.swing

import java.awt.Component

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout

import eu.eyan.util.awt.FramePlus.FramePlusImplicit
import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.WindowConstants

object JFramePlus {
  implicit class JFramePlusImplicit[TYPE <: JFrame](jFrame: TYPE) extends FramePlusImplicit(jFrame) {
    def onCloseDoNothing = withDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = withDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = withDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    def onCloseExit = withDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    def withDefaultCloseOperation(operation: Int) = { jFrame.setDefaultCloseOperation(operation); jFrame }
    def withJMenuBar(menubar: JMenuBar) = { jFrame.setJMenuBar(menubar); jFrame }
    def withComponent(component: Component) = {
      jFrame.setLayout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
      jFrame.add(component, CC.xy(1, 1))
      jFrame
    }
  }
}