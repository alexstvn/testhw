package calendar.controller.guicontroller;

import calendar.view.guiView.editEventView.EditEventDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for the Edit Event Dialog.
 * Separates controller logic from view.
 */
public class EditEventListener implements ActionListener {
  private final EditEventDialog dialog;

  /**
   * Creates a listener for edit event dialog actions.
   *
   * @param dialog the edit event dialog
   */
  public EditEventListener(EditEventDialog dialog) {
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
}