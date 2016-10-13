package eu.eyan.util.swing

import javax.swing.JPanel
import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.ColumnSpec
import com.jgoodies.forms.layout.RowSpec
import javax.swing.JTextField
import javax.swing.JLabel
import java.awt.Component
import java.awt.event.ActionEvent
import eu.eyan.util.awt.AwtHelper._
import javax.swing.JTextArea
import javax.swing.JScrollPane
import java.awt.Dimension

object JPanelWithFrameLayout {
  def apply(firstRowSpec: String = "p", firstColumnSpec: String = "p") = {
    new JPanelWithFrameLayout(firstRowSpec).newColumn(firstColumnSpec)
  }
}

class JPanelWithFrameLayout(firstRowSpec: String = "p") extends JPanel {
  val frameLayout = new FormLayout("", firstRowSpec)
  this.setLayout(frameLayout)
  var column = 0
  var row = 1

  def newColumnSeparator(spec: String = "3dlu") = {
    frameLayout.appendColumn(ColumnSpec.decode(spec))
    column += 1
  }
  def newRowSeparator(spec: String = "3dlu") = {
    frameLayout.appendRow(RowSpec.decode(spec))
    row += 1
  }

  def newColumn(spec: String = "p") = {
    if (column != 0) newColumnSeparator()
    frameLayout.appendColumn(ColumnSpec.decode(spec))
    column += 1
    this
  }

  def newRow(comp: Component = null, spec: String = "p") = {
    newRowSeparator()
    frameLayout.appendRow(RowSpec.decode(spec))
    row += 1
    if (comp != null) add(comp)
    this
  }

  def newRow(spec: String) = {
    newRowSeparator()
    frameLayout.appendRow(RowSpec.decode(spec))
    row += 1
    this
  }

  def addButton(text: String, action: ActionEvent => Unit = null) = {
    val button = new JButtonPlus(text)
    this.add(button, CC.xy(column, row))
    if (action != null) button.addActionListener(newActionListener(action))
    button
  }

  def addTextField(text: String, size: Int = 15) = {
    newColumn()
    val tf = new JTextField(text, size)
    this.add(tf, CC.xy(column, row))
    tf
  }

  def addTextArea() = {
    val textArea = new JTextAreaPlus()
    val scrollPane = new JScrollPane(textArea)
    val containerPanel = JPanelWithFrameLayout("f:1px:g", "f:1px:g").add(scrollPane)
    this.add(containerPanel, CC.xy(column, row))
    textArea
  }

  def addLabel(text: String) = {
    newColumn()
    val label = new JLabel(text)
    this.add(label, CC.xy(column, row))
    label
  }

  override def add(comp: Component) = {
    if (column == 0) newColumn()
    this.add(comp, CC.xy(column, row))
    comp
  }

  def addPanelWithFormLayout(firstRowSpec: String = "p") = {
    val panel = new JPanelWithFrameLayout(firstRowSpec)
    this.add(panel, CC.xy(column, row))
    panel
  }

}