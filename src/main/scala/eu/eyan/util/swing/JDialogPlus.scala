package eu.eyan.util.swing

import javax.swing.JDialog
import eu.eyan.util.awt.DialogPlus.DialogPlusImplicit
import javax.swing.JMenuBar
import javax.swing.WindowConstants
import java.awt.Container
import java.awt.Component
import javax.swing.TransferHandler
import java.awt.LayoutManager
import javax.swing.JLayeredPane

object JDialogPlus {
  implicit class JdialogPlusImplicit[TYPE <: JDialog](jDialog: TYPE) extends DialogPlusImplicit(jDialog) {
    def onCloseDoNothing = withDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = withDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = withDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

    def withDefaultCloseOperation(operation: Int) = { jDialog.setDefaultCloseOperation(operation); jDialog }
    def withJMenuBar(menubar: JMenuBar) = { jDialog.setJMenuBar(menubar); jDialog }

    def contentPane(contentPane: Container) = { jDialog.setContentPane(contentPane); jDialog }
    def glassPane(glassPane: Component) = { jDialog.setGlassPane(glassPane); jDialog }
    def layeredPane(layeredPane: JLayeredPane) = { jDialog.setLayeredPane(layeredPane); jDialog }
    override def layout(manager: LayoutManager) = { jDialog.setLayout(manager); jDialog }
    def transferHandler(newHandler: TransferHandler) = { jDialog.setTransferHandler(newHandler); jDialog }
    
    def addFluent(c: Component) = { jDialog.add(c); jDialog }
    def addFluent(c: Component, constraints: Any) = { jDialog.add(c, constraints); jDialog }
  }
}