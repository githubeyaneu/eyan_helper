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
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.plaf.PanelUI
import eu.eyan.util.swing.JTextAreaPlus.JTextAreaImplicit
import javax.swing.JTextArea
import javax.swing.text.JTextComponent
import javax.swing.JPasswordField
import java.awt.Desktop
import java.net.URI
import javax.swing.JCheckBox
import java.io.File
import javax.swing.JOptionPane
import eu.eyan.util.swing.JLabelPlus.JLabelImplicit
import java.awt.Color

object JPanelPlus {
  implicit class JPanelImplicit[TYPE <: JPanel](jPanel: JPanel) extends JComponentImplicit(jPanel) {
    def ui(ui: PanelUI) = { jPanel.setUI(ui); jPanel }
  }
}

object JPanelWithFrameLayout {
  private var counterForLogs = 0
  def counterForLog = { counterForLogs += 1; counterForLogs }
  val PREF = "p"
  val DEFAULT_SEPARATOR_SIZE = "3dlu"
  val DEFAULT_BORDER_SIZE = "6dlu"

  def apply(firstColumnSpec: String = PREF, firstRowSpec: String = PREF) = new JPanelWithFrameLayout().newColumn(firstColumnSpec).newRow(firstRowSpec)
}

//TODO Rename to JPanelPlus...
class JPanelWithFrameLayout() extends JPanel {
  private val counterForLog = JPanelWithFrameLayout.counterForLog
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

  // TODO make it generic
  override def add(comp: Component) = {
    if (noRowYet) newRow
    if (noColumnYet) newColumn

    val width = if (useSeparators) actualSpanColumns * 2 - 1 else actualSpanColumns

    val name = if (getName != null) getName else f"($counterForLog)"
    val cc = f"x$actualColumn%-2s y$actualRow%-2s w$width%-2s"
    Log.debug(f"$name%-4s $cc ${debug(comp)}")
    this.add(comp, CC.xyw(actualColumn, actualRow, width))
    actualSpanColumns = 1
    comp
  }

  def debug(comp: Component) = {
    val text = if (comp.isInstanceOf[JTextComponent]) "\"" + comp.asInstanceOf[JTextComponent].getText.lines.toList.lift(0).getOrElse("") + "\""
    else if (comp.isInstanceOf[JLabel]) "\"" + comp.asInstanceOf[JLabel].getText + "\""
    else ""
    val name = if (comp.getName == null) "" else comp.getName
    f"${comp.getClass.getSimpleName}%-20s $text $name"
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

  def addPasswordField(text: String, size: Int = TEXTFIELD_DEFAULT_SIZE) = {
    val tf = new JPasswordField(text, size)
    add(tf)
    tf
  }

  def addTextArea(text: String = "") = {
    val textArea = new JTextArea().appendText(text)
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

  def addHelpLabel(text: String) = {
    val label = new JLabel("").cursor_HAND_CURSOR.onClicked(JOptionPane.showMessageDialog(null, text)).iconFromChar('?', Color.gray.brighter).tooltipText(text)
    add(label)
    label
  }

  def addLabelAsURL(text: String) = {
    val label = addLabel(text).cursor_HAND_CURSOR
    label.onMouseClicked(Desktop.getDesktop.browse((new URI(label.getText))))
    label
  }

  def addLabelAsFile(text: String) = {
    val label = addLabel(text)
    def file = new File(label.getText)
    label.onPropertyChange({ if (file.exists()) label.cursor_HAND_CURSOR else label.cursor_DEFAULT_CURSOR })
    label.onMouseClicked(if (file.exists()) Desktop.getDesktop.open(new File(label.getText)))
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

  def addSeparatorRow(title: String, span: Int) = newRow("5px").newRow.span(span).addSeparatorWithTitle(title)

  def addCheckBox(text: String = "") = {
    val cb = new JCheckBox(text)
    add(cb)
    cb
  }

  def addProgressBar(min: Int = 0, max: Int = 100, format: String = "%d%%") = {
    val progressBar = new JProgressBarPlus(min, max, format)
    progressBar.setValue(-1)
    progressBar.setStringPainted(true)
    progressBar.setString("...")
    add(progressBar)
    progressBar
  }
}