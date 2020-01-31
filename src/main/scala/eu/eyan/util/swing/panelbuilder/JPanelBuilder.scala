package eu.eyan.util.swing.panelbuilder

import java.awt.{ Color, Component, Desktop }
import java.io.File
import java.net.URI

import com.jgoodies.forms.FormsSetup
import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.{ ColumnSpec, FormLayout, RowSpec }
import eu.eyan.log.Log
import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.swing.JLabelPlus.JLabelImplicit
import eu.eyan.util.swing.JTextAreaPlus.JTextAreaImplicit
import eu.eyan.util.text.Text
import javax.swing.{ JButton, JCheckBox, JLabel, JOptionPane, JPanel, JPasswordField, JScrollPane, JTextArea, JTextField, SwingConstants }
import javax.swing.plaf.PanelUI
import javax.swing.text.JTextComponent
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit
import eu.eyan.util.swing.JCheckBoxPlus.JCheckBoxImplicit
import rx.lang.scala.Observable
import eu.eyan.util.swing.JListPlus
import eu.eyan.util.swing.JTablePlus
import eu.eyan.util.swing.JProgressBarPlus
import eu.eyan.util.swing.MultiFieldJTextField
import eu.eyan.util.swing.MultiFieldAutocomplete
import eu.eyan.util.swing.SwingPlus
import javax.swing.JList
import javax.swing.JTable
import eu.eyan.util.string.StringPlus.StringPlusImplicit

object JPanelBuilder {
  val PREF = "p"
  private var counterForLogs = 0
  def counterForLog = { counterForLogs += 1; counterForLogs }
  def apply(firstColumnSpec: String = PREF, firstRowSpec: String = PREF, name: String ="") = new JPanelBuilder(name).newColumn(firstColumnSpec).newRow(firstRowSpec)
}

class JPanelBuilder private (name: String) extends JPanel with IPanelBuilder {
  setName(name)
  
  private val DEFAULT_SEPARATOR_SIZE = "3dlu"
  private val DEFAULT_BORDER_SIZE = "6dlu"
  private def DEFAULT_FORM_LAYOUT = new FormLayout("", "")
  private val DEFAULT_USE_SEPARATORS = false
  private val DEFAULT_USE_BORDERS = false
  private val counterForLog = JPanelBuilder.counterForLog

  private var separatorSizeBetweenColumns = DEFAULT_SEPARATOR_SIZE
  private var separatorSizeBetweenRows = DEFAULT_SEPARATOR_SIZE
  private var borderSize = DEFAULT_BORDER_SIZE
  private var frameLayout = DEFAULT_FORM_LAYOUT
  private var useSeparators = DEFAULT_USE_SEPARATORS
  private var useBorders = DEFAULT_USE_BORDERS
  private var actualColumn = 0
  private var actualRow = 0
  private var actualSpanColumns = 1

  reset

  private def appendColumnSeparator = frameLayout.appendColumn(ColumnSpec.decode(separatorSizeBetweenColumns))
  private def appendRowSeparator = frameLayout.appendRow(RowSpec.decode(separatorSizeBetweenRows))

  private def appendColumnBorder = frameLayout.appendColumn(ColumnSpec.decode(borderSize))
  private def appendRowBorder = frameLayout.appendRow(RowSpec.decode(borderSize))

  private def checkNotStarted(msg: String) = if (0 < actualColumn || 0 < actualRow) { Log.error(msg); false } else true

  private def notFirstRow = if (useBorders) actualRow >= 2 else actualRow >= 1
  private def notFirstColumn = if (useBorders) actualColumn >= 2 else actualColumn >= 1
  private def noRowYet = actualRow == 0
  private def noColumnYet = actualColumn == 0

  private def addComponent[C <: Component](c: C): C = { add(c); c }
  private def addComponentInScrollPane[C <: Component](c: C): C = { addInScrollPane(c); c }
  private def addComponentInResizableScrollPane[C <: Component](c: C): C = { addComponent(JPanelBuilder("f:1px:g", "f:1px:g").addInScrollPane(c)); c }

