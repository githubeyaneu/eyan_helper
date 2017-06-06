package eu.eyan.util.swing

import java.awt.Component
import java.awt.event.ActionEvent
import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.ColumnSpec
import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.layout.RowSpec
import JPanelWithFrameLayoutOld.PREF
import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.awt.AwtHelper.onActionPerformed
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import com.jgoodies.forms.internal.AbstractBuilder
import com.jgoodies.forms.FormsSetup
import javax.swing.SwingConstants
import javax.swing.JTable

object JPanelWithFrameLayoutOld {
  val PREF = "p"

  def apply(firstRowSpec: String = PREF, firstColumnSpec: String = PREF) = {
    new JPanelWithFrameLayoutOld(firstRowSpec).newColumn(firstColumnSpec)
  }
}

class JPanelWithFrameLayoutOld(firstRowSpec: String = PREF) extends JPanel {
  private val frameLayout = new FormLayout("", firstRowSpec)
  this.setLayout(frameLayout)
  private var column = 0
  private var row = 1
  private var spanColumns = 1
  var separatorSizeBetweenColumns = "3dlu"
  var separatorSizeBetweenRows = "3dlu"

  def newColumnSeparator(spec: String = separatorSizeBetweenColumns) = {
    frameLayout.appendColumn(ColumnSpec.decode(spec))
    column += 1
    this
  }

  def newRowSeparator(spec: String = separatorSizeBetweenRows) = {
    frameLayout.appendRow(RowSpec.decode(spec))
    row += 1
    this
  }

  def newColumn: JPanelWithFrameLayoutOld = newColumn()
  def newColumnFPG: JPanelWithFrameLayoutOld = newColumn("f:p:g")

  def newColumn(spec: String = PREF) = {
    if (column != 0) newColumnSeparator()
    frameLayout.appendColumn(ColumnSpec.decode(spec))
    column += 1
    this
  }

  def nextColumn = { column += 2; this }

  def newRow: JPanelWithFrameLayoutOld = newRow()
  def newRowFPG: JPanelWithFrameLayoutOld = newRow("f:p:g")

  def newRow(comp: Component = null, spec: String = PREF) = {
    newRowSeparator()
    frameLayout.appendRow(RowSpec.decode(spec))
    row += 1
    if (comp != null) add(comp)
    column = 1
    this
  }

  def newRow(spec: String) = {
    newRowSeparator()
    frameLayout.appendRow(RowSpec.decode(spec))
    row += 1
    column = 1
    this
  }

  def addButton(text: String, action: ActionEvent => Unit = null) = {
    val button = new JButtonPlus(text)
    add(button)
    if (action != null) button.addActionListener(onActionPerformed(action))
    button
  }

  val TEXTFIELD_DEFAULT_SIZE = 15
  def addTextField(text: String, size: Int = TEXTFIELD_DEFAULT_SIZE) = {
    val tf = new JTextField(text, size)
    add(tf)
    tf
  }

  def addTextArea(text: String = "", documentAction: () => Unit = null) = {
    val textArea = new JTextAreaPlus().appendText(text)
    val scrollPane = new JScrollPane(textArea)
    val containerPanel = JPanelWithFrameLayoutOld("f:1px:g", "f:1px:g").add(scrollPane)
    add(containerPanel)
    if (documentAction != null) textArea.getDocument.addDocumentListener(SwingPlus.docListener(documentAction))
    textArea
  }

  def addLabel(text: String) = {
    val label = new JLabel(text)
    add(label)
    label
  }

  def addList[LIST_TYPE]() = {
    val list = new JListPlus[LIST_TYPE]
    add(list)
    list
  }

  def addTable[TYPE]() = {
    val table = new JTablePlus[TYPE]
    add(new JScrollPane(table))
    table
  }

  /**
   * Changes the functionality of the int parameter, this here is not the position as in the super but the span width for the framelayout
   */
  override def add(comp: Component) = {
    if (column == 0) newColumn()
    this.add(comp, CC.xyw(column, row, spanColumns * 2 - 1))
    spanColumns = 1
    comp
  }

  def addPanelWithFormLayout(firstRowSpec: String = PREF) = {
    val panel = new JPanelWithFrameLayoutOld(firstRowSpec)
    add(panel)
    panel
  }

  def addSeparatorWithTitle(title: String) = {
    val titledSeparator = FormsSetup.getComponentFactoryDefault.createSeparator(title, SwingConstants.LEFT)
    add(titledSeparator)
    this
  }

  def span(columns: Int) = { spanColumns = spanColumns + columns; this }
  def span: JPanelWithFrameLayoutOld = span(1)
}