package calendar.view.guiView.createEventView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

/**
 * Dialog for creating a single or recurring event with proper date and time pickers.
 * Now follows MVC: View only handles UI, Controller handles logic.
 */
public class CreateEventDialog extends JDialog {
  public static final String ACTION_OK = "OK";
  public static final String ACTION_CANCEL = "CANCEL";
  public static final String ACTION_TOGGLE_ALL_DAY = "TOGGLE_ALL_DAY";
  public static final String ACTION_TOGGLE_REPEATS = "TOGGLE_REPEATS";

  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);

  private JTextField subjectField;
  private JTextField locationField;
  private JTextArea descriptionField;
  private JCheckBox isPrivateCheckBox;
  private JCheckBox repeatsCheckBox;
  private JCheckBox allDayCheckBox;

  private JSpinner startDateSpinner;
  private JSpinner endDateSpinner;

  private JSpinner startHourSpinner;
  private JSpinner startMinuteSpinner;
  private JSpinner endHourSpinner;
  private JSpinner endMinuteSpinner;

  private JPanel startTimePanel;
  private JPanel endTimePanel;

  // Recurring event fields
  private JPanel recurringPanel;
  private JCheckBox mondayCheckBox;
  private JCheckBox tuesdayCheckBox;
  private JCheckBox wednesdayCheckBox;
  private JCheckBox thursdayCheckBox;
  private JCheckBox fridayCheckBox;
  private JCheckBox saturdayCheckBox;
  private JCheckBox sundayCheckBox;

  private JRadioButton endsOnDateRadio;
  private JRadioButton endsAfterOccurrencesRadio;
  private JSpinner endDateRecurringSpinner;
  private JSpinner occurrencesSpinner;

  private JButton okButton;
  private JButton cancelButton;

  private boolean confirmed;
  private EventInfo eventInfo;
  private LocalDate initialDate;

  /**
   * Creates a create event dialog.
   *
   * @param owner the parent frame
   * @param initialDate the initial date to set in the form
   */
  public CreateEventDialog(Frame owner, LocalDate initialDate) {
    super(owner, "Create Event", true);
    this.confirmed = false;
    this.initialDate = initialDate != null ? initialDate : LocalDate.now();
    initializeComponents();
    setSize(550, 650);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents() {
    setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;

    // Subject
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    formPanel.add(new JLabel("Subject:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    subjectField = new JTextField(30);
    formPanel.add(subjectField, gbc);

    // All-day checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    formPanel.add(new JLabel("All Day Event:"), gbc);

    gbc.gridx = 1;
    allDayCheckBox = new JCheckBox();
    formPanel.add(allDayCheckBox, gbc);

    // Start date
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("Start Date:"), gbc);

    gbc.gridx = 1;
    startDateSpinner = createDateSpinner(initialDate);
    formPanel.add(startDateSpinner, gbc);

    // Start time
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("Start Time:"), gbc);

    gbc.gridx = 1;
    startTimePanel = createTimePanel(9, 0);
    startHourSpinner = (JSpinner) startTimePanel.getComponent(0);
    startMinuteSpinner = (JSpinner) startTimePanel.getComponent(2);
    formPanel.add(startTimePanel, gbc);

    // End date
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("End Date:"), gbc);

    gbc.gridx = 1;
    endDateSpinner = createDateSpinner(initialDate);
    formPanel.add(endDateSpinner, gbc);

    // End time
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("End Time:"), gbc);

    gbc.gridx = 1;
    endTimePanel = createTimePanel(10, 0);
    endHourSpinner = (JSpinner) endTimePanel.getComponent(0);
    endMinuteSpinner = (JSpinner) endTimePanel.getComponent(2);
    formPanel.add(endTimePanel, gbc);

    // Location
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    formPanel.add(new JLabel("Location:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    locationField = new JTextField(30);
    formPanel.add(locationField, gbc);

    // Description
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    formPanel.add(new JLabel("Description:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 0.5;
    gbc.fill = GridBagConstraints.BOTH;

    descriptionField = new JTextArea(3, 30);
    descriptionField.setLineWrap(true);
    descriptionField.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(descriptionField);
    formPanel.add(scrollPane, gbc);

    // isPrivate checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weighty = 0.0;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    formPanel.add(new JLabel("Private Event:"), gbc);

    gbc.gridx = 1;
    isPrivateCheckBox = new JCheckBox();
    formPanel.add(isPrivateCheckBox, gbc);

    // Repeats checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("Repeats:"), gbc);

    gbc.gridx = 1;
    repeatsCheckBox = new JCheckBox();
    formPanel.add(repeatsCheckBox, gbc);

    // Recurring event panel (initially hidden)
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    gbc.fill = GridBagConstraints.BOTH;
    recurringPanel = createRecurringPanel();
    recurringPanel.setVisible(false);
    formPanel.add(recurringPanel, gbc);

    // CONSTRUCTING EVERYTHING
    add(formPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    okButton = new JButton("Create");
    okButton.setActionCommand(ACTION_OK);
    buttonPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(ACTION_CANCEL);
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);

    allDayCheckBox.setActionCommand(ACTION_TOGGLE_ALL_DAY);
    repeatsCheckBox.setActionCommand(ACTION_TOGGLE_REPEATS);
  }

  /**
   * Creates the panel for recurring event options.
   */
  private JPanel createRecurringPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;

    // Days of week label
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.gridwidth = 7;
    panel.add(new JLabel("Repeat on:"), gbc);

    // Days of week checkboxes
    gbc.gridy = row++;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0;

    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    mondayCheckBox = new JCheckBox("M");
    tuesdayCheckBox = new JCheckBox("T");
    wednesdayCheckBox = new JCheckBox("W");
    thursdayCheckBox = new JCheckBox("Th");
    fridayCheckBox = new JCheckBox("F");
    saturdayCheckBox = new JCheckBox("Sa");
    sundayCheckBox = new JCheckBox("Su");

    daysPanel.add(mondayCheckBox);
    daysPanel.add(tuesdayCheckBox);
    daysPanel.add(wednesdayCheckBox);
    daysPanel.add(thursdayCheckBox);
    daysPanel.add(fridayCheckBox);
    daysPanel.add(saturdayCheckBox);
    daysPanel.add(sundayCheckBox);

    gbc.gridx = 0;
    gbc.gridwidth = 7;
    panel.add(daysPanel, gbc);

    // Ends section
    gbc.gridy = row++;
    gbc.gridx = 0;
    gbc.gridwidth = 7;
    panel.add(new JLabel("Ends:"), gbc);

    // Radio button group for end options
    ButtonGroup endGroup = new ButtonGroup();
    endsOnDateRadio = new JRadioButton("On");
    endsAfterOccurrencesRadio = new JRadioButton("After");
    endGroup.add(endsOnDateRadio);
    endGroup.add(endsAfterOccurrencesRadio);
    endsOnDateRadio.setSelected(true);

    // "On [date]" option
    gbc.gridy = row++;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0;
    panel.add(endsOnDateRadio, gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 6;
    gbc.weightx = 1.0;
    endDateRecurringSpinner = createDateSpinner(initialDate.plusMonths(1));
    panel.add(endDateRecurringSpinner, gbc);

    // "After [n] occurrences" option
    gbc.gridy = row++;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0;
    panel.add(endsAfterOccurrencesRadio, gbc);

    JPanel occurrencesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    SpinnerNumberModel occurrencesModel = new SpinnerNumberModel(10, 1, 365, 1);
    occurrencesSpinner = new JSpinner(occurrencesModel);
    JSpinner.NumberEditor occurrencesEditor = new JSpinner.NumberEditor(occurrencesSpinner, "0");
    occurrencesSpinner.setEditor(occurrencesEditor);
    occurrencesEditor.getTextField().setColumns(4);

    occurrencesPanel.add(occurrencesSpinner);
    occurrencesPanel.add(new JLabel("occurrences"));

    gbc.gridx = 1;
    gbc.gridwidth = 6;
    gbc.weightx = 1.0;
    panel.add(occurrencesPanel, gbc);

    // Add listeners to enable/disable spinners based on radio selection
    endsOnDateRadio.addActionListener(e -> {
      endDateRecurringSpinner.setEnabled(true);
      occurrencesSpinner.setEnabled(false);
    });

    endsAfterOccurrencesRadio.addActionListener(e -> {
      endDateRecurringSpinner.setEnabled(false);
      occurrencesSpinner.setEnabled(true);
    });

    // Set initial state
    occurrencesSpinner.setEnabled(false);

    return panel;
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
    allDayCheckBox.addActionListener(listener);
    repeatsCheckBox.addActionListener(listener);
  }

  /**
   * Validates user inputs and creates EventInfo if valid.
   * Called by the controller through the listener.
   *
   * @return true if validation passes, false otherwise
   */
  public boolean validateInputs() {
    String subject = subjectField.getText().trim();
    if (subject.isEmpty()) {
      showError("Subject cannot be empty");
      return false;
    }

    try {
      LocalDateTime start = generateDateTime(startDateSpinner, startHourSpinner, startMinuteSpinner);
      LocalDateTime end = generateDateTime(endDateSpinner, endHourSpinner, endMinuteSpinner);

      if (!end.isAfter(start)) {
        showError("End date/time must be after start date/time");
        return false;
      }

      if (allDayCheckBox.isSelected() && !start.toLocalDate().equals(end.toLocalDate())) {
        showError("All-day events must start and end on the same day");
        return false;
      }

      String pattern = null;
      String termination = null;

      if (repeatsCheckBox.isSelected()) {
        if (!isAnyDaySelected()) {
          showError("Please select at least one day of the week for recurring event");
          return false;
        }

        pattern = buildPatternString();

        if (endsOnDateRadio.isSelected()) {
          Date endDateRecurring = (Date) endDateRecurringSpinner.getValue();
          LocalDate terminationDate = endDateRecurring.toInstant()
              .atZone(ZoneId.systemDefault())
              .toLocalDate();

          if (!terminationDate.isAfter(start.toLocalDate())) {
            showError("Recurring event end date must be after start date");
            return false;
          }

          termination = terminationDate.toString();
        } else {
          termination = occurrencesSpinner.getValue().toString();
        }
      }

      String location = locationField.getText().trim();
      String description = descriptionField.getText().trim();
      boolean isPrivate = isPrivateCheckBox.isSelected();
      boolean repeats = repeatsCheckBox.isSelected();

      eventInfo = new EventInfo(subject, start, end, location, description,
          isPrivate, repeats, pattern, termination);
      return true;

    } catch (Exception e) {
      showError("Invalid date/time: " + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if at least one day of week checkbox is selected.
   */
  private boolean isAnyDaySelected() {
    return mondayCheckBox.isSelected() || tuesdayCheckBox.isSelected() ||
        wednesdayCheckBox.isSelected() || thursdayCheckBox.isSelected() ||
        fridayCheckBox.isSelected() || saturdayCheckBox.isSelected() ||
        sundayCheckBox.isSelected();
  }

  /**
   * Builds the pattern string from selected day checkboxes.
   * Format: "M,T,W,Th,F,Sa,Su" (comma-separated)
   */
  private String buildPatternString() {
    StringBuilder pattern = new StringBuilder();

    if (mondayCheckBox.isSelected()) pattern.append("M");
    if (tuesdayCheckBox.isSelected()) pattern.append("T");
    if (wednesdayCheckBox.isSelected()) pattern.append("W");
    if (thursdayCheckBox.isSelected()) pattern.append("R");
    if (fridayCheckBox.isSelected()) pattern.append("F");
    if (saturdayCheckBox.isSelected()) pattern.append("S");
    if (sundayCheckBox.isSelected()) pattern.append("U");

    return pattern.toString();
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
   * Toggles the recurring event UI state.
   * Made public so controller can call it.
   */
  public void toggleRepeatsEvent() {
    boolean repeats = repeatsCheckBox.isSelected();
    recurringPanel.setVisible(repeats);

    // Repack the dialog to adjust size
    pack();
    setLocationRelativeTo(getOwner());
  }

  private JSpinner createDateSpinner(LocalDate initialDate) {
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

  private JPanel createTimePanel(int defaultHour, int defaultMinute) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

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

  private void setTimeComponentsEnabled(JPanel timePanel, boolean enabled) {
    for (Component comp : timePanel.getComponents()) {
      comp.setEnabled(enabled);
    }
  }

  private LocalDateTime generateDateTime(JSpinner dateSpinner, JSpinner hourSpinner,
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

  private void showError(String message) {
    JOptionPane.showMessageDialog(
        this,
        message,
        "Validation Error",
        JOptionPane.ERROR_MESSAGE
    );
  }

  // Getters and setters for controller
  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public EventInfo getEventInfo() {
    return eventInfo;
  }

  /**
   * Data class to hold event information from the dialog.
   * Now includes location, description, privacy, and recurring event fields.
   */
  public static class EventInfo {
    public final String subject;
    public final LocalDateTime start;
    public final LocalDateTime end;
    public final String location;
    public final String description;
    public final boolean isPrivate;
    public final boolean repeats;
    public final String pattern;      // e.g., "M,T,W,Th,F"
    public final String termination;  // e.g., "date:2025-12-31" or "occurrences:10"

    public EventInfo(String subject, LocalDateTime start, LocalDateTime end,
                     String location, String description, boolean isPrivate,
                     boolean repeats, String pattern, String termination) {
      this.subject = subject;
      this.start = start;
      this.end = end;
      this.location = location;
      this.description = description;
      this.isPrivate = isPrivate;
      this.repeats = repeats;
      this.pattern = pattern;
      this.termination = termination;
    }
  }
}