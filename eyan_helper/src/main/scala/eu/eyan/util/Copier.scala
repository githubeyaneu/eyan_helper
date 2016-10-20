package eu.eyan.util.swing;

import static javax.swing.SwingUtilities.invokeLater;

import java.util.function.Consumer;

import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class JProgressBarPlus extends JProgressBar {

  private Consumer<Integer> percentChangedConsumer;
  private String format;
  private String finishedText = "Ready";

  public JProgressBarPlus(int min, int max, String format) {
    super(min, max);
    this.format = format;
    this.percentChangedConsumer = createPercentChangedConsumer();
    this.setNewValue(min);
  }

  public Consumer<Integer> createPercentChangedConsumer() {
    return percent -> invokeLater(() -> {
      this.setVisible(true);
      setNewValue(percent);
    });
  }

  private void setNewValue(Integer percent) {
    this.setString(String.format(format, percent));
    this.setValue(percent);
  }

  public JProgressBarPlus percentChanged(int value) {
    if (this.getValue() != value) {
      this.percentChangedConsumer.accept(value);
    }
    return this;
  }

  public Runnable doneThenInvisible() {
    return () -> invokeLater(() -> this.setVisible(false));
  }

  public JProgressBarPlus setFormat(String format) {
    this.format = format;
    setNewValue(this.getValue());
    return this;
  }

  public JProgressBarPlus finished() {
    invokeLater(() -> {
      this.setString(finishedText);
      this.setValue(this.getMaximum());
    });
    return this;
  }
}