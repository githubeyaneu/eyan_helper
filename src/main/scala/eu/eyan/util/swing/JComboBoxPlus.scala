package eu.eyan.util.swing

import javax.swing.JComboBox
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object JComboBoxPlus {
  implicit class JComboBoxImplicit[E, TYPE <: JComboBox[E]](jComboBox: TYPE) extends JComponentImplicit(jComboBox){
    //actionPerformed(ActionEvent)
    //addActionListener(ActionListener)
    //addItem(E)
    //addItemListener(ItemListener)
    //addPopupMenuListener(PopupMenuListener)
    //configureEditor(ComboBoxEditor, Object)
    //contentsChanged(ListDataEvent)
    //firePopupMenuCanceled()
    //firePopupMenuWillBecomeInvisible()
    //firePopupMenuWillBecomeVisible()
    //hidePopup()
    //insertItemAt(E, int)
    //intervalAdded(ListDataEvent)
    //intervalRemoved(ListDataEvent)
    //processKeyEvent(KeyEvent)
    //removeActionListener(ActionListener)
    //removeAllItems()
    //removeItem(Object)
    //removeItemAt(int)
    //removeItemListener(ItemListener)
    //removePopupMenuListener(PopupMenuListener)
    //selectWithKeyChar(char)
    //setAction(Action)
    //setActionCommand(String)
    //setEditable(boolean)
    //setEditor(ComboBoxEditor)
    //setEnabled(boolean)
    //setKeySelectionManager(KeySelectionManager)
    //setLightWeightPopupEnabled(boolean)
    //setMaximumRowCount(int)
    //setModel(ComboBoxModel<E>)
    //setPopupVisible(boolean)
    //setPrototypeDisplayValue(E)
    //setRenderer(ListCellRenderer<? super E>)
    //setSelectedIndex(int)
    //setSelectedItem(Object)
    //setUI(ComboBoxUI)
    //showPopup()
    //updateUI()    
  }
}