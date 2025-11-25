package calendar.view.gui.headerview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Dialog for selecting an existing calendar or creating a new one.
 * Pure UI component - shows list and returns user selection.
 */
public class CalendarSelectorDialog extends JDialog {
  private static final String ACTION_SWITCH = "SWITCH";
  private static final String ACTION_ADD = "ADD";
  private static final String ACTION_CANCEL = "CANCEL";

  private JList<String> calendarList;
  private DefaultListModel<String> listModel;
  private JButton switchButton;
  private JButton addButton;
  private JButton cancelButton;
  private String selectedCalendar;
  private boolean addNewCalendar;

  /**
   * Initializes select calendar dialog box.
   *
   * @param owner Frame that is calling calendar selector dialog.
   */
  public CalendarSelectorDialog(Frame owner) {
    super(owner, "Switch or Add Calendar", true);
    this.selectedCalendar = null;
    this.addNewCalendar = false;
    initializeComponents();
    setSize(400, 300);
    setLocationRelativeTo(owner);
  }

  private void initializeComponents() {
    setLayout(new BorderLayout(10, 10));

    // Calendar list
    listModel = new DefaultListModel<>();
    calendarList = new JList<>(listModel);
    calendarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(calendarList);
    add(scrollPane, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    addButton = new JButton("Add New Calendar");
    buttonPanel.add(addButton);

    switchButton = new JButton("Switch");
    buttonPanel.add(switchButton);

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
    addButton.setActionCommand(ACTION_ADD);
    addButton.addActionListener(this::actionPerformed);

    switchButton.setActionCommand(ACTION_SWITCH);
    switchButton.addActionListener(this::actionPerformed);

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
      case ACTION_SWITCH:
        handleSwitch();
        break;
      case ACTION_ADD:
        handleAdd();
        break;
      case ACTION_CANCEL:
        handleCancel();
        break;
      default:
        break;
    }
  }

  private void handleSwitch() {
    selectedCalendar = calendarList.getSelectedValue();
    if (selectedCalendar != null) {
      dispose();
    }
  }

  private void handleAdd() {
    addNewCalendar = true;
    dispose();
  }

  private void handleCancel() {
    selectedCalendar = null;
    dispose();
  }

  /**
   * Resets the list of calendar names for form selection.
   *
   * @param calendars Array of calendar names.
   */
  public void setCalendars(String[] calendars) {
    listModel.clear();
    for (String calendar : calendars) {
      listModel.addElement(calendar);
    }
  }

  public String getSelectedCalendar() {
    return selectedCalendar;
  }

  public boolean isAddNewCalendar() {
    return addNewCalendar;
  }
}
