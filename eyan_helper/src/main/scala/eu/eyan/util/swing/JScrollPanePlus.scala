package eu.eyan.util.swing

import javax.swing.JScrollPane
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.JViewport
import java.awt.Component
import java.awt.ComponentOrientation
import javax.swing.JScrollBar
import javax.swing.ScrollPaneConstants
import java.awt.LayoutManager
import javax.swing.plaf.ScrollPaneUI
import javax.swing.border.Border

object JScrollPanePlus {
  implicit class JScrollPaneImplicit[TYPE <: JScrollPane](jScrollPane: TYPE) extends JComponentImplicit(jScrollPane) {
    def horizontalScrollBar = { jScrollPane.createHorizontalScrollBar(); jScrollPane }
    def verticalScrollBar = { jScrollPane.createVerticalScrollBar; jScrollPane }
    def columnHeader(columnHeader: JViewport) = { jScrollPane.setColumnHeader(columnHeader); jScrollPane }
    def columnHeaderView(view: Component) = { jScrollPane.setColumnHeaderView(view); jScrollPane }
    override def componentOrientation(co: ComponentOrientation) = { jScrollPane.setComponentOrientation(co); jScrollPane }
    def componentOrientation_LeftToRight = componentOrientation(ComponentOrientation.LEFT_TO_RIGHT)
    def componentOrientation_RightToLeft = componentOrientation(ComponentOrientation.RIGHT_TO_LEFT)
    def componentOrientation_Unknown = componentOrientation(ComponentOrientation.UNKNOWN)
    def corner(key: String, corner: Component) = { jScrollPane.setCorner(key, corner); jScrollPane }
    def horizontalScrollBar(horizontalScrollBar: JScrollBar) = { jScrollPane.setHorizontalScrollBar(horizontalScrollBar); jScrollPane }
    def horizontalScrollBarPolicy(policy: Int) = { jScrollPane.setHorizontalScrollBarPolicy(policy); jScrollPane }
    def horizontalScrollBarPolicy_AsNeeded = horizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    def horizontalScrollBarPolicy_Never = horizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    def horizontalScrollBarPolicy_Always = horizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS)
    override def layout(layout: LayoutManager) = { jScrollPane.setLayout(layout); jScrollPane }
    def rowHeader(rowHeader: JViewport) = { jScrollPane.setRowHeader(rowHeader); jScrollPane }
    def setRowHeaderView(view: Component) = { jScrollPane.setRowHeaderView(view); jScrollPane }
    def ui(ui: ScrollPaneUI) = { jScrollPane.setUI(ui); jScrollPane }
    def verticalScrollBar(verticalScrollBar: JScrollBar) = { jScrollPane.setVerticalScrollBar(verticalScrollBar); jScrollPane }
    def verticalScrollBarPolicy(policy: Int) = { jScrollPane.setVerticalScrollBarPolicy(policy); jScrollPane }
    def verticalScrollBarPolicy_AsNeeded = horizontalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    def verticalScrollBarPolicy_Always = horizontalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
    def verticalScrollBarPolicy_Never = horizontalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER)
    def viewport(viewport: JViewport) = { jScrollPane.setViewport(viewport); jScrollPane }
    def viewportBorder(viewportBorder: Border) = { jScrollPane.setViewportBorder(viewportBorder); jScrollPane }
    def viewportView(view: Component) = { jScrollPane.setViewportView(view); jScrollPane }
    def wheelScrollingEnabled(enabled: Boolean) = { jScrollPane.setWheelScrollingEnabled(enabled); jScrollPane }
    def wheelScrollingEnabled: TYPE = wheelScrollingEnabled(true)
    def wheelScrollingDisabled: TYPE = wheelScrollingEnabled(false)
  }
}