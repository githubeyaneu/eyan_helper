package eu.eyan.util.swing

import javax.swing.JToolBar
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.Action
import java.awt.Dimension
import java.awt.LayoutManager
import java.awt.Insets
import javax.swing.SwingConstants
import javax.swing.plaf.ToolBarUI
import javax.swing.JButton
import javax.swing.ImageIcon
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import javax.swing.JLabel
import javax.swing.JTextField
import eu.eyan.util.text.Text
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.swing.JLabelPlus.JLabelImplicit
import java.awt.Component
import eu.eyan.util.text.TextsButton

object JToolBarPlus {
  implicit class JToolBarImplicit[TYPE <: JToolBar](jToolBar: TYPE) extends JComponentImplicit(jToolBar) {
    def addAction(action: Action) = { jToolBar.add(action); jToolBar }
    def addSeparator = { jToolBar.addSeparator(); jToolBar }
    def addSeparator(size: Dimension) = { jToolBar.addSeparator(size); jToolBar }
    def borderPainted(enabled: Boolean) = { jToolBar.setBorderPainted(enabled); jToolBar }
    def borderPainted: TYPE = borderPainted(true)
    def borderNotPainted: TYPE = borderPainted(false)
    def floateble(enabled: Boolean) = { jToolBar.setFloatable(enabled); jToolBar }
    def floateble: TYPE = floateble(true)
    def floatebleDisabled: TYPE = floateble(false)
    override def layout(layout: LayoutManager) = { jToolBar.setLayout(layout); jToolBar }
    def margins(insets: Insets) = { jToolBar.setMargin(insets); jToolBar }
    def orientation(orientation: Int) = { jToolBar.setOrientation(orientation); jToolBar }
    def orientation_Horizontal = orientation(SwingConstants.HORIZONTAL)
    def orientation_Vertical = orientation(SwingConstants.VERTICAL)
    def rollover(enabled: Boolean) = { jToolBar.setRollover(enabled); jToolBar }
    def rollover: TYPE = rollover(true)
    def rolloverDisabled: TYPE = rollover(false)
    def ui(ui: ToolBarUI) = { jToolBar.setUI(ui); jToolBar }

    def addFluent(c:Component) = {jToolBar.add(c); jToolBar}
    
    def addButton(texts: TextsButton) = {
      val button = new JButton().texts(texts)
      jToolBar.add(button)
      button
    }

    def addLabel(text: Text) = {
      val label = new JLabel()
      label.text(text)
      jToolBar.add(label)
      label
    }

    def addTextField(size: Int, text: String, name: String) = {//TODO refact to fluent
      val comp = new JTextField(size)
      comp.setText(text)
      comp.setName(name)
      jToolBar.add(comp)
      comp
    }
  }
}