package calendar.view.terminalView;

/**
 * Represents output viewer of a model.
 */
public interface InterfaceView {
  /**
   * Appends messages to an output.
   *
   * @param message Message to append to output.
   */
  void renderMessage(String message);
}
