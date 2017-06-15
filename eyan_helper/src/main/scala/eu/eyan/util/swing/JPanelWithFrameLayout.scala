package eu.eyan.util.swing

import java.awt.Component
import java.awt.event.ActionEvent
import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.ColumnSpec
import com.jgoodies.forms.layout.FormLayout
import com.jgoodies.forms.layout.RowSpec
import JPanelWithFrameLayout._
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
import eu.eyan.log.Log
import eu.eyan.util.swing.JComponentPlus2.JComponentImplicit2
import javax.swing.plaf.PanelUI

object JPanelPlus {
  implicit class JPanelImplicit[TYPE <: JPanel](jPanel: JPanel) extends JComponentImplicit2(jPanel) {
    def ui(ui: PanelUI) = { jPanel.setUI(ui); jPanel }
  }
}

object JPanelWithFrameLayout {
  val PREF = "p"
  val DEFAULT_SEPARATOR_SIZE = "3dlu"
  val DEFAULT_BORDER_SIZE = "6dlu"

  def apply(firstColumnSpec: String = PREF, firstRowSpec: String = PREF) = new JPanelWithFrameLayout().newColumn(firstColumnSpec).newRow(firstRowSpec)
}

//TODO Rename to JPanelPlus...
class JPanelWithFrameLayout() extends JPanel {
  private val frameLayout = new FormLayout("", "")
  this.setLayout(frameLayout)

  var separatorSizeBetweenColumns = DEFAULT_SEPARATOR_SIZE
  var separatorSizeBetweenRows = DEFAULT_SEPARATOR_SIZE
  def withSeparators = { if (checkNotStarted("withSeparators can be used only after the constructor! Do not use after adding cols/rows/components!")) useSeparators = true; this }
  private var useSeparators = false
  private def appendColumnSeparator = frameLayout.appendColumn(ColumnSpec.decode(separatorSizeBetweenColumns))
  private def appendRowSeparator = frameLayout.appendRow(RowSpec.decode(separatorSizeBetweenRows))

  var borderSize = DEFAULT_BORDER_SIZE
  def withBorders = { if (checkNotStarted("withBorders can be used only after the constructor! Do not use after adding cols/rows/components!")) useBorders = true; this }
  private var useBorders = false
  private def appendColumnBorder = frameLayout.appendColumn(ColumnSpec.decode(borderSize))
  private def appendRowBorder = frameLayout.appendRow(RowSpec.decode(borderSize))

  private def checkNotStarted(msg: String) = if (0 < actualColumn || 0 < actualRow) { Log.error(msg); false } else true

  private var actualColumn = 0
  private var actualRow = 0

  def span(columns: Int) = { actualSpanColumns = actualSpanColumns + columns; this }
  def span: JPanelWithFrameLayout = span(1)
  private var actualSpanColumns = 1

  def notFirstRow = if (useBorders) actualRow >= 2 else actualRow >= 1
  def notFirstColumn = if (useBorders) actualColumn >= 2 else actualColumn >= 1
  def noRowYet = actualRow == 0
  def noColumnYet = actualColumn == 0

  def nextColumn = { if (useSeparators) actualColumn += 2 else actualColumn += 1; this }
  def newColumn: JPanelWithFrameLayout = newColumn()
  def newColumnFPG: JPanelWithFrameLayout = newColumn("f:p:g")
  def newColumn(spec: String = PREF): JPanelWithFrameLayout = {
    if (useBorders && actualColumn == 0) {
      appendColumnBorder
      appendColumnBorder
      actualColumn += 1
    }

    val columnCount = frameLayout.getColumnCount
    if (useBorders) {
      frameLayout.removeColumn(columnCount)
    }

    if (useSeparators) {
      if (notFirstColumn) {
        appendColumnSeparator
        actualColumn += 1
      }
    }

    frameLayout.appendColumn(ColumnSpec.decode(spec))
    actualColumn += 1

    if (useBorders) appendColumnBorder

    this
  }

  def newRow: JPanelWithFrameLayout = newRow()
  def newRowFPG: JPanelWithFrameLayout = newRow("f:p:g")
  def newRow(spec: String = PREF): JPanelWithFrameLayout = {

    if (useBorders && actualRow == 0) {
      appendRowBorder
      appendRowBorder
      actualRow += 1
    }

    val rowCount = frameLayout.getRowCount
    if (useBorders) {
      frameLayout.removeRow(rowCount)
    }

    if (useSeparators) {
      if (notFirstRow) {
        appendRowSeparator
        actualRow += 1
      }
    }

    frameLayout.appendRow(RowSpec.decode(spec))
    actualRow += 1

    if (useBorders) appendRowBorder

    if (notFirstColumn)
      if (useBorders) actualColumn = 2
      else actualColumn = 1

    this
  }

  override def add(comp: Component) = {
    if (noRowYet) newRow
    if (noColumnYet) newColumn

    val width = if (useSeparators) actualSpanColumns * 2 - 1 else actualSpanColumns
    this.add(comp, CC.xyw(actualColumn, actualRow, width))
    actualSpanColumns = 1
    comp
  }

  def addButton(text: String) = {
    val button = new JButtonPlus(text)
    add(button)
    button
  }

  val TEXTFIELD_DEFAULT_SIZE = 15
  def addTextField(text: String, size: Int = TEXTFIELD_DEFAULT_SIZE) = {
    val tf = new JTextField(text, size)
    add(tf)
    tf
  }

  def addTextArea(text: String = "") = {
    val textArea = new JTextAreaPlus().appendText(text)
    val scrollPane = new JScrollPane(textArea)
    val containerPanel = JPanelWithFrameLayout("f:1px:g", "f:1px:g").add(scrollPane)
    add(containerPanel)
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

  def addPanelWithFormLayout() = {
    val panel = new JPanelWithFrameLayout()
    add(panel)
    panel
  }

  def addSeparatorWithTitle(title: String) = {
    val titledSeparator = FormsSetup.getComponentFactoryDefault.createSeparator(title, SwingConstants.LEFT)
    add(titledSeparator)
    this
  }
}