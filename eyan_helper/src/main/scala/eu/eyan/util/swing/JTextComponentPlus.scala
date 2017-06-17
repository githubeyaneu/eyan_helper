package eu.eyan.util.swing

import javax.swing.text.JTextComponent
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object JTextComponentPlus {
  implicit class JTextComponentImplicit[TYPE <: JTextComponent](jTextComponent: TYPE) extends JComponentImplicit(jTextComponent){
//    AccessibleJTextComponent
//      AccessibleJTextComponent(JTextComponent)
//      caretUpdate(CaretEvent)
//      changedUpdate(DocumentEvent)
//      cut(int, int)
//      delete(int, int)
//      doAccessibleAction(int)
//      insertTextAtIndex(int, String)
//      insertUpdate(DocumentEvent)
//      paste(int)
//      removeUpdate(DocumentEvent)
//      replaceText(int, int, String)
//      selectText(int, int)
//      setAttributes(int, int, AttributeSet)
//      setTextContents(String)
    
    
    
//    JTextComponent()
//    addCaretListener(CaretListener)
//    addInputMethodListener(InputMethodListener)
//    copy()
//    cut()
//    isEditable()
//    modelToView(int)
//    moveCaretPosition(int)
//    paste()
//    print()
//    print(MessageFormat, MessageFormat)
//    print(MessageFormat, MessageFormat, boolean, PrintService, PrintRequestAttributeSet, boolean)
//    read(Reader, Object)
//    removeCaretListener(CaretListener)
//    removeNotify()
//    replaceSelection(String)
//    select(int, int)
//    selectAll()
//    setCaret(Caret)
//    setCaretColor(Color)
//    setCaretPosition(int)
//    setComponentOrientation(ComponentOrientation)
//    setDisabledTextColor(Color)
//    setDocument(Document)
//    setDragEnabled(boolean)
//    setDropMode(DropMode)
//    setEditable(boolean)
//    setFocusAccelerator(char)
//    setHighlighter(Highlighter)
//    setKeymap(Keymap)
//    setMargin(Insets)
//    setNavigationFilter(NavigationFilter)
//    setSelectedTextColor(Color)
//    setSelectionColor(Color)
//    setSelectionEnd(int)
//    setSelectionStart(int)
//    setText(String)
//    setUI(TextUI)
//    updateUI()
//    viewToModel(Point)
//    write(Writer)
  }
}