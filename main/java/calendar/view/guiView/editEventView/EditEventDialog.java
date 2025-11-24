package calendar.view.guiView.editEventView;

import calendar.view.guiView.adapter.IViewEvent;
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
import javax.swing.BorderFactory;
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
 * Dialog for editing an existing event.
 * Pure UI component - collects user input and returns data.
 * Supports editing single events and recurring events with scope selection.
 */
public class EditEventDialog extends JDialog {
  public static final String ACTION_OK = "OK";
  public static final String ACTION_CANCEL = "CANCEL";
  public static final String ACTION_TOGGLE_ALL_DAY = "TOGGLE_ALL_DAY";

  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);

  private JTextField subjectField;
  private JTextField locationField;
  private JTextArea descriptionField;
  private JCheckBox isPrivateCheckBox;
  private JCheckBox allDayCheckBox;

  private JSpinner startDateSpinner;
  private JSpinner endDateSpinner;

  private JSpinner startHourSpinner;
  private JSpinner startMinuteSpinner;
  private JSpinner endHourSpinner;
  private JSpinner endMinuteSpinner;

  private JPanel startTimePanel;
  private JPanel endTimePanel;

  // Recurring event edit scope options
  private JPanel editScopePanel;
  private JRadioButton editSingleRadio;
  private JRadioButton editAllRadio;
  private JRadioButton editFollowingRadio;

  private JButton okButton;
  private JButton cancelButton;

  private boolean confirmed;
  private EventInfo eventInfo;
  private boolean isPartOfSeries;

  /**
   * Creates an edit event dialog pre-populated with existing event data.
   *
   * @param owner the parent frame
   * @param viewEvent the event to edit
   */
  public EditEventDialog(Frame owner, IViewEvent viewEvent) {
    super(owner, "Edit Event", true);
    this.confirmed = false;
    this.isPartOfSeries = viewEvent.isRepeating();
    initializeComponents(viewEvent);
    setSize(550, isPartOfSeries ? 650 : 550);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents(IViewEvent viewEvent) {
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
    subjectField = new JTextField(viewEvent.getSubject(), 30);
    formPanel.add(subjectField, gbc);

    // All-day checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    formPanel.add(new JLabel("All Day Event:"), gbc);

    gbc.gridx = 1;
    allDayCheckBox = new JCheckBox();
    LocalDateTime start = viewEvent.getStartDateTime();
    LocalDateTime end = viewEvent.getEndDateTime();
    boolean isAllDay = start.toLocalTime().equals(ALL_DAY_START)
        && end.toLocalTime().equals(ALL_DAY_END)
        && start.toLocalDate().equals(end.toLocalDate());
    allDayCheckBox.setSelected(isAllDay);
    formPanel.add(allDayCheckBox, gbc);

    // Start date
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("Start Date:"), gbc);

    gbc.gridx = 1;
    startDateSpinner = createDateSpinner(start.toLocalDate());
    formPanel.add(startDateSpinner, gbc);

    // Start time
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("Start Time:"), gbc);

    gbc.gridx = 1;
    startTimePanel = createTimePanel(start.getHour(), start.getMinute());
    startHourSpinner = (JSpinner) startTimePanel.getComponent(0);
    startMinuteSpinner = (JSpinner) startTimePanel.getComponent(2);
    formPanel.add(startTimePanel, gbc);

    // End date
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("End Date:"), gbc);

    gbc.gridx = 1;
    endDateSpinner = createDateSpinner(end.toLocalDate());
    formPanel.add(endDateSpinner, gbc);

    // End time
    gbc.gridx = 0;
    gbc.gridy = row++;
    formPanel.add(new JLabel("End Time:"), gbc);

    gbc.gridx = 1;
    endTimePanel = createTimePanel(end.getHour(), end.getMinute());
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
    locationField = new JTextField(viewEvent.getLocation(), 30);
    formPanel.add(locationField, gbc);

    // Description
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    formPanel.add(new JLabel("Description:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    descriptionField = new JTextArea(viewEvent.getDescription(), 4, 30);
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
    boolean isPrivate = viewEvent.isPrivate();
    isPrivateCheckBox.setSelected(isPrivate);
    formPanel.add(isPrivateCheckBox, gbc);

    // Edit scope panel (only for recurring events)
    if (isPartOfSeries) {
      gbc.gridx = 0;
      gbc.gridy = row++;
      gbc.gridwidth = 2;
      gbc.weightx = 1.0;
      gbc.weighty = 0.0;
      editScopePanel = createEditScopePanel();
      formPanel.add(editScopePanel, gbc);
    }

    // Add form panel to dialog
    add(formPanel, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    okButton = new JButton("Save");
    okButton.setActionCommand(ACTION_OK);
    buttonPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(ACTION_CANCEL);
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);

    // Set initial state for all-day
    allDayCheckBox.setActionCommand(ACTION_TOGGLE_ALL_DAY);
    if (isAllDay) {
      setTimeComponentsEnabled(startTimePanel, false);
      setTimeComponentsEnabled(endTimePanel, false);
    }
  }

  /**
   * Creates the panel for selecting edit scope (for recurring events).
   */
  private JPanel createEditScopePanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Edit Recurring Event"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    ButtonGroup editScopeGroup = new ButtonGroup();

    // Radio button: Edit only this event
    gbc.gridx = 0;
    gbc.gridy = 0;
    editSingleRadio = new JRadioButton("Edit only this event");
    editSingleRadio.setSelected(true); // Default selection
    editScopeGroup.add(editSingleRadio);
    panel.add(editSingleRadio, gbc);

    // Radio button: Edit all events in series
    gbc.gridy = 1;
    editAllRadio = new JRadioButton("Edit all events in series");
    editScopeGroup.add(editAllRadio);
    panel.add(editAllRadio, gbc);

    // Radio button: Edit this and following events
    gbc.gridy = 2;
    editFollowingRadio = new JRadioButton("Edit this and following events");
    editScopeGroup.add(editFollowingRadio);
    panel.add(editFollowingRadio, gbc);

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

      // Get additional fields
      String location = locationField.getText().trim();
      String description = descriptionField.getText().trim();
      boolean isPrivate = isPrivateCheckBox.isSelected();

      // Determine edit scope
      EventInfo.EditScope editScope = EventInfo.EditScope.SINGLE;
      if (isPartOfSeries) {
        if (editSingleRadio.isSelected()) {
          editScope = EventInfo.EditScope.SINGLE;
        } else if (editAllRadio.isSelected()) {
          editScope = EventInfo.EditScope.ALL;
        } else if (editFollowingRadio.isSelected()) {
          editScope = EventInfo.EditScope.THIS_AND_FOLLOWING;
        }
      }

      // Create event info with all fields
      eventInfo = new EventInfo(subject, start, end, location, description, isPrivate, editScope);
      return true;

    } catch (Exception e) {
      showError("Invalid date/time: " + e.getMessage());
      return false;
    }
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
   * Now includes location, description, privacy, and edit scope for recurring events.
   */
  public static class EventInfo {
    public final String subject;
    public final LocalDateTime start;
    public final LocalDateTime end;
    public final String location;
    public final String description;
    public final boolean isPrivate;
    public final EditScope editScope;

    /**
     * Enum representing the scope of editing for recurring events.
     */
    public enum EditScope {
      SINGLE,           // Edit only this event
      ALL,              // Edit all events in the series
      THIS_AND_FOLLOWING // Edit this event and all following events
    }

    public EventInfo(String subject, LocalDateTime start, LocalDateTime end,
                     String location, String description, boolean isPrivate,
                     EditScope editScope) {
      this.subject = subject;
      this.start = start;
      this.end = end;
      this.location = location;
      this.description = description;
      this.isPrivate = isPrivate;
      this.editScope = editScope;
    }
  }
}