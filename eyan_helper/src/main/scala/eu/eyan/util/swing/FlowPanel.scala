package eu.eyan.util.swing

import javax.swing.JPanel
import java.awt.LayoutManager
import java.awt.Component

class FlowPanel(layout:LayoutManager) extends JPanel(layout){
	def withComp(comp: Component):FlowPanel = {
		this.add(comp)
		this.revalidate()
		this
	}
}