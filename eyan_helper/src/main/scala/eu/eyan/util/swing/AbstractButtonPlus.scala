package eu.eyan.util.swing

import javax.swing.AbstractButton
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.awt.AwtHelper
import java.awt.event.ActionEvent

object AbstractButtonPlus {
  implicit class AbstractButtonImplicit[TYPE <: AbstractButton](abstractButton: TYPE) extends JComponentImplicit(abstractButton) {
    //addActionListener(ActionListener)
    //??
    def onAction[A](action: () => A): TYPE = { abstractButton.addActionListener(AwtHelper.onActionPerformed(e => action())); abstractButton }
    def onAction_Parallel_Disabled(action: => Unit) = onActionEvent_Parallel_Disabled { e => action }
    def onActionEvent_Parallel_Disabled(action: ActionEvent => Unit) = {
      abstractButton.addActionListener(AwtHelper.onActionPerformed(e => {
        abstractButton.disabled
        SwingPlus.runInWorker(action(e), abstractButton.enabled)
      }))
      this
    }
    //addChangeListener(ChangeListener)
    //addItemListener(ItemListener)
    //doClick()
    //doClick(int)
    //imageUpdate(Image, int, int, int, int, int)
    //isBorderPainted()
    //isContentAreaFilled()
    //isFocusPainted()
    //isRolloverEnabled()
    //isSelected()
    //removeActionListener(ActionListener)
    //removeChangeListener(ChangeListener)
    //removeItemListener(ItemListener)
    //removeNotify()
    //setAction(Action)
    //setActionCommand(String)
    //setBorderPainted(boolean)
    //setContentAreaFilled(boolean)
    //setDisabledIcon(Icon)
    //setDisabledSelectedIcon(Icon)
    //setDisplayedMnemonicIndex(int)
    //setEnabled(boolean)
    //setFocusPainted(boolean)
    //setHideActionText(boolean)
    //setHorizontalAlignment(int)
    //setHorizontalTextPosition(int)
    //setIcon(Icon)
    //setIconTextGap(int)
    //setLabel(String)
    //setLayout(LayoutManager)
    //setMargin(Insets)
    //setMnemonic(char)
    //setMnemonic(int)
    //setModel(ButtonModel)
    //setMultiClickThreshhold(long)
    //setPressedIcon(Icon)
    //setRolloverEnabled(boolean)
    //setRolloverIcon(Icon)
    //setRolloverSelectedIcon(Icon)
    //setSelected(boolean)
    //setSelectedIcon(Icon)
    //setText(String)
    //setUI(ButtonUI)
    //setVerticalAlignment(int)
    //setVerticalTextPosition(int)
    //updateUI()    
  }
}