  def reset = {
    removeAll()
    separatorSizeBetweenColumns = DEFAULT_SEPARATOR_SIZE
    separatorSizeBetweenRows = DEFAULT_SEPARATOR_SIZE
    borderSize = DEFAULT_BORDER_SIZE
    frameLayout = DEFAULT_FORM_LAYOUT
    useSeparators = DEFAULT_USE_SEPARATORS
    useBorders = DEFAULT_USE_BORDERS
    actualColumn = 0
    actualRow = 0
    actualSpanColumns = 1
    this.setLayout(frameLayout)
    this
  }
  def withSeparators = { if (checkNotStarted("withSeparators can be used only after the constructor! Do not use after adding cols/rows/components!")) useSeparators = true; this }
  def withBorders = { if (checkNotStarted("withBorders can be used only after the constructor! Do not use after adding cols/rows/components!")) useBorders = true; this }

  def span(columns: Int): IPanelBuilder = { actualSpanColumns = actualSpanColumns + columns; this }
  def span: IPanelBuilder = span(1)

  def nextColumn: IPanelBuilder = { if (useSeparators) actualColumn += 2 else actualColumn += 1; this }
  def newColumn: IPanelBuilder = newColumn()
  def newColumnFPG: IPanelBuilder = newColumn("f:p:g")
  def newColumnFPGForTextArea: IPanelBuilder = newColumn("f:1px:g")
  def newColumnScrollable: IPanelBuilder = newColumn("f:1px:g")
  def newColumn(spec: String = PREF): IPanelBuilder = {
    if (useBorders && actualColumn == 0) { appendColumnBorder; appendColumnBorder; actualColumn += 1 }

    val columnCount = frameLayout.getColumnCount
    if (useBorders) frameLayout.removeColumn(columnCount)

    if (useSeparators && notFirstColumn) { appendColumnSeparator; actualColumn += 1 }

    frameLayout.appendColumn(ColumnSpec.decode(spec))
    actualColumn += 1

    if (useBorders) appendColumnBorder

    this
  }

  def newRow: IPanelBuilder = newRow()
  def newRowFPG: IPanelBuilder = newRow("f:p:g")
  def newRowFPGForTextArea: IPanelBuilder = newRow("f:1px:g")
  def newRowScrollable: IPanelBuilder = newRow("f:1px:g")

  def newRow(spec: String = PREF): IPanelBuilder = {
    if (useBorders && actualRow == 0) { appendRowBorder; appendRowBorder; actualRow += 1 }

    val rowCount = frameLayout.getRowCount

    if (useBorders) frameLayout.removeRow(rowCount)

    if (useSeparators && notFirstRow) { appendRowSeparator; actualRow += 1 }

    frameLayout.appendRow(RowSpec.decode(spec))
    actualRow += 1

    if (useBorders) appendRowBorder

    if (notFirstColumn) actualColumn = if (useBorders) 2 else 1

    this
  }

  def addFluent(comp: Component): IPanelBuilder = { add(comp); this }
  def addInScrollPane[C <: Component](c: C): IPanelBuilder = { add(c.inScrollPane); this }

  def addSeparatorWithTitle(title: String): IPanelBuilder = { add(FormsSetup.getComponentFactoryDefault.createSeparator(title, SwingConstants.LEFT)); this }
  def addSeparatorRow(title: String, span: Int): IPanelBuilder = newRow("5px").newRow.span(span).addSeparatorWithTitle(title)

  // should not be in JPanelBuilderDelegator
  def addTextField = new JPanelBuilderTextField(this, addComponent(new JTextField(TEXTFIELD_DEFAULT_SIZE)))
  def addPaaswordField = new JPanelBuilderPasswordField(this, addComponent(new JPasswordField(TEXTFIELD_DEFAULT_SIZE)))
  def addLabel = new JPanelBuilderLabel(this, addComponent(new JLabel()))
  def addCheckBox = new JPanelBuilderCheckBox(this, addComponent(new JCheckBox()))
  def addButton = new JPanelBuilderButton(this, addComponent(new JButton()))
  def addList[LIST_TYPE]() = new JPanelBuilderList[LIST_TYPE](this, addComponent(new JListPlus[LIST_TYPE]))

