package eu.eyan.util.swing

import java.awt.Component
import java.awt.Frame

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout

import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.awt.FramePlus.FramePlusImplicit
import javax.swing.JFrame
import javax.swing.JMenuBar
import java.awt.Window
import javax.swing.WindowConstants
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit

object JFramePlus {
  def apply(title: String, component: Component) = new JFramePlus(title, component)

  implicit class JFramePlusImplicit[TYPE <: JFrame](jFrame: TYPE) extends FramePlusImplicit(jFrame) {
    def onCloseDoNothing = withDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = withDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = withDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    def onCloseExit = withDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    def withDefaultCloseOperation(operation: Int) = { jFrame.setDefaultCloseOperation(operation); jFrame }
    def withJMenuBar(menubar: JMenuBar) = { jFrame.setJMenuBar(menubar); jFrame }
  }
}

//TODO delete and use implicit instead
class JFramePlus(title_ : String, component: Component) extends JFrame {
  this.title(title_)
  this.onCloseExit
  setLayout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
  add(component, CC.xy(1, 1))
}