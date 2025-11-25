package calendar.view.gui.eventdialog;

import calendar.controller.guicontroller.InterfaceViewEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for editing an existing event.
 * Pure UI component - collects user input and returns data.
 * Supports editing single events and recurring events with scope selection.
 */
public class EditEventDialog extends AbstractEventDialog {
  private JTextField subjectField;
  private JTextField locationField;
  private JTextArea descriptionField;
  private JCheckBox isPrivateCheckBox;

  private JRadioButton editAllRadio;
  private JRadioButton editFollowingRadio;

  private EditScope scope;
  private final boolean isPartOfSeries;

  /**
   * Creates an edit event dialog pre-populated with existing event data.
   *
   * @param owner     the parent frame
   * @param viewEvent the event to edit
   */
  public EditEventDialog(Frame owner, InterfaceViewEvent viewEvent) {
    super(owner, "Edit Event", true);
    this.confirmed = false;
    this.isPartOfSeries = viewEvent.repeats();
    initializeComponents(viewEvent);
    setSize(550, isPartOfSeries ? 650 : 550);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents(InterfaceViewEvent viewEvent) {
    setLayout(new BorderLayout(10, 10));

    final JPanel formPanel = new JPanel(new GridBagLayout());
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
      gbc.gridwidth = 2;
      gbc.weightx = 1.0;
      gbc.weighty = 0.0;
      // Recurring event edit scope options
      JPanel editScopePanel = createEditScopePanel();
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

    final ButtonGroup editScopeGroup = new ButtonGroup();

    // Radio button: Edit only this event
    gbc.gridx = 0;
    gbc.gridy = 0;
    JRadioButton editSingleRadio = new JRadioButton("Edit only this event");
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
    super.addActionListener(listener);
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
      LocalDateTime start =
          generateDateTime(startDateSpinner, startHourSpinner, startMinuteSpinner);
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
      scope = EditScope.SINGLE;
      if (isPartOfSeries) {
        if (editAllRadio.isSelected()) {
          scope = EditScope.ALL;
        } else if (editFollowingRadio.isSelected()) {
          scope = EditScope.THIS_AND_FOLLOWING;
        }
      }

      // Create event info with all fields
      eventInfo = new EventFormData.EventFormBuilder()
          .subject(subject)
          .start(start)
          .end(end)
          .location(location)
          .description(description)
          .isPrivate(isPrivate)
          .build();

      return true;

    } catch (Exception e) {
      showError("Invalid date/time: " + e.getMessage());
      return false;
    }
  }

  public EditScope getScope() {
    return scope;
  }
}