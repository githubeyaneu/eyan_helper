package eu.eyan.util.swing

import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.AbstractButton
import eu.eyan.util.awt.AwtHelper.onActionPerformed
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit
import eu.eyan.util.text.Texts
import eu.eyan.util.text.Text

object JButtonPlus {
  implicit class JButtonImplicit[TYPE <: JButton](jButton: TYPE) extends AbstractButtonImplicit(jButton) {
    //removeNotify()
    //setDefaultCapable(boolean)
    //updateUI()    
  }
}