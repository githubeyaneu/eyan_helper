package eu.eyan.util.swing

import java.awt.{Color, ComponentOrientation, Insets, Point}
import java.awt.event.InputMethodEvent
import java.io.Writer

import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.awt.remember.RememberInRegistry
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.DropMode
import javax.swing.event.{CaretEvent, DocumentEvent}
import javax.swing.plaf.TextUI
import javax.swing.text._
import rx.lang.scala.Observer

object JTextComponentPlus {
  implicit class JTextComponentImplicit[TYPE <: JTextComponent](jTextComponent: TYPE) extends JComponentImplicit(jTextComponent) with RememberInRegistry[TYPE] {
    //    updateUI()

    //    AccessibleJTextComponent
    //      AccessibleJTextComponent(JTextComponent)
    //      caretUpdate(CaretEvent)
    //      changedUpdate(DocumentEvent)
    //      cut(Int, Int)
    //      delete(Int, Int)
    //      doAccessibleAction(Int)
    //      insertTextAtIndex(Int, String)
    //      insertUpdate(DocumentEvent)
    //      paste(Int)
    //      removeUpdate(DocumentEvent)
    //      replaceText(Int, Int, String)
    //      selectText(Int, Int)
    //      setAttributes(Int, Int, AttributeSet)
    //      setTextContents(String)

    def onCaretUpdate(action: => Unit) = onCaretUpdateEvent(e => action)
    def onCaretUpdateEvent(action: CaretEvent => Unit) = { jTextComponent.addCaretListener(SwingPlus.onCaretUpdate(action)); jTextComponent }

    def onTextChanged(observer: Observer[String]): TYPE = onTextChanged(observer.onNext _)
    def onTextChanged(action: String => Unit): TYPE = onTextChanged(action(jTextComponent.getText))
    def onTextChanged(action: => Unit): TYPE = onTextChangedEvent(e => action)
    def onTextChangedEvent(action: DocumentEvent => Unit) = { onChangedUpdateEvent(action); onRemoveUpdateEvent(action); onInsertUpdateEvent(action); jTextComponent }

    def onChangedUpdate(action: => Unit): TYPE = onChangedUpdateEvent(e => action)
    def onChangedUpdateEvent(action: DocumentEvent => Unit) = { jTextComponent.getDocument.addDocumentListener(SwingPlus.onChangedUpdate(action)); jTextComponent }
    def onRemoveUpdate(action: => Unit): TYPE = onRemoveUpdateEvent(e => action)
    def onRemoveUpdateEvent(action: DocumentEvent => Unit) = { jTextComponent.getDocument.addDocumentListener(SwingPlus.onRemoveUpdate(action)); jTextComponent }
    def onInsertUpdate(action: => Unit): TYPE = onInsertUpdateEvent(e => action)
    def onInsertUpdateEvent(action: DocumentEvent => Unit) = { jTextComponent.getDocument.addDocumentListener(SwingPlus.onInsertUpdate(action)); jTextComponent }

    override def onInputMethodTextChanged(action: => Unit) = onInputMethodTextChangedEvent(e => action)
    override def onInputMethodTextChangedEvent(action: InputMethodEvent => Unit) = { jTextComponent.addInputMethodListener(AwtHelper.onInputMethodTextChanged(action)); jTextComponent }
    override def onCaretPositionChanged(action: => Unit) = onCaretPositionChangedEvent(e => action)
    override def onCaretPositionChangedEvent(action: InputMethodEvent => Unit) = { jTextComponent.addInputMethodListener(AwtHelper.onCaretPositionChanged(action)); jTextComponent }

    def copy = { jTextComponent.copy(); jTextComponent }
    def cut = { jTextComponent.cut(); jTextComponent }
    def moveCaretPosition(pos: Int) = { jTextComponent.moveCaretPosition(pos); jTextComponent }
    def paste = { jTextComponent.paste(); jTextComponent }
    def replaceSelection(content: String) = { jTextComponent.replaceSelection(content); jTextComponent }
    def select(selectionStart: Int, selectionEnd: Int) = { jTextComponent.select(selectionStart, selectionEnd); jTextComponent }
    def selectAll = { jTextComponent.selectAll(); jTextComponent }
    def caret(c: Caret) = { jTextComponent.setCaret(c); jTextComponent }
    def caretColor(c: Color) = { jTextComponent.setCaretColor(c); jTextComponent }
    def caretPosition(position: Int) = { jTextComponent.setCaretPosition(position); jTextComponent }
    override def componentOrientation(o: ComponentOrientation) = { jTextComponent.setComponentOrientation(o); jTextComponent }
    def componentOrientation_leftToRight = componentOrientation(ComponentOrientation.LEFT_TO_RIGHT)
    def componentOrientation_rightToLeft = componentOrientation(ComponentOrientation.RIGHT_TO_LEFT)
    def componentOrientation_unknown = componentOrientation(ComponentOrientation.UNKNOWN)
    def disabledTextColor(c: Color) = { jTextComponent.setDisabledTextColor(c); jTextComponent }
    def document(doc: Document) = { jTextComponent.setDocument(doc); jTextComponent }
    def dragEnabled(b: Boolean) = { jTextComponent.setDragEnabled(b); jTextComponent }
    def dragEnabled: TYPE = dragEnabled(true)
    def dragDisabled: TYPE = dragEnabled(false)
    def dropMode(dropMode: DropMode) = { jTextComponent.setDropMode(dropMode); jTextComponent }
    def dropMode_insert = dropMode(DropMode.INSERT)
    def dropMode_useSelection = dropMode(DropMode.USE_SELECTION)
    def editable(b: Boolean) = { jTextComponent.setEditable(b); jTextComponent }
    def editable: TYPE = editable(true)
    def editableDisabeld: TYPE = editable(false)
    def focusAccelerator(aKey: Char) = { jTextComponent.setFocusAccelerator(aKey); jTextComponent }
    def highlighter(h: Highlighter) = { jTextComponent.setHighlighter(h); jTextComponent }
    def keymap(map: Keymap) = { jTextComponent.setKeymap(map); jTextComponent }
    def margin(m: Insets) = { jTextComponent.setMargin(m); jTextComponent }
    def navigationFilter(filter: NavigationFilter) = { jTextComponent.setNavigationFilter(filter); jTextComponent }
    def selectedTextColor(c: Color) = { jTextComponent.setSelectedTextColor(c); jTextComponent }
    def selectionColor(c: Color) = { jTextComponent.setSelectionColor(c); jTextComponent }
    def selectionEnd(selectionEnd: Int) = { jTextComponent.setSelectionEnd(selectionEnd); jTextComponent }
    def selectionStart(selectionStart: Int) = { jTextComponent.setSelectionStart(selectionStart); jTextComponent }
    def text(t: String) = { jTextComponent.setText(t); jTextComponent }
    def uI(ui: TextUI) = { jTextComponent.setUI(ui); jTextComponent }
    def updateUI = { jTextComponent.updateUI(); jTextComponent }
    def viewToModel(p: Point) = { jTextComponent.viewToModel(p); jTextComponent }
    def write(out: Writer) = { jTextComponent.write(out); jTextComponent }

    def alwaysScrollDown() = { jTextComponent.getCaret.asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); jTextComponent }
    def dontScroll() = { jTextComponent.getCaret.asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.NEVER_UPDATE); jTextComponent }
    def clickSelectsAll = onClicked(jTextComponent.selectAll())
    def lines: Iterator[String] = jTextComponent.getText.lines
    def onClickedSelectAll = onClicked(jTextComponent.selectAll())

    protected def rememberComponent = jTextComponent
    protected def rememberEventListener(action: => Unit) = onTextChanged(action)
    protected def rememberValueGet = jTextComponent.getText
    protected def rememberValueSet(value: String) = jTextComponent.setText(value)
    
    def nonEmpty:Boolean = jTextComponent.getText.nonEmpty
    def isEmpty:Boolean = jTextComponent.getText.nonEmpty
  }
}