  def addHelpLabel(text: String) = new JPanelBuilderLabel(this, addComponent(new JLabel("").cursor_HAND_CURSOR.onClicked(JOptionPane.showMessageDialog(null, text)).iconFromChar('?', Color.gray.brighter).tooltipText(text)))
  def addLabelAsURL(text: String) = new JPanelBuilderLabel(this, addComponent(new JLabel())).text(text).cursor_HAND_CURSOR.onMouseClicked(text => Desktop.getDesktop.browse(new URI(text)))
  def addLabelAsFile(text: String) = {
    val label = new JLabel(text)
    label.onPropertyChange({ if (label.getText.asFile.exists()) label.cursor_HAND_CURSOR else label.cursor_DEFAULT_CURSOR })
    label.onMouseClicked(if (label.getText.asFile.exists()) Desktop.getDesktop.open(new File(label.getText)))
    new JPanelBuilderLabel(this, addComponent(label))
  }

  def addTextArea(text: String = "") = new JPanelBuilderTextArea(this, addComponentInResizableScrollPane(new JTextArea()))

  def addTextAreaWoScrollPane = new JPanelBuilderTextArea(this, addComponent(new JTextArea))
  def addTable[TYPE] = new JPanelBuilderTable[TYPE](this, addComponentInResizableScrollPane(new JTablePlus[TYPE])) // TODO maybe only addComponentInScrollPane?

  def addProgressBar(min: Int = 0, max: Int = 100, format: String = "%d%%") = new JPanelBuilderProgressBar(this, new JProgressBarPlus(min, max, format)).setValue(-1).setStringPainted(true).setString("...")

  def addTextFieldMulti(name: String, size: Int) = new JPanelBuilderMultiFieldJTextField(this, new MultiFieldJTextField(name, size))

  def addAutocompleteMulti(name: String, hint: String, noItemsFoundHint: String) = new JPanelBuilderMultiFieldAutocomplete(this, new MultiFieldAutocomplete(name, hint, noItemsFoundHint))

  def addPanelWithFormLayout() = new JPanelBuilderPanelBuilder(this, addComponent(new JPanelBuilder("")))
}

trait IPanelBuilder extends JPanel /*TODO hmmm... is it good idea to extend from JPanel even it is itself a JPanel? But otherwise it is referenced like panel.panel*/ {
  val TEXTFIELD_DEFAULT_SIZE = 15
  val PREF = JPanelBuilder.PREF

  def reset: IPanelBuilder
  def withSeparators: IPanelBuilder
  def withBorders: IPanelBuilder
  def span(columns: Int): IPanelBuilder
  def span: IPanelBuilder
  def nextColumn: IPanelBuilder
  def newColumn: IPanelBuilder
  def newColumnFPG: IPanelBuilder
  def newColumnFPGForTextArea: IPanelBuilder
  def newColumnScrollable: IPanelBuilder
  def newColumn(spec: String): IPanelBuilder
  def newRow: IPanelBuilder
  def newRowFPG: IPanelBuilder
  def newRowFPGForTextArea: IPanelBuilder
  def newRowScrollable: IPanelBuilder
  def newRow(spec: String): IPanelBuilder
  def addFluent(comp: Component): IPanelBuilder

  def addSeparatorWithTitle(title: String): IPanelBuilder
  def addSeparatorRow(title: String, span: Int): IPanelBuilder
  def addInScrollPane[C <: Component](c: C): IPanelBuilder

  def addTextField: JPanelBuilderTextField
  def addLabel: JPanelBuilderLabel
  def addCheckBox: JPanelBuilderCheckBox
  def addButton: JPanelBuilderButton
}

