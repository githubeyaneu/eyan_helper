package eu.eyan.util.swing

import javax.swing.JToolBar
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.Action
import java.awt.Dimension
import java.awt.LayoutManager
import java.awt.Insets
import javax.swing.SwingConstants
import javax.swing.plaf.ToolBarUI

object JToolBarPlus {
  implicit class JToolBarImplicit[TYPE <: JToolBar](jToolBar: TYPE) extends JComponentImplicit(jToolBar){
    def addAction(action: Action) = {jToolBar.add(action); jToolBar}
    def addSeparator = {jToolBar.addSeparator(); jToolBar}
    def addSeparator(size: Dimension) = {jToolBar.addSeparator(size); jToolBar}
    def borderPainted(enabled: Boolean) = {jToolBar.setBorderPainted(enabled); jToolBar}
    def borderPainted:TYPE = borderPainted(true)
		def borderNotPainted:TYPE = borderPainted(false)
		def floateble(enabled: Boolean) = {jToolBar.setFloatable(enabled); jToolBar}
    def floateble:TYPE = floateble(true)
		def floatebleDisabled:TYPE = floateble(false)
		override def layout(layout: LayoutManager) = {jToolBar.setLayout(layout); jToolBar}
    def margins(insets: Insets) = {jToolBar.setMargin(insets); jToolBar}
    def orientation(orientation: Int) = {jToolBar.setOrientation(orientation); jToolBar}
    def orientation_Horizontal = orientation(SwingConstants.HORIZONTAL)
		def orientation_Vertical= orientation(SwingConstants.VERTICAL)
		def rollover(enabled: Boolean) = {jToolBar.setRollover(enabled); jToolBar}
    def rollover:TYPE = rollover(true)
		def rolloverDisabled:TYPE = rollover(false)
		def ui(ui: ToolBarUI) = {jToolBar.setUI(ui); jToolBar}
  }
}