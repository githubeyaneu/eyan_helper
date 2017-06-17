package eu.eyan.util.swing

import javax.swing.JWindow
import eu.eyan.util.awt.WindowPlus.WindowPlusImplicit
import java.awt.LayoutManager
import javax.swing.TransferHandler
import java.awt.Container
import javax.swing.JLayeredPane
import java.awt.Component

object JWindowPlus {
  implicit class JWindowPlusImplicit[TYPE <: JWindow](jWindow: TYPE) extends WindowPlusImplicit(jWindow) {
    def contentPane(container: Container) = { jWindow.setContentPane(container); jWindow }
    def glassPane(component: Component) = { jWindow.setGlassPane(component); jWindow }
    def layeredPane(pane: JLayeredPane) = { jWindow.setLayeredPane(pane); jWindow }
    override def layout(layout: LayoutManager) = { jWindow.setLayout(layout); jWindow }
    def transferHandler(th: TransferHandler) = { jWindow.setTransferHandler(th); jWindow }
  }
}