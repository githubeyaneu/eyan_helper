package eu.eyan.util.swing

import javax.swing.AbstractButton
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.awt.AwtHelper
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.event.ChangeEvent
import java.awt.event.ItemEvent
import java.io.File
import eu.eyan.util.text.Text
import javax.swing.ImageIcon
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.util.text.TextsButton
import java.net.URL
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

object AbstractButtonPlus {

  implicit class AbstractButtonImplicit[TYPE <: AbstractButton](abstractButton: TYPE) extends JComponentImplicit(abstractButton) {
    //addActionListener(ActionListener)
    //??
    def onActionPerformedEvent(action: ActionEvent => Unit) = { abstractButton.addActionListener(AwtHelper.onActionPerformed(action)); abstractButton }
    def onActionPerformed(action: => Unit) = onActionPerformedEvent { e => action }
    def onAction(action: => Unit): TYPE = onActionPerformedEvent { e => action }

    def onActionEvent_EnableDisable(action: ActionEvent => Unit) = onActionPerformedEvent { e => abstractButton.disabled; SwingPlus.runInWorker(action(e), abstractButton.enabled) }
    def onAction_disableEnable(action: => Unit) = onActionEvent_EnableDisable { e => action }

    def onChange(action: => Unit) = onChangeEvent { e => action; }
    def onChangeEvent(action: ChangeEvent => Unit) = { abstractButton.addChangeListener(SwingPlus.onStateChanged(action)); abstractButton }
    def onStateChange(action: => Unit) = onChangeEvent { e => action; }
    def onStateChangeEvent(action: ChangeEvent => Unit) = { abstractButton.addChangeListener(SwingPlus.onStateChanged(action)); abstractButton }

    def onItemStateChange(action: => Unit) = onItemStateChangeEvent { e => action; }
    def onItemStateChangeEvent(action: ItemEvent => Unit) = { abstractButton.addItemListener(SwingPlus.onItemStateChanged(action)); abstractButton }

    def onSelectionChange(action: Boolean => Unit) = onChange(action(abstractButton.isSelected))
    
    def selectedObservable = {
      val subject = BehaviorSubject(abstractButton.isSelected)
      onSelectionChange { subject.onNext(_) }
      subject.distinctUntilChanged
    }

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
    def isNotSelected = !abstractButton.isSelected
    def selected = { abstractButton.setSelected(true); abstractButton }
    def notSelected = { abstractButton.setSelected(false); abstractButton }
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

    def texts(texts: TextsButton) = text(texts.text).toolTip(texts.tooltip).icon(texts.icon)
    
    def text(text: Observable[String]) = {
      //FIXME make it safe!
      text.subscribe(abstractButton.setText _)
      abstractButton
    }
    def icon(path: Observable[String]) = {
      //FIXME make it safe!
      path.subscribe(path => abstractButton.setIcon(path.toIconAsResource))
      abstractButton
    }
    
    def toolTip(tip: Observable[String]) = {
      //FIXME make it safe!
      tip.subscribe(abstractButton.setToolTipText _)
      abstractButton
    }
  }
}