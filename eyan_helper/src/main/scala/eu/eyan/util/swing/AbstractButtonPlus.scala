package eu.eyan.util.swing

import javax.swing.AbstractButton
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.awt.AwtHelper
import java.awt.event.ActionEvent
import akka.actor.ActorRef
import javax.swing.JButton
import eu.eyan.util.swingakka.ActionEventHandler
import javax.swing.event.ChangeEvent
import java.awt.event.ItemEvent

object AbstractButtonPlus {

  implicit class AbstractButtonImplicit[TYPE <: AbstractButton](abstractButton: TYPE) extends JComponentImplicit(abstractButton) {
    //addActionListener(ActionListener)
    //??
    def onActionPerformedEvent(action: ActionEvent => Unit) = { abstractButton.addActionListener(AwtHelper.onActionPerformed(action)); abstractButton }
    def onActionPerformed(action: => Unit) = onActionPerformedEvent { e => action }
    def onAction(action: => Unit): TYPE = onActionPerformedEvent { e => action }
    def onAction_Actor(actor: ActorRef): TYPE = { onActionPerformedEvent { e => actor ! e }; abstractButton }

    def onActionEvent_EnableDisable(action: ActionEvent => Unit) = onActionPerformedEvent { e => abstractButton.disabled; SwingPlus.runInWorker(action(e), abstractButton.enabled) }
    def onAction_disableEnable(action: => Unit) = onActionEvent_EnableDisable { e => action }

    def onChange(action: => Unit) = onChangeEvent { e => action; }
    def onChangeEvent(action: ChangeEvent => Unit) = { abstractButton.addChangeListener(SwingPlus.onStateChanged(action)); abstractButton }
    def onStateChange(action: => Unit) = onChangeEvent { e => action; }
    def onStateChangeEvent(action: ChangeEvent => Unit) = { abstractButton.addChangeListener(SwingPlus.onStateChanged(action)); abstractButton }

    def onItemStateChange(action: => Unit) = onItemStateChangeEvent { e => action; }
    def onItemStateChangeEvent(action: ItemEvent => Unit) = { abstractButton.addItemListener(SwingPlus.onItemStateChanged(action)); abstractButton }

    def onSelectionChange(action: Boolean => Unit) = onChange(action(abstractButton.isSelected))

    //addChangeListener(ChangeListener)
    //    addItemListener(ItemListener)
    //doClick()
    //doClick(int)
    //imageUpdate(Image, int, int, int, int, int)
    //isBorderPainted()
    //isContentAreaFilled()
    //isFocusPainted()
    //isRolloverEnabled()
    //isSelected()
    def notSelected = !abstractButton.isSelected
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