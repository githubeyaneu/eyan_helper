package eu.eyan.util.swing;

import static javax.swing.SwingUtilities.invokeLater;

import java.util.function.Consumer;

import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class JProgressBarPlus extends JProgressBar {

	private Consumer<Integer> percentChangedConsumer;
	private String format;

	public JProgressBarPlus(int min, int max, String format) {
		super(min, max);
		this.format = format;
		this.percentChangedConsumer = createPercentChangedConsumer();
	}

	public Consumer<Integer> createPercentChangedConsumer() {
		return percent -> invokeLater(() -> {
			this.setVisible(true);
			this.setString(String.format(format, percent));
			this.setValue(percent);
		});
	}

	public JProgressBarPlus percentChanged(int value) {
		if (this.getValue() != value && this.percentChangedConsumer != null) {
			this.percentChangedConsumer.accept(value);
		}
		return this;
	}

	public Runnable doneThenInvisible() {
		return () -> invokeLater(() -> this.setVisible(false));
	}

	public JProgressBarPlus setFormat(String format) {
		this.format = format;
		return this;
	}

	public JProgressBarPlus finished() {
		invokeLater(() -> {
			this.setString("Ready");
			this.setValue(100);
		});
		return this;
	}
}