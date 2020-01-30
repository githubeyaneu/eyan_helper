package eu.eyan.util.swing

import java.awt.{Color, Component, Desktop}
import java.io.File
import java.net.URI

import com.jgoodies.forms.FormsSetup
import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.{ColumnSpec, FormLayout, RowSpec}
import eu.eyan.log.Log
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.swing.JLabelPlus.JLabelImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout._
import eu.eyan.util.swing.JTextAreaPlus.JTextAreaImplicit
import eu.eyan.util.text.Text
import javax.swing.{JButton, JCheckBox, JLabel, JOptionPane, JPanel, JPasswordField, JScrollPane, JTextArea, JTextField, SwingConstants}
import javax.swing.plaf.PanelUI
import javax.swing.text.JTextComponent

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

  var separatorSizeBetweenColumns = DEFAULT_SEPARATOR_SIZE
  var separatorSizeBetweenRows = DEFAULT_SEPARATOR_SIZE
  var borderSize = DEFAULT_BORDER_SIZE
  private var frameLayout = new FormLayout("", "")
  private var useSeparators = false
  private var useBorders = false
  private var actualColumn = 0
  private var actualRow = 0
  private var actualSpanColumns = 1

  reset

  def reset = {
    removeAll()
    separatorSizeBetweenColumns = DEFAULT_SEPARATOR_SIZE
    separatorSizeBetweenRows = DEFAULT_SEPARATOR_SIZE
    borderSize = DEFAULT_BORDER_SIZE
    frameLayout = new FormLayout("", "")
    useSeparators = false
    useBorders = false
    actualColumn = 0
    actualRow = 0
    actualSpanColumns = 1
    this.setLayout(frameLayout)
    this
  }

  def withSeparators = { if (checkNotStarted("withSeparators can be used only after the constructor! Do not use after adding cols/rows/components!")) useSeparators = true; this }
  private def appendColumnSeparator = frameLayout.appendColumn(ColumnSpec.decode(separatorSizeBetweenColumns))
  private def appendRowSeparator = frameLayout.appendRow(RowSpec.decode(separatorSizeBetweenRows))

  def withBorders = { if (checkNotStarted("withBorders can be used only after the constructor! Do not use after adding cols/rows/components!")) useBorders = true; this }
  private def appendColumnBorder = frameLayout.appendColumn(ColumnSpec.decode(borderSize))
  private def appendRowBorder = frameLayout.appendRow(RowSpec.decode(borderSize))

  private def checkNotStarted(msg: String) = if (0 < actualColumn || 0 < actualRow) { Log.error(msg); false } else true

  def span(columns: Int) = { actualSpanColumns = actualSpanColumns + columns; this }
  def span: JPanelWithFrameLayout = span(1)

  def notFirstRow = if (useBorders) actualRow >= 2 else actualRow >= 1
  def notFirstColumn = if (useBorders) actualColumn >= 2 else actualColumn >= 1
  def noRowYet = actualRow == 0
  def noColumnYet = actualColumn == 0

  def nextColumn = { if (useSeparators) actualColumn += 2 else actualColumn += 1; this }
  def newColumn: JPanelWithFrameLayout = newColumn()
  def newColumnFPG = newColumn("f:p:g")
  def newColumnFPGForTextArea = newColumn("f:1px:g")
  def newColumnScrollable = newColumn("f:1px:g")
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
  def newRowFPG = newRow("f:p:g")
  def newRowFPGForTextArea = newRow("f:1px:g")
  def newRowScrollable = newRow("f:1px:g")

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

  def addFluent(comp: Component) = {
    add(comp)
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
    val text = if (comp.isInstanceOf[JTextComponent]) "\"" + comp.asInstanceOf[JTextComponent].getText.lines.toList.headOption.getOrElse("") + "\""
    else if (comp.isInstanceOf[JLabel]) "\"" + comp.asInstanceOf[JLabel].getText + "\""
    else ""
    val name = if (comp.getName == null) "" else comp.getName
    f"${comp.getClass.getSimpleName}%-20s $text $name"
  }

  def addButton(text: String) = {
    val button = new JButton(text)
    add(button)
    button
  }

  def addButton(text: Text) = {
    val button = new JButton("").text(text)
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

  def addTextAreaWoScrollPane(text: String = "") = {
    val textArea = new JTextArea().appendText(text)
    add(textArea)
    textArea
  }

  def addLabel(text: String) = {
    val label = new JLabel(text)
    add(label)
    label
  }

  def addLabelFluent(text: String) = {
    addLabel(text)
    this
  }

  def addHelpLabel(text: String) = {
    val label = new JLabel("").cursor_HAND_CURSOR.onClicked(JOptionPane.showMessageDialog(null, text)).iconFromChar('?', Color.gray.brighter).tooltipText(text)
    add(label)
    label
  }

  def addLabelAsURL(text: String) = {
    val label = addLabel(text).cursor_HAND_CURSOR
    label.onMouseClicked(Desktop.getDesktop.browse(new URI(label.getText)))
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

  def addCheckBox(text: String = "", selected: Boolean = false) = {
    val cb = new JCheckBox(text, selected)
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

  def addTextFieldMulti(name: String, size: Int, values: List[String]) = {
    val tfm = new MultiFieldJTextField(name, size)
    tfm.setValues(values)
    add(tfm)
    tfm
  }

  def addAutocompleteMulti(name: String, hint: String, noItemsFoundHint: String) = {
    val mfa = new MultiFieldAutocomplete(name, hint, noItemsFoundHint)
    add(mfa)
    mfa
  }

  def addFluentInScrollPane[C <: Component](c: C) = {
    add(c.inScrollPane)
    this
  }
}