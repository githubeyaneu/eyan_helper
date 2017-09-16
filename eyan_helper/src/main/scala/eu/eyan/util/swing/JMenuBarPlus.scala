package eu.eyan.util.swing

import javax.swing.JMenuBar
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.JMenu
import java.awt.Insets
import java.awt.Component
import javax.swing.SingleSelectionModel
import javax.swing.plaf.MenuBarUI

object JMenuBarPlus {
  implicit class JMenuBarImplicit[TYPE <: JMenuBar](jMenuBar: TYPE) extends JComponentImplicit(jMenuBar) {
    def borderPainted(enabled: Boolean) = { jMenuBar.setBorderPainted(enabled); jMenuBar }
    def borderPainted: TYPE = borderPainted(true)
    def borderPaintedDisabled = borderPainted(false)
    def helpMenu(menu: JMenu) = { jMenuBar.setHelpMenu(menu); jMenuBar }
    def margin(insets: Insets) = { jMenuBar.setMargin(insets); jMenuBar }
    def selected(component: Component) = { jMenuBar.setSelected(component); jMenuBar }
    def selectionModel(model: SingleSelectionModel) = { jMenuBar.setSelectionModel(model); jMenuBar }
    def ui(ui: MenuBarUI) = { jMenuBar.setUI(ui); jMenuBar }
  }
}