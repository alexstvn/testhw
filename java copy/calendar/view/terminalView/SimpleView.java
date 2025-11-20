package calendar.view.terminalView;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a simplistic, text-style view of a model.
 */
public class SimpleView implements InterfaceView {
  private final Appendable out;

  /**
   * Initializes view with an appendable object to allow for continuous text add-ons.
   *
   * @param out Appendable object to eventually use to display output.
   */
  public SimpleView(Appendable out) {
    this.out = Objects.requireNonNull(out);
  }

  /**
   * Appends to the output if possible.
   *
   * @param message Message to append to output.
   */
  private void write(String message) {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot write to appendable.", e);
    }
  }

  /**
   * Writes or appends message to the appendable output.
   *
   * @param message Message to append to output.
   */
  @Override
  public void renderMessage(String message) {
    if (message == null || message.isBlank()) {
      return;
    }
    write(message.stripTrailing() + System.lineSeparator());
  }
}
