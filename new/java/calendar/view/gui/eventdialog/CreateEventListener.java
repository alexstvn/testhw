package calendar.view.gui.eventdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for the Create Event Dialog.
 * Separates controller logic from view.
 */
public class CreateEventListener implements ActionListener {
  private final CreateEventDialog dialog;

  /**
   * Creates a listener for create event dialog actions.
   *
   * @param dialog the create event dialog
   */
  public CreateEventListener(CreateEventDialog dialog) {
    this.dialog = dialog;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    switch (command) {
      case "OK":
        handleOk();
        break;
      case "CANCEL":
        handleCancel();
        break;
      case "TOGGLE_ALL_DAY":
        handleToggleAllDay();
        break;
      case "TOGGLE_REPEATS":
        handleToggleRepeats();
        break;
      default:
        break;
    }
  }

  private void handleOk() {
    if (dialog.validateInputs()) {
      dialog.setConfirmed(true);
      dialog.dispose();
    }
  }

  private void handleCancel() {
    dialog.setConfirmed(false);
    dialog.dispose();
  }

  private void handleToggleAllDay() {
    dialog.toggleAllDayEvent();
  }

  private void handleToggleRepeats() {
    dialog.toggleRepeatsEvent();
  }
}