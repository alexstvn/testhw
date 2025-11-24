package calendar.view.guiView.editEventsSameNameView;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

/**
 * Dialog for editing all events with the same name.
 * Allows editing a specific property (subject, start, end) for all matching events.
 * Pure UI component - collects user input and returns data.
 */
public class EditEventsSameNameDialog extends JDialog {
  private static final String ACTION_OK = "OK";
  private static final String ACTION_CANCEL = "CANCEL";
  private static final String ACTION_PROPERTY_CHANGED = "PROPERTY_CHANGED";

  private JTextField eventNameField;
  private JComboBox<String> propertyComboBox;
  private JTextField newValueField;
  private JPanel dateTimePanel;
  private JSpinner dateSpinner;
  private JSpinner timeSpinner;
  private JButton okButton;
  private JButton cancelButton;
  private boolean confirmed;

  /**
   * Creates an edit events with same name dialog.
   *
   * @param owner the parent frame
   */
  public EditEventsSameNameDialog(Frame owner) {
    super(owner, "Edit Events with Same Name", true);
    this.confirmed = false;
    initializeComponents();
    setSize(450, 250);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents() {
    setLayout(new BorderLayout(10, 10));

    // Form panel
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Event name
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(new JLabel("Event Name:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    eventNameField = new JTextField(20);
    formPanel.add(eventNameField, gbc);

    // Property to edit
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Property to Edit:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    propertyComboBox = new JComboBox<>(new String[]{"subject", "start", "end"});
    formPanel.add(propertyComboBox, gbc);

    // New value (text field)
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    formPanel.add(new JLabel("New Value:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    newValueField = new JTextField(20);
    formPanel.add(newValueField, gbc);

    // Date/Time panel (hidden by default, shown when property is start/end)
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    dateTimePanel = createDateTimePanel();
    dateTimePanel.setVisible(false);
    formPanel.add(dateTimePanel, gbc);

    add(formPanel, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    okButton = new JButton("Save");
    buttonPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);

    // Wire all buttons
    wireButtons();
  }

  private JPanel createDateTimePanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Date/Time:"), gbc);

    gbc.gridx = 1;
    dateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    dateSpinner.setValue(new java.util.Date());
    panel.add(dateSpinner, gbc);

    gbc.gridx = 2;
    timeSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
    timeSpinner.setEditor(timeEditor);
    timeSpinner.setValue(new java.util.Date());
    panel.add(timeSpinner, gbc);

    return panel;
  }

  /**
   * Wires up all button and combobox action listeners.
   */
  private void wireButtons() {
    okButton.setActionCommand(ACTION_OK);
    okButton.addActionListener(this::actionPerformed);

    cancelButton.setActionCommand(ACTION_CANCEL);
    cancelButton.addActionListener(this::actionPerformed);

    propertyComboBox.setActionCommand(ACTION_PROPERTY_CHANGED);
    propertyComboBox.addActionListener(this::actionPerformed);
  }

  /**
   * Handles action events from buttons and combobox.
   *
   * @param e the action event
   */
  private void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    switch (command) {
      case ACTION_OK:
        handleOk();
        break;
      case ACTION_CANCEL:
        handleCancel();
        break;
      case ACTION_PROPERTY_CHANGED:
        handlePropertyChanged();
        break;
      default:
        // Unknown action - ignore
        break;
    }
  }

  private void handleOk() {
    if (eventNameField.getText().trim().isEmpty()) {
      javax.swing.JOptionPane.showMessageDialog(
          this,
          "Event name cannot be empty",
          "Validation Error",
          javax.swing.JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    String property = (String) propertyComboBox.getSelectedItem();
    if (property.equals("start") || property.equals("end")) {
      // For date/time properties, use the date/time pickers (newValue will be set in getEditInfo)
    } else {
      // For subject property, check that new value is not empty
      if (newValueField.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "New value cannot be empty",
            "Validation Error",
            javax.swing.JOptionPane.ERROR_MESSAGE
        );
        return;
      }
    }

    confirmed = true;
    dispose();
  }

  private void handleCancel() {
    confirmed = false;
    dispose();
  }

  private void handlePropertyChanged() {
    String selectedProperty = (String) propertyComboBox.getSelectedItem();
    boolean isDateTime = "start".equals(selectedProperty) || "end".equals(selectedProperty);

    dateTimePanel.setVisible(isDateTime);
    newValueField.setVisible(!isDateTime);
    pack();
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public EditInfo getEditInfo() {
    String eventName = eventNameField.getText().trim();
    String property = (String) propertyComboBox.getSelectedItem();
    String newValue;

    if (property.equals("start") || property.equals("end")) {
      // Get value from date/time pickers
      java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
      java.util.Date utilTime = (java.util.Date) timeSpinner.getValue();

      java.time.LocalDate date = new java.sql.Date(utilDate.getTime()).toLocalDate();
      java.time.LocalTime time = new java.sql.Time(utilTime.getTime()).toLocalTime();

      LocalDateTime dateTime = LocalDateTime.of(date, time);
      newValue = dateTime.toString();
    } else {
      newValue = newValueField.getText().trim();
    }

    return new EditInfo(eventName, property, newValue);
  }

  /**
   * Data class to hold edit information.
   */
  public static class EditInfo {
    public final String eventName;
    public final String property;
    public final String newValue;

    public EditInfo(String eventName, String property, String newValue) {
      this.eventName = eventName;
      this.property = property;
      this.newValue = newValue;
    }
  }
}
