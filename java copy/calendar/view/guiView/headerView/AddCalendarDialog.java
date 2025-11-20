package calendar.view.guiView.headerView;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimeZone;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for adding a new calendar with name and timezone.
 * Pure UI component - collects user input and returns data.
 */
public class AddCalendarDialog extends JDialog {
  private JTextField nameField;
  private JComboBox<String> timezoneComboBox;
  private JButton okButton;
  private JButton cancelButton;
  private String calendarName;
  private String selectedTimezone;
  private boolean confirmed;

  public AddCalendarDialog(Frame owner) {
    super(owner, "Add New Calendar", true);
    this.confirmed = false;
    initializeComponents();
    setSize(400, 200);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents() {
    setLayout(new BorderLayout(10, 10));

    // Form panel
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    // Calendar name
    formPanel.add(new JLabel("Calendar Name:"));
    nameField = new JTextField(20);
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
    timezoneComboBox.setSelectedItem("America/New_York"); // Default
    formPanel.add(timezoneComboBox);

    add(formPanel, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        calendarName = nameField.getText().trim();
        if (calendarName.isEmpty()) {
          JOptionPane.showMessageDialog(
              AddCalendarDialog.this,
              "Calendar name cannot be empty",
              "Validation Error",
              JOptionPane.ERROR_MESSAGE
          );
          return;
        }
        selectedTimezone = (String) timezoneComboBox.getSelectedItem();
        confirmed = true;
        dispose();
      }
    });
    buttonPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        confirmed = false;
        dispose();
      }
    });
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  public String getCalendarName() {
    return confirmed ? calendarName : null;
  }

  public String getSelectedTimezone() {
    return confirmed ? selectedTimezone : null;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public TimeZone getTimezone() {
    return confirmed ? TimeZone.getTimeZone(selectedTimezone) : null;
  }
}