abstract class JPanelBuilderDelegator(jPanelBuilder: JPanelBuilder) extends IPanelBuilder {
  def reset: IPanelBuilder = jPanelBuilder.reset
  def withSeparators: IPanelBuilder = jPanelBuilder.withSeparators
  def withBorders: IPanelBuilder = jPanelBuilder.withBorders
  def span(columns: Int): IPanelBuilder = jPanelBuilder.span(columns)
  def span: IPanelBuilder = jPanelBuilder.span
  def nextColumn: IPanelBuilder = jPanelBuilder.nextColumn
  def newColumn: IPanelBuilder = jPanelBuilder.newColumn
  def newColumnFPG: IPanelBuilder = jPanelBuilder.newColumnFPG
  def newColumnFPGForTextArea: IPanelBuilder = jPanelBuilder.newColumnFPGForTextArea
  def newColumnScrollable: IPanelBuilder = jPanelBuilder.newColumnScrollable
  def newColumn(spec: String = PREF): IPanelBuilder = jPanelBuilder.newColumn(spec)
  def newRow: IPanelBuilder = jPanelBuilder.newRow
  def newRowFPG: IPanelBuilder = jPanelBuilder.newRowFPG
  def newRowFPGForTextArea: IPanelBuilder = jPanelBuilder.newRowFPGForTextArea
  def newRowScrollable: IPanelBuilder = jPanelBuilder.newRowScrollable
  def newRow(spec: String = PREF): IPanelBuilder = jPanelBuilder.newRow(spec)
  def addFluent(comp: Component): IPanelBuilder = jPanelBuilder.addFluent(comp)
  def addSeparatorWithTitle(title: String): IPanelBuilder = jPanelBuilder.addSeparatorWithTitle(title)
  def addSeparatorRow(title: String, span: Int): IPanelBuilder = jPanelBuilder.addSeparatorRow(title, span)
  def addInScrollPane[C <: Component](c: C): IPanelBuilder = jPanelBuilder.addInScrollPane(c)

  def addTextField = jPanelBuilder.addTextField
  def addLabel = jPanelBuilder.addLabel
  def addCheckBox = jPanelBuilder.addCheckBox
  def addButton = jPanelBuilder.addButton
}

