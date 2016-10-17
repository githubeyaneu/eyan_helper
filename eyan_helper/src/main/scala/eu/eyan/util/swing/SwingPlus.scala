package eu.eyan.util.swing

import java.awt.Component
import java.awt.event.ActionEvent

import com.jgoodies.forms.factories.CC

import eu.eyan.util.awt.AwtHelper
import eu.eyan.util.jgoodies.FormLayoutPlus
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities

object SwingPlus {
  def showErrorDialog(msg: String, e: Throwable, shown: Set[Throwable] = Set()): Unit = {
    if (e.getCause != null && !shown.contains(e.getCause))
      showErrorDialog(msg + ", " + e.getLocalizedMessage, e.getCause, shown + e)
    else JOptionPane.showMessageDialog(null, msg + ", " + e.getLocalizedMessage)
  }

  def invokeLater(runnable: () => Unit) = SwingUtilities.invokeLater(AwtHelper.newRunnable(() => runnable() ))

  // TEXTFIELD
	def jTextField(columns:Int,  actionListener: JTextField => Unit) = newTextFieldWithAction(columns, (tf, e) => actionListener(tf))

	def newTextFieldWithAction(columns: Int, actionListener: (JTextField, ActionEvent) => Unit) = {
		val tf = new JTextFieldPlus(columns)
		tf.addActionListener(AwtHelper.newActionListener( e => actionListener(tf, e)))
		tf
	}
	
  // LABEL
	def label(text: String) = new JLabel(text) 
  
	// PROGRESSBAR
	def jProgressBarPercent( format:String) = {
		val progressBar = new JProgressBarPlus(0, 100, format)
		progressBar.setValue(0)
		progressBar.setStringPainted(true)
		progressBar.setVisible(false)
		invokeLater(() => progressBar.setString("..."))
		progressBar
	}
  
	def jPanelOneRow(rowSpec:String , col1Spec:String , col1Comp:Component , col2Spec:String , col2Comp:Component ) = {
		val layout = new FormLayoutPlus(new JPanel(), col1Spec + "," + col2Spec)
		layout.appendRow(rowSpec)
		layout.getComponent().add(col1Comp, CC.xy(1, 1))
		layout.getComponent().add(col2Comp, CC.xy(2, 1))
		layout.getComponent()
	}
}