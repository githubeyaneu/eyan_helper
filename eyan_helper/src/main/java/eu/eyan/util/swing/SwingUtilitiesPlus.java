package eu.eyan.util.swing;

import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.forms.factories.CC;

import eu.eyan.util.jgoodies.FormLayoutPlus;

public class SwingUtilitiesPlus {

	public static ListDataListener createListContentsChangedListener(Consumer<ListDataEvent> listDataContentsChangedEventConsumer) {
		return new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				listDataContentsChangedEventConsumer.accept(e);
			}
		};
	}

	public static void addKeyPressedListener(JTextPane textPane, Consumer<KeyEvent> keyPressedEventConsumer) {
		textPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				keyPressedEventConsumer.accept(e);
			}
		});
	}

	public static FlowPanel newLeftFlowPanel() {
		FlowPanel flowPanel = new FlowPanel(new FlowLayout(FlowLayout.LEFT));
		return flowPanel;
	}

	// CHECKBOX
	public static JCheckBox newCheckBoxWithAction(String text, Runnable actionListener) {
		return newCheckBoxWithAction(text, (cb, e) -> actionListener.run());
	}

	public static JCheckBox jCheckBox(String text, Consumer<JCheckBox> actionListener) {
		return newCheckBoxWithAction(text, (cb, e) -> actionListener.accept(cb));
	}

	public static JCheckBox newCheckBoxWithAction(String text, BiConsumer<JCheckBox, ActionEvent> actionListener) {
		JCheckBox cb = new JCheckBox(text);
		cb.addActionListener(e -> actionListener.accept(cb, e));
		return cb;
	}

	// BUTTON
	public static JButton jButton(String text, Runnable actionListener) {
		return newButtonWithAction(text, (b, e) -> actionListener.run());
	}

	public static JButton newButtonWithAction(String text, BiConsumer<JButton, ActionEvent> actionListener) {
		JButton button = new JButton(text);
		button.addActionListener(e -> actionListener.accept(button, e));
		return button;
	}

	// TEXTFIELD
	public static JTextFieldPlus jTextField(int columns, Consumer<JTextField> actionListener) {
		return newTextFieldWithAction(columns, (tf, e) -> actionListener.accept(tf));
	}

	public static JTextFieldPlus newTextFieldWithAction(int columns, BiConsumer<JTextField, ActionEvent> actionListener) {
		JTextFieldPlus tf = new JTextFieldPlus(columns);
		tf.addActionListener(e -> actionListener.accept(tf, e));
		return tf;
	}

	// LABEL
	public static JLabel jLabel(String text) {
		return new JLabel(text);
	}

	// PROGRESSBAR
	public static JProgressBarPlus jProgressBarPercent(String format) {
		JProgressBarPlus progressBar = new JProgressBarPlus(0, 100, format);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		invokeLater(() -> progressBar.setString("..."));
		return progressBar;
	}

	public static Container jPanelOneRow(String rowSpec, String col1Spec, Component col1Comp, String col2Spec, Component col2Comp) {
		FormLayoutPlus layout = new FormLayoutPlus(new JPanel(), col1Spec + "," + col2Spec);
		layout.appendRow(rowSpec);
		layout.getComponent().add(col1Comp, CC.xy(1, 1));
		layout.getComponent().add(col2Comp, CC.xy(2, 1));
		return layout.getComponent();
	}
}