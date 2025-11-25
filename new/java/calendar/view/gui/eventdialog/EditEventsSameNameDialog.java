package calendar.view.gui.eventdialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for editing all events with the same name.
 * Provides a full form similar to EditEventDialog to edit multiple properties at once.
 * Pure UI component - collects user input and returns data.
 */
public class EditEventsSameNameDialog extends AbstractEventDialog {
  private JTextField eventNameField;
  private JTextField subjectField;
  private JTextField locationField;
  private JTextArea descriptionField;
  private JCheckBox isPrivateCheckBox;

  // Checkboxes to indicate which fields should be updated
  private JCheckBox updateSubjectCheckBox;
  private JCheckBox updateDateTimeCheckBox;
  private JCheckBox updateLocationCheckBox;
  private JCheckBox updateDescriptionCheckBox;
  private JCheckBox updatePrivacyCheckBox;
  private JCheckBox updateStartCheckBox;
  private JCheckBox updateEndCheckBox;

  /**
   * Creates an edit events with same name dialog.
   *
   * @param owner the parent frame
   */
  public EditEventsSameNameDialog(Frame owner) {
    super(owner, "Edit Events with Same Name", true);
    this.confirmed = false;
    initializeComponents();
    setSize(600, 700);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents() {
    setLayout(new BorderLayout(10, 10));

    final JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;

    // Event name to search for (required)
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    formPanel.add(new JLabel("Event Name to Find:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    eventNameField = new JTextField(30);
    formPanel.add(eventNameField, gbc);

    // Separator
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.gridwidth = 2;
    formPanel.add(new JLabel(" "), gbc); // Spacing

    // Instruction label
    gbc.gridy = row++;
    JLabel instructionLabel = new JLabel(
        "<html><i>Check the fields you want to update for all matching events:</i></html>");
    formPanel.add(instructionLabel, gbc);
    gbc.gridwidth = 1;

    // Subject with checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    updateSubjectCheckBox = new JCheckBox("Update Subject:");
    formPanel.add(updateSubjectCheckBox, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    subjectField = new JTextField(30);
    subjectField.setEnabled(false);
    formPanel.add(subjectField, gbc);

    // Wire checkbox to enable/disable field
    updateSubjectCheckBox.addActionListener(
        e -> subjectField.setEnabled(updateSubjectCheckBox.isSelected()));

    // DateTime section with checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.gridwidth = 2;
    gbc.weightx = 0.0;
    updateDateTimeCheckBox = new JCheckBox("Update Time:");
    formPanel.add(updateDateTimeCheckBox, gbc);
    gbc.gridwidth = 1;

    // All-day checkbox (sub-option under DateTime)
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    formPanel.add(new JLabel("  All Day Event:"), gbc);

    gbc.gridx = 1;
    allDayCheckBox = new JCheckBox();
    allDayCheckBox.setEnabled(false);
    formPanel.add(allDayCheckBox, gbc);

    // Start time
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    updateStartCheckBox = new JCheckBox("Update Start Time: ");
    updateStartCheckBox.setEnabled(false);
    formPanel.add(updateStartCheckBox, gbc);

    gbc.gridwidth = 1;
    gbc.gridx = 1;
    startTimePanel = createTimePanel(9, 0);
    startHourSpinner = (JSpinner) startTimePanel.getComponent(0);
    startMinuteSpinner = (JSpinner) startTimePanel.getComponent(2);
    setTimeComponentsEnabled(startTimePanel, false);
    formPanel.add(startTimePanel, gbc);

    updateStartCheckBox.addActionListener(e -> updateDateTimeFormState());

    // End time
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    updateEndCheckBox = new JCheckBox("Update End Time: ");
    updateEndCheckBox.setEnabled(false);
    formPanel.add(updateEndCheckBox, gbc);

    gbc.gridwidth = 1;
    gbc.gridx = 1;
    endTimePanel = createTimePanel(10, 0);
    endHourSpinner = (JSpinner) endTimePanel.getComponent(0);
    endMinuteSpinner = (JSpinner) endTimePanel.getComponent(2);
    setTimeComponentsEnabled(endTimePanel, false);
    formPanel.add(endTimePanel, gbc);

    // Wire DateTime checkbox
    updateEndCheckBox.addActionListener(e -> updateDateTimeFormState());
    updateDateTimeCheckBox.addActionListener(e -> updateDateTimeFormState());

    // Location with checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    updateLocationCheckBox = new JCheckBox("Update Location:");
    formPanel.add(updateLocationCheckBox, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    locationField = new JTextField(30);
    locationField.setEnabled(false);
    formPanel.add(locationField, gbc);

    updateLocationCheckBox.addActionListener(
        e -> locationField.setEnabled(updateLocationCheckBox.isSelected()));

    // Description with checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    updateDescriptionCheckBox = new JCheckBox("Update Description:");
    formPanel.add(updateDescriptionCheckBox, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    descriptionField = new JTextArea(4, 30);
    descriptionField.setLineWrap(true);
    descriptionField.setWrapStyleWord(true);
    descriptionField.setEnabled(false);

    JScrollPane scrollPane = new JScrollPane(descriptionField);
    formPanel.add(scrollPane, gbc);

    updateDescriptionCheckBox.addActionListener(
        e -> descriptionField.setEnabled(updateDescriptionCheckBox.isSelected()));

    // Privacy with checkbox
    gbc.gridx = 0;
    gbc.gridy = row++;
    gbc.weighty = 0.0;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    updatePrivacyCheckBox = new JCheckBox("Update Privacy:");
    formPanel.add(updatePrivacyCheckBox, gbc);

    gbc.gridx = 1;
    isPrivateCheckBox = new JCheckBox("Private Event");
    isPrivateCheckBox.setEnabled(false);
    formPanel.add(isPrivateCheckBox, gbc);

    updatePrivacyCheckBox.addActionListener(
        e -> isPrivateCheckBox.setEnabled(updatePrivacyCheckBox.isSelected()));

    // Add form panel to dialog
    add(formPanel, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    okButton = new JButton("Update All");
    okButton.setActionCommand(ACTION_OK);
    buttonPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(ACTION_CANCEL);
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);

    // Set up all-day toggle
    allDayCheckBox.setActionCommand(ACTION_TOGGLE_ALL_DAY);
  }

  private void updateDateTimeFormState() {
    boolean updateTime = updateDateTimeCheckBox.isSelected();
    boolean isAllDay = allDayCheckBox.isSelected();

    // --- Update Time OFF → disable EVERYTHING ---
    if (!updateTime) {
      allDayCheckBox.setEnabled(false);

      updateStartCheckBox.setEnabled(false);
      updateEndCheckBox.setEnabled(false);

      setTimeComponentsEnabled(startTimePanel, false);
      setTimeComponentsEnabled(endTimePanel, false);

      return;
    }

    // --- Update Time ON → All-day checkbox active ---
    allDayCheckBox.setEnabled(true);

    // --- All-day ON → shut down all time editing ---
    if (isAllDay) {
      updateStartCheckBox.setEnabled(false);
      updateEndCheckBox.setEnabled(false);

      updateStartCheckBox.setSelected(true);
      updateEndCheckBox.setSelected(true);

      setTimeComponentsEnabled(startTimePanel, false);
      setTimeComponentsEnabled(endTimePanel, false);
    } else {
      updateStartCheckBox.setEnabled(true);
      updateEndCheckBox.setEnabled(true);

      // Time spinners enabled ONLY when their checkbox is selected
      setTimeComponentsEnabled(startTimePanel, updateStartCheckBox.isSelected());
      setTimeComponentsEnabled(endTimePanel, updateEndCheckBox.isSelected());
    }
  }


  /**
   * Adds an action listener to the dialog's buttons.
   *
   * @param listener the action listener (typically from controller)
   */
  public void addActionListener(ActionListener listener) {
    super.addActionListener(listener);
    allDayCheckBox.addActionListener(e -> {
      toggleAllDayEvent();
      updateDateTimeFormState();
    });

  }

  /**
   * Validates user inputs and creates EventInfo if valid.
   *
   * @return true if validation passes, false otherwise
   */
  public boolean validateInputs() {
    String eventName = eventNameField.getText().trim();
    if (eventName.isEmpty()) {
      showError("Event name cannot be empty");
      return false;
    }

    // Check that at least one field is selected for update
    if (!updateSubjectCheckBox.isSelected() && !updateDateTimeCheckBox.isSelected()
        && !updateLocationCheckBox.isSelected() && !updateDescriptionCheckBox.isSelected()
        && !updatePrivacyCheckBox.isSelected()) {
      showError("Please select at least one field to update");
      return false;
    }

    try {
      // Build event info with only the selected fields
      EventFormData.EventFormBuilder builder = new EventFormData.EventFormBuilder();

      // Subject validation and setting
      if (updateSubjectCheckBox.isSelected()) {
        String subject = subjectField.getText().trim();
        if (subject.isEmpty()) {
          showError("New subject cannot be empty");
          return false;
        }
        builder.subject(subject);
      }

      // DateTime validation and setting
      if (updateDateTimeCheckBox.isSelected()) {
        LocalDateTime start = generateDateTime(startHourSpinner, startMinuteSpinner);
        LocalDateTime end = generateDateTime(endHourSpinner, endMinuteSpinner);

        if (!end.isAfter(start)) {
          showError("End date/time must be after start date/time");
          return false;
        }

        if (allDayCheckBox.isSelected() && !start.toLocalDate().equals(end.toLocalDate())) {
          showError("All-day events must start and end on the same day");
          return false;
        }

        builder.start(start).end(end);
      }

      // Location
      if (updateLocationCheckBox.isSelected()) {
        builder.location(locationField.getText().trim());
      }

      // Description
      if (updateDescriptionCheckBox.isSelected()) {
        builder.description(descriptionField.getText().trim());
      }

      // Privacy
      if (updatePrivacyCheckBox.isSelected()) {
        builder.isPrivate(isPrivateCheckBox.isSelected());
      }

      eventInfo = builder.build();
      return true;

    } catch (Exception e) {
      showError("Invalid input: " + e.getMessage());
      return false;
    }
  }

  /**
   * Toggles the all-day event UI state.
   */
  @Override
  public void toggleAllDayEvent() {
    if (!updateDateTimeCheckBox.isSelected()) {
      return;
    }

    if (allDayCheckBox.isSelected()) {
      startHourSpinner.setValue(ALL_DAY_START.getHour());
      startMinuteSpinner.setValue(ALL_DAY_START.getMinute());
      endHourSpinner.setValue(ALL_DAY_END.getHour());
      endMinuteSpinner.setValue(ALL_DAY_END.getMinute());
    }
  }

  private LocalDateTime generateDateTime(JSpinner hourSpinner, JSpinner minuteSpinner) {
    int hour = Integer.parseInt(hourSpinner.getValue().toString());
    int minute = Integer.parseInt(minuteSpinner.getValue().toString());

    LocalDate localDate = LocalDate.now();
    LocalTime time = LocalTime.of(hour, minute);

    return LocalDateTime.of(localDate, time);
  }

  public String getEventName() {
    return eventNameField.getText().trim();
  }

  /**
   * Retrieves whether or not the subject should be edited based on checkbox selection.
   *
   * @return True if should be edited, false if otherwise.
   */
  public boolean shouldUpdateSubject() {
    return updateSubjectCheckBox.isSelected();
  }

  /**
   * Retrieves whether or not the start time should be edited based on checkbox selection.
   *
   * @return True if should be edited, false if otherwise.
   */
  public boolean shouldUpdateStartTime() {
    return updateStartCheckBox.isSelected();
  }

  /**
   * Retrieves whether or not the end time should be edited based on checkbox selection.
   *
   * @return True if should be edited, false if otherwise.
   */
  public boolean shouldUpdateEndTime() {
    return updateEndCheckBox.isSelected();
  }
}