class JPanelBuilderTextField(jPanelBuilder: JPanelBuilder, jTextField: JTextField) extends JPanelBuilderDelegator(jPanelBuilder) {
  def text(text: String) = { jTextField.setText(text); this }
  def text(text: Observable[String]) = { text.subscribe(jTextField.setText(_)); this }
  def size(columns: Int) = { jTextField.setColumns(columns); this }
  def onTextChanged(textChanged: String => Unit) = { jTextField.onTextChanged(textChanged); textChanged(jTextField.getText); this }
  def rememberInRegistry(rememberValueInRegistryName: String) = { jTextField.rememberValueInRegistry(rememberValueInRegistryName); this }
}
class JPanelBuilderLabel(jPanelBuilder: JPanelBuilder, jLabel: JLabel) extends JPanelBuilderDelegator(jPanelBuilder) {
  def text(text: String) = { jLabel.setText(text); this }
  def text(text: Observable[String]) = { text.subscribe(jLabel.setText(_)); this }
  def cursor_HAND_CURSOR = { jLabel.cursor_HAND_CURSOR; this }
  def onMouseClicked(action: String => Unit) = { jLabel.onMouseClicked(action(jLabel.getText)); this }
}
class JPanelBuilderCheckBox(jPanelBuilder: JPanelBuilder, jCheckBox: JCheckBox) extends JPanelBuilderDelegator(jPanelBuilder) {
  def text(text: String) = { jCheckBox.setText(text); this }
  def text(text: Observable[String]) = { text.subscribe(jCheckBox.setText(_)); this }
  def selected(isSelected: Boolean) = { jCheckBox.setSelected(isSelected); this }
  def onSelectionChanged(action: Boolean => Unit) = { jCheckBox.onSelectionChange(action); action(jCheckBox.isSelected); this }
  def rememberInRegistry(rememberValueInRegistryName: String) = { jCheckBox.rememberValueInRegistry(rememberValueInRegistryName); this }
}
class JPanelBuilderButton(jPanelBuilder: JPanelBuilder, jButton: JButton) extends JPanelBuilderDelegator(jPanelBuilder) {
  def text(text: String) = { jButton.setText(text); this }
  def text(text: Observable[String]) = { text.subscribe(jButton.setText(_)); this }
  def onAction(action: String => Unit) = { jButton.onAction(action(jButton.getText)); this }
  def enabled(enabled: Observable[Boolean]) = { enabled.subscribe(jButton.setEnabled(_)); this }
}
class JPanelBuilderPasswordField(jPanelBuilder: JPanelBuilder, jPasswordField: JPasswordField) extends JPanelBuilderDelegator(jPanelBuilder) {
  def text(text: String) = { jPasswordField.setText(text); this }
  def text(text: Observable[String]) = { text.subscribe(jPasswordField.setText(_)); this }
  def size(columns: Int) = { jPasswordField.setColumns(columns); this }
  def onPasswordChanged(textChanged: Array[Char] => Unit) = { jPasswordField.onTextChanged(textChanged(jPasswordField.getPassword)); textChanged(jPasswordField.getPassword); this }
  def rememberInRegistry(rememberValueInRegistryName: String) = { jPasswordField.rememberValueInRegistry(rememberValueInRegistryName); this }
}
class JPanelBuilderTextArea(jPanelBuilder: JPanelBuilder, jTextArea: JTextArea) extends JPanelBuilderDelegator(jPanelBuilder) {
  def text(text: String) = { jTextArea.setText(text); this }
  def text(text: Observable[String]) = { text.subscribe(jTextArea.setText(_)); this }
  def textAppender(text: Observable[String]) = { text.subscribe(jTextArea.appendText(_)); this }
  def size(columns: Int) = { jTextArea.setColumns(columns); this }
  def onTextChanged(textChanged: String => Unit) = { jTextArea.onTextChanged(textChanged); textChanged(jTextArea.getText); this }
  def rememberInRegistry(rememberValueInRegistryName: String) = { jTextArea.rememberValueInRegistry(rememberValueInRegistryName); this }
}
class JPanelBuilderList[LIST_TYPE](jPanelBuilder: JPanelBuilder, jList: JList[LIST_TYPE]) extends JPanelBuilderDelegator(jPanelBuilder) {
  @deprecated("Should extend the JPanelBuilderList instead setup", "per default") def setupList(setup: JList[LIST_TYPE] => Unit) = { setup(jList); this }
}
class JPanelBuilderTable[TYPE](jPanelBuilder: JPanelBuilder, jTable: JTablePlus[TYPE]) extends JPanelBuilderDelegator(jPanelBuilder) {
  @deprecated("Should extend the JPanelBuilderTable instead setup", "per default") def setupTable(setup: JTablePlus[TYPE] => Unit) = { setup(jTable); this }
}
class JPanelBuilderProgressBar(jPanelBuilder: JPanelBuilder, jProgressBar: JProgressBarPlus) extends JPanelBuilderDelegator(jPanelBuilder) {
  @deprecated("Should extend the JPanelBuilderProgressBar instead setup", "per default") def setupprogressBAr(setup: JProgressBarPlus => Unit) = { setup(jProgressBar); this }
    def setValue(value: Int) =  {jProgressBar.setValue(value); this}
    def setStringPainted(painted: Boolean) =  {jProgressBar.setStringPainted(painted); this}
    def setString(value: String) =  {jProgressBar.setString(value); this}
}
class JPanelBuilderMultiFieldJTextField(jPanelBuilder: JPanelBuilder, multiText: MultiFieldJTextField) extends JPanelBuilderDelegator(jPanelBuilder) {
  def setValues(values: List[String]) = {multiText.setValues(values); this}
}
class JPanelBuilderMultiFieldAutocomplete(jPanelBuilder: JPanelBuilder, multiAutocomplete: MultiFieldAutocomplete) extends JPanelBuilderDelegator(jPanelBuilder) {
}
class JPanelBuilderPanelBuilder(jPanelBuilderParent: JPanelBuilder, jPanelBuilder: JPanelBuilder) extends JPanelBuilderDelegator(jPanelBuilder) {
  def done: IPanelBuilder = jPanelBuilderParent
  def exit: IPanelBuilder = done
}


