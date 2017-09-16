package eu.eyan.util.swing

import javax.swing.AbstractButton
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.awt.AwtHelper
import java.awt.event.ActionEvent
import akka.actor.ActorRef
import javax.swing.JButton
import eu.eyan.util.swingakka.ActionEventHandler

object AbstractButtonPlus {
  
  implicit class AbstractButtonImplicit[TYPE <: AbstractButton](abstractButton: TYPE) extends JComponentImplicit(abstractButton) {
    //addActionListener(ActionListener)
    //??
    def onActionPerformedEvent(action: ActionEvent => Unit) = { abstractButton.addActionListener(AwtHelper.onActionPerformed(action)); abstractButton }
    def onActionPerformed(action: => Unit) = onActionPerformedEvent { e => action }
    def onAction(action: => Unit):TYPE = onActionPerformedEvent { e => action }
    def onAction_Actor(actor: ActorRef):TYPE = {onActionPerformedEvent { e => actor ! e }; abstractButton}

    def onActionEvent_EnableDisable(action: ActionEvent => Unit) = onActionPerformedEvent { e => abstractButton.disabled; SwingPlus.runInWorker(action(e), abstractButton.enabled) }
    def onAction_disableEnable(action: => Unit) = onActionEvent_EnableDisable { e => action }
    

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