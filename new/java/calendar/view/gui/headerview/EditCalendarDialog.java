package calendar.view.gui.headerview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.TimeZone;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for editing an existing calendar's name and timezone.
 * Pure UI component - collects user input and returns data.
 */
public class EditCalendarDialog extends JDialog {
  private static final String ACTION_OK = "OK";
  private static final String ACTION_CANCEL = "CANCEL";

  private JTextField nameField;
  private JComboBox<String> timezoneComboBox;
  private JButton okButton;
  private JButton cancelButton;
  private String calendarName;
  private String selectedTimezone;
  private boolean confirmed;
  private final String currentName;
  private final List<String> existingCalendarNames;

  /**
   * Creates an edit calendar dialog.
   *
   * @param owner                 the parent frame
   * @param currentName           the current calendar name
   * @param currentTimezone       the current timezone
   * @param existingCalendarNames list of all existing calendar names for validation
   */
  public EditCalendarDialog(Frame owner, String currentName, TimeZone currentTimezone,
                            java.util.List<String> existingCalendarNames) {
    super(owner, "Edit Calendar", true);
    this.confirmed = false;
    this.currentName = currentName;
    this.existingCalendarNames = existingCalendarNames;
    initializeComponents(currentName, currentTimezone);
    setSize(400, 200);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents(String currentName, TimeZone currentTimezone) {
    setLayout(new BorderLayout(10, 10));

    // Form panel
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    // Calendar name
    formPanel.add(new JLabel("Calendar Name:"));
    nameField = new JTextField(currentName, 20);
    formPanel.add(nameField);

    // Timezone
    formPanel.add(new JLabel("Timezone:"));

    // Get common timezones
    String[] commonTimezones = {
        "America/New_York",
        "America/Chicago",
        "America/Denver",
        "America/Los_Angeles",
        "America/Phoenix",
        "Europe/London",
        "Europe/Paris",
        "Asia/Tokyo",
        "Asia/Shanghai",
        "Australia/Sydney",
        "UTC"
    };

    timezoneComboBox = new JComboBox<>(commonTimezones);
    timezoneComboBox.setSelectedItem(currentTimezone.getID());
    formPanel.add(timezoneComboBox);

    add(formPanel, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    okButton = new JButton("OK");
    buttonPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);

    // Wire all buttons
    wireButtons();
  }

  /**
   * Wires up all button action listeners.
   */
  private void wireButtons() {
    okButton.setActionCommand(ACTION_OK);
    okButton.addActionListener(this::actionPerformed);

    cancelButton.setActionCommand(ACTION_CANCEL);
    cancelButton.addActionListener(this::actionPerformed);
  }

  /**
   * Handles action events from buttons.
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
      default:
        // Unknown action - ignore
        break;
    }
  }

  private void handleOk() {
    calendarName = nameField.getText().trim();
    if (calendarName.isEmpty()) {
      JOptionPane.showMessageDialog(
          this,
          "Calendar name cannot be empty",
          "Validation Error",
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    // Check for duplicate name (only if name changed)
    if (!calendarName.equals(currentName) && existingCalendarNames.contains(calendarName)) {
      JOptionPane.showMessageDialog(
          this,
          "A calendar with the name \"" + calendarName
              + "\" already exists. Please choose a different name.",
          "Duplicate Calendar Name",
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    selectedTimezone = (String) timezoneComboBox.getSelectedItem();
    confirmed = true;
    dispose();
  }

  private void handleCancel() {
    confirmed = false;
    dispose();
  }

  public String getCalendarName() {
    return confirmed ? calendarName : null;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public TimeZone getTimezone() {
    return confirmed ? TimeZone.getTimeZone(selectedTimezone) : null;
  }
}
