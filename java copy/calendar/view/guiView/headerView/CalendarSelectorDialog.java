package calendar.view.guiView.headerView;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
  private JList<String> calendarList;
  private DefaultListModel<String> listModel;
  private JButton switchButton;
  private JButton addButton;
  private JButton cancelButton;
  private String selectedCalendar;
  private boolean addNewCalendar;

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
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addNewCalendar = true;
        dispose();
      }
    });
    buttonPanel.add(addButton);

    switchButton = new JButton("Switch");
    switchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedCalendar = calendarList.getSelectedValue();
        if (selectedCalendar != null) {
          dispose();
        }
      }
    });
    buttonPanel.add(switchButton);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedCalendar = null;
        dispose();
      }
    });
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

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
