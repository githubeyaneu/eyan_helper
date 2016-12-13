package eu.eyan.util.swing

import java.awt.Component
import java.awt.event.ActionEvent

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.ColumnSpec
import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.layout.RowSpec

import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.awt.AwtHelper.newActionListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField

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

  def newColumn:JPanelWithFrameLayout = newColumn()
  
  def newColumn(spec: String = "p") = {
    if (column != 0) newColumnSeparator()
    frameLayout.appendColumn(ColumnSpec.decode(spec))
    column += 1
    this
  }
  
  def nextColumn = column += 2

  def newRow:JPanelWithFrameLayout = newRow()
  
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
    column=1
    this
  }

  def addButton(text: String, action: ActionEvent => Unit = null) = {
    val button = new JButtonPlus(text)
    this.add(button, CC.xy(column, row))
    if (action != null) button.addActionListener(newActionListener(action))
    button
  }

  def addTextField(text: String, size: Int = 15) = {
    val tf = new JTextField(text, size)
    this.add(tf, CC.xy(column, row))
    tf
  }

  def addTextArea(text:String = "", documentAction: () => Unit = null) = {
    val textArea = new JTextAreaPlus().appendText(text)
    val scrollPane = new JScrollPane(textArea)
    val containerPanel = JPanelWithFrameLayout("f:1px:g", "f:1px:g").add(scrollPane)
    this.add(containerPanel, CC.xy(column, row))
    if(documentAction!=null) textArea.getDocument.addDocumentListener(AwtHelper.docListener(documentAction))
    textArea
  }

  def addLabel(text: String) = {
    val label = new JLabel(text)
    this.add(label, CC.xy(column, row))
    label
  }

  override def add(comp: Component) = add(comp,1)
  
  override def add(comp: Component, width:Int) = {
    if (column == 0) newColumn()
    this.add(comp, CC.xyw(column, row, width*2-1))
    comp
  }

  def addPanelWithFormLayout(firstRowSpec: String = "p") = {
    val panel = new JPanelWithFrameLayout(firstRowSpec)
    this.add(panel, CC.xy(column, row))
    panel
  }
}