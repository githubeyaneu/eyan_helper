package eu.eyan.util.swing

import java.awt.Color
import java.awt.ComponentOrientation
import java.awt.Insets
import java.awt.Point
import java.awt.event.InputMethodEvent
import java.io.Writer

import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.DropMode
import javax.swing.event.CaretEvent
import javax.swing.plaf.TextUI
import javax.swing.text.Caret
import javax.swing.text.Document
import javax.swing.text.Highlighter
import javax.swing.text.JTextComponent
import javax.swing.text.Keymap
import javax.swing.text.NavigationFilter
import javax.swing.text.DefaultCaret
import javax.swing.event.DocumentEvent
import eu.eyan.util.registry.RegistryPlus
import eu.eyan.log.Log
import javax.swing.SwingUtilities
import javax.swing.JFrame
import eu.eyan.util.awt.remember.RememberInRegistry
import rx.lang.scala.Observer
import javax.swing.JPasswordField
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit
import eu.eyan.util.string.StringPlus.StringPlusImplicit

object JPasswordFieldPlus {
  implicit class JPasswordFieldImplicit[TYPE <: JPasswordField](jPasswordField: TYPE) extends JTextFieldPlusImplicit(jPasswordField) {
    def getPasswordAsString = String.valueOf(jPasswordField.getPassword)

    protected override def rememberValueGet = getPasswordAsString.encrypt("JPasswordFieldPlus1009")
    protected override def rememberValueSet(value: String) = jPasswordField.setText(value.decrypt("JPasswordFieldPlus1009"))
    //    54 -> T->"a"
    //    65 -> e ->""
    
  }
}