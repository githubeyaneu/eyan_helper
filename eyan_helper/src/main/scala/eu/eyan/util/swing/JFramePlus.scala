package eu.eyan.util.swing

import java.awt.Component

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout

import eu.eyan.util.awt.FramePlus.FramePlusImplicit
import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.WindowConstants
import java.awt.LayoutManager
import javax.swing.TransferHandler
import java.awt.Container
import javax.swing.JLayeredPane
import java.awt.Image

object JFramePlus {
  implicit class JFramePlusImplicit[TYPE <: JFrame](jFrame: TYPE) extends FramePlusImplicit(jFrame) {
    def onCloseDoNothing = defaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = defaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = defaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    def onCloseExit = defaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    def defaultCloseOperation(operation: Int) = { jFrame.setDefaultCloseOperation(operation); jFrame }
    def jMenuBar(menubar: JMenuBar) = { jFrame.setJMenuBar(menubar); jFrame }
    def component(component: Component) = {
      jFrame.setLayout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
      jFrame.add(component, CC.xy(1, 1))
      jFrame
    }

    def contentPane(contentPane: Container) = { jFrame.setContentPane(contentPane); jFrame }
    def glassPane(glassPane: Component) = { jFrame.setGlassPane(glassPane); jFrame }
    def layeredPane(layeredPane: JLayeredPane) = { jFrame.setLayeredPane(layeredPane); jFrame }
    override def layout(manager: LayoutManager) = { jFrame.setLayout(manager); jFrame }
    def transferHandler(newHandler: TransferHandler) = { jFrame.setTransferHandler(newHandler); jFrame }
    override def iconImage(image: Image) = { jFrame.setIconImage(image); jFrame }

    
    def withComponent(c: Component) = component(c)
  }
}