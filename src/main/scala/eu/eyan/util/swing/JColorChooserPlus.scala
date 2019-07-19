package eu.eyan.util.swing

import javax.swing.JColorChooser
import javax.swing.JComponent
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.colorchooser.ColorSelectionModel
import javax.swing.colorchooser.AbstractColorChooserPanel
import javax.swing.plaf.ColorChooserUI
import java.awt.Color

object JColorChooserPlus {
  implicit class JColorChooserImplicit[TYPE <: JColorChooser](jColorChooser: TYPE) extends JComponentImplicit(jColorChooser) {
    def chooserPanel(panel: AbstractColorChooserPanel) = { jColorChooser.addChooserPanel(panel); jColorChooser }
    def chooserPanels(panels: Array[AbstractColorChooserPanel]) = { jColorChooser.setChooserPanels(panels); jColorChooser }
    def color(color: Int) = { jColorChooser.setColor(color); jColorChooser }
    def color(r: Int, g: Int, b: Int) = { jColorChooser.setColor(r, g, b); jColorChooser }
    def color(color: Color) = { jColorChooser.setColor(color); jColorChooser }
    def dragEnabled(enabled: Boolean) = { jColorChooser.setDragEnabled(enabled); jColorChooser }
    def dragEnabled: TYPE = dragEnabled(true)
    def dragDisabled: TYPE = dragEnabled(false)
    def previewPanel(preview: JComponent) = { jColorChooser.setPreviewPanel(preview); jColorChooser }
    def selectionModel(model: ColorSelectionModel) = { jColorChooser.setSelectionModel(model); jColorChooser }
    def ui(ui: ColorChooserUI) = { jColorChooser.setUI(ui); jColorChooser }
  }
}