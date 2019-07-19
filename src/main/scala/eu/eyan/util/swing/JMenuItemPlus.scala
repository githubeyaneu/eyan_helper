package eu.eyan.util.swing

import javax.swing.JMenuItem
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit

object JMenuItemPlus {
  implicit class JMenuItemImplicit[TYPE <: JMenuItem](jMenuItem: TYPE) extends AbstractButtonImplicit(jMenuItem){
    //addMenuDragMouseListener(MenuDragMouseListener)
    //addMenuKeyListener(MenuKeyListener)
    //menuSelectionChanged(boolean)
    //processKeyEvent(KeyEvent, MenuElement[], MenuSelectionManager)
    //processMenuDragMouseEvent(MenuDragMouseEvent)
    //processMenuKeyEvent(MenuKeyEvent)
    //processMouseEvent(MouseEvent, MenuElement[], MenuSelectionManager)
    //removeMenuDragMouseListener(MenuDragMouseListener)
    //removeMenuKeyListener(MenuKeyListener)
    //setAccelerator(KeyStroke)
    //setArmed(boolean)
    //setEnabled(boolean)
    //setModel(ButtonModel)
    //setUI(MenuItemUI)
    //updateUI()    
  }
}