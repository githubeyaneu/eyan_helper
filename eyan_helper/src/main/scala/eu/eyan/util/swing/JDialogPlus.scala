package eu.eyan.util.swing

import javax.swing.JDialog
import eu.eyan.util.awt.DialogPlus.DialogPlusImplicit
import javax.swing.JMenuBar
import javax.swing.WindowConstants

object JDialogPlus {
  implicit class JdialogPlusImplicit[TYPE <: JDialog](jDialog: TYPE) extends DialogPlusImplicit(jDialog) {
    def onCloseDoNothing = withDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = withDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = withDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

    def withDefaultCloseOperation(operation: Int) = { jDialog.setDefaultCloseOperation(operation); jDialog }
    def withJMenuBar(menubar: JMenuBar) = { jDialog.setJMenuBar(menubar); jDialog }
  }
}