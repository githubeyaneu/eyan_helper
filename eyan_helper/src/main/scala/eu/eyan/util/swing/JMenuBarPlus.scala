package eu.eyan.util.swing

import javax.swing.JMenuBar
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.JMenu
import java.awt.Insets
import java.awt.Component
import javax.swing.SingleSelectionModel
import javax.swing.plaf.MenuBarUI
import javax.swing.JMenuItem
import eu.eyan.util.text.Text

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

    def menus = (for (i <- 0 until jMenuBar.getMenuCount) yield jMenuBar.getMenu(i)).toList

    def getOrCreateMenu(text: String) = {
      def menuText(menu: JMenu) = menu.getText == text
      if (menus.exists(menuText)) menus.filter(menuText)(0)
      else { val menu = new JMenu(text); jMenuBar.add(menu); menu }
    }

    // FIXME: ??? is it possible that the text changes between?
    def getOrCreateMenu(text: Text) = {
      val menuString = text.get
      def menuText(menu: JMenu) = menu.getText == menuString
      if (menus.exists(menuText)) menus.filter(menuText)(0)
      else {
        //TODO JMenuPlus
        val menu = new JMenu(menuString)
        menu.setName(menuString)
        text.subscribe(menu.setText(_))
        jMenuBar.add(menu)
        menu
      }
    }
  }
}