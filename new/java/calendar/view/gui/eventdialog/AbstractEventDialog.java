package calendar.view.gui.eventdialog;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

/**
 * Represents a general event handling dialog that contains common methods for populating
 * a dialog window.
 */
public abstract class AbstractEventDialog extends JDialog {
  public static final String ACTION_OK = "OK";
  public static final String ACTION_CANCEL = "CANCEL";
  public static final String ACTION_TOGGLE_ALL_DAY = "TOGGLE_ALL_DAY";

  protected static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);
  protected static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);

  protected JButton okButton;
  protected JButton cancelButton;

  protected JCheckBox allDayCheckBox;

  protected JSpinner startDateSpinner;
  protected JSpinner endDateSpinner;

  protected JPanel startTimePanel;
  protected JPanel endTimePanel;

  protected JSpinner startHourSpinner;
  protected JSpinner startMinuteSpinner;
  protected JSpinner endHourSpinner;
  protected JSpinner endMinuteSpinner;

  protected boolean confirmed;

  protected EventFormData eventInfo;

  /**
   * Initializes the JDialog window.
   *
   * @param owner    Initializes using owner passed from dialog initialization.
   * @param title    Title of dialog window.
   * @param editable Sets modal of JDialog.
   */
  public AbstractEventDialog(Frame owner, String title, boolean editable) {
    super(owner, title, editable);
  }

  /**
   * Adds an action listener to the dialog's buttons.
   * This is how the controller connects to the view.
   *
   * @param listener the action listener (typically from controller)
   */
  public void addActionListener(ActionListener listener) {
    okButton.addActionListener(listener);
    cancelButton.addActionListener(listener);
  }

  /**
   * Toggles the all-day event UI state.
   * Made public so controller can call it.
   */
  public void toggleAllDayEvent() {
    boolean isAllDay = allDayCheckBox.isSelected();

    setTimeComponentsEnabled(startTimePanel, !isAllDay);
    setTimeComponentsEnabled(endTimePanel, !isAllDay);

    if (isAllDay) {
      startHourSpinner.setValue(ALL_DAY_START.getHour());
      startMinuteSpinner.setValue(ALL_DAY_START.getMinute());
      endHourSpinner.setValue(ALL_DAY_END.getHour());
      endMinuteSpinner.setValue(ALL_DAY_END.getMinute());
      endDateSpinner.setValue(startDateSpinner.getValue());
    }
  }

  /**
   * Allows the time components of form to be editable.
   *
   * @param timePanel Time panel associated with field.
   * @param enabled   True if can be enabled, false if otherwise.
   */
  protected void setTimeComponentsEnabled(JPanel timePanel, boolean enabled) {
    for (Component comp : timePanel.getComponents()) {
      comp.setEnabled(enabled);
    }
  }

  /**
   * Creates a date field for the user to input a date.
   *
   * @param initialDate Initializes with a date (like the current day view).
   * @return A spinner object that the user can edit the date with.
   */
  protected JSpinner createDateSpinner(LocalDate initialDate) {
    Date date = Date.from(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    SpinnerDateModel dateModel = new SpinnerDateModel(
        date, null, null, Calendar.DATE
    );

    JSpinner spinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
    spinner.setEditor(dateEditor);
    dateEditor.getTextField().setColumns(10);

    return spinner;
  }

  /**
   * Generates a date time object using the form spinners.
   *
   * @param dateSpinner   Spinner that represents date selection.
   * @param hourSpinner   Spinner that represents hour selection.
   * @param minuteSpinner Spinner that represents minute selection.
   * @return A LocalDateTime object that combines the values of these spinners.
   */
  protected LocalDateTime generateDateTime(JSpinner dateSpinner,
                                           JSpinner hourSpinner,
                                           JSpinner minuteSpinner) {
    Date date = (Date) dateSpinner.getValue();
    int hour = Integer.parseInt(hourSpinner.getValue().toString());
    int minute = Integer.parseInt(minuteSpinner.getValue().toString());

    LocalDate localDate = date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
    LocalTime time = LocalTime.of(hour, minute);

    return LocalDateTime.of(localDate, time);
  }


  /**
   * Creates a time panel using spinners for the hour and minute.
   *
   * @param defaultHour   Default/initial hour value for spinner.
   * @param defaultMinute Default/initial minute value for spinner.
   * @return Panel that represents a combination time spinner.
   */
  protected JPanel createTimePanel(int defaultHour, int defaultMinute) {
    final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

    SpinnerNumberModel hourModel = new SpinnerNumberModel(
        defaultHour, 0, 23, 1
    );
    JSpinner hourSpinner = new JSpinner(hourModel);
    JSpinner.NumberEditor hourEditor = new JSpinner.NumberEditor(hourSpinner, "00");
    hourSpinner.setEditor(hourEditor);
    hourEditor.getTextField().setColumns(2);

    SpinnerNumberModel minuteModel = new SpinnerNumberModel(
        defaultMinute, 0, 59, 1
    );
    JSpinner minuteSpinner = new JSpinner(minuteModel);
    JSpinner.NumberEditor minuteEditor = new JSpinner.NumberEditor(minuteSpinner, "00");
    minuteSpinner.setEditor(minuteEditor);
    minuteEditor.getTextField().setColumns(2);

    panel.add(hourSpinner);
    panel.add(new JLabel(":"));
    panel.add(minuteSpinner);

    return panel;
  }

  /**
   * Returns the inputted fields as an event info object.
   *
   * @return Class representation of inputted fields.
   */
  public EventFormData getEventInfo() {
    return eventInfo;
  }

  /**
   * Determines whether an inputted form is successfully confirmed.
   *
   * @param confirmed True if successful, false if otherwise (or canceled).
   */
  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  /**
   * Returns whether or not the form was successfully confirmed.
   *
   * @return True if successful, false if otherwise (or canceled).
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  /**
   * Generates an error message based on the validation process.
   *
   * @param message Message to output to error dialog.
   */
  protected void showError(String message) {
    JOptionPane.showMessageDialog(
        this,
        message,
        "Validation Error",
        JOptionPane.ERROR_MESSAGE
    );
  }

}
