package calendar.view.guiView.headerView;

import calendar.controller.guicontroller.Features;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Month;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Header panel that displays calendar information and navigation controls.
 * Handles its own button events and delegates to the controller.
 */
public class HeaderPanel extends JPanel implements InterfaceHeaderPanel {
  // Action command constants
  private static final String ACTION_SWITCH_CALENDAR = "SWITCH_CALENDAR";
  private static final String ACTION_EDIT_CALENDAR = "EDIT_CALENDAR";
  private static final String ACTION_NEXT_MONTH = "NEXT_MONTH";
  private static final String ACTION_PREV_MONTH = "PREV_MONTH";
  private static final String ACTION_EDIT_EVENTS_SAME_NAME = "EDIT_EVENTS_SAME_NAME";

  // Calendar info - received from MainView
  private String calendarName;
  private String timeZone;

  // UI components
  private JLabel calendarLabel;
  private JLabel monthLabel;
  private JButton prevButton;
  private JButton nextButton;
  private JButton editCalendarButton;
  private JButton switchOrAddCalendarButton;
  private JButton editEventsSameNameButton;

  // Current month/year for navigation
  private int currentYear;
  private int currentMonth;

  // Controller reference
  private Features controller;
  private Runnable refreshCallback;

  /**
   * Creates a new HeaderPanel with the specified calendar information.
   * Data is provided by MainView, which manages all calendar state.
   *
   * @param calendarName the active calendar name from MainView
   * @param timeZone the active calendar timezone from MainView
   */
  public HeaderPanel(String calendarName, String timeZone) {
    this.timeZone = timeZone;
    this.calendarName = calendarName;
    LocalDate now = LocalDate.now();
    this.currentYear = now.getYear();
    this.currentMonth = now.getMonthValue();
    initializeComponents();
    // Note: Calendar switcher is set up by MainView after construction
  }

  private void initializeComponents() {
    setLayout(new BorderLayout());
    Box vstack = Box.createVerticalBox();

    vstack.add(Box.createVerticalStrut(20));
    vstack.add(createCalendarNamePanel());
    vstack.add(createSwitchCalendarButton());
    vstack.add(Box.createVerticalStrut(10));
    vstack.add(createEditEventsSameNameButton());
    vstack.add(Box.createVerticalStrut(20));
    vstack.add(createMonthNavigationPanel());
    vstack.add(Box.createVerticalStrut(20));

    add(vstack);
  }

  /**
   * Sets the controller and wires up all button action listeners.
   *
   * @param controller the controller to handle button events
   * @param refreshCallback callback to refresh MainView state after calendar operations
   */
  public void setController(Features controller, Runnable refreshCallback) {
    this.controller = controller;
    this.refreshCallback = refreshCallback;
    wireButtons();
  }

  private void wireButtons() {
    // Wire switch calendar button
    switchOrAddCalendarButton.setActionCommand(ACTION_SWITCH_CALENDAR);
    switchOrAddCalendarButton.addActionListener(this::actionPerformed);

    // Wire edit calendar button
    editCalendarButton.setActionCommand(ACTION_EDIT_CALENDAR);
    editCalendarButton.addActionListener(this::actionPerformed);

    // Wire edit events same name button
    editEventsSameNameButton.setActionCommand(ACTION_EDIT_EVENTS_SAME_NAME);
    editEventsSameNameButton.addActionListener(this::actionPerformed);

    // Wire navigation buttons
    prevButton.setActionCommand(ACTION_PREV_MONTH);
    prevButton.addActionListener(this::actionPerformed);

    nextButton.setActionCommand(ACTION_NEXT_MONTH);
    nextButton.addActionListener(this::actionPerformed);
  }

  private void actionPerformed(ActionEvent e) {
    if (controller == null) {
      return;
    }

    String command = e.getActionCommand();

    switch (command) {
      case ACTION_SWITCH_CALENDAR:
        controller.handleSwitchCalendar();
        if (refreshCallback != null) {
          refreshCallback.run();
        }
        break;
      case ACTION_EDIT_CALENDAR:
        controller.handleEditCalendar();
        if (refreshCallback != null) {
          refreshCallback.run();
        }
        break;
      case ACTION_EDIT_EVENTS_SAME_NAME:
        controller.handleEditEventsWithSameName();
        break;
      case ACTION_NEXT_MONTH:
        controller.nextMonth();
        break;
      case ACTION_PREV_MONTH:
        controller.previousMonth();
        break;
      default:
        // Unknown action - ignore
        break;
    }
  }

  private JPanel createCalendarNamePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    calendarLabel = new JLabel(formatCalendarLabel(calendarName, timeZone));
    calendarLabel.setFont(new Font("Arial", Font.BOLD, 24));
    calendarLabel.setForeground(new Color(34, 139, 34));
    panel.add(calendarLabel);

    editCalendarButton = new JButton("✎ edit calendar");
    editCalendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
    editCalendarButton.setBorderPainted(false);
    editCalendarButton.setFocusPainted(false);
    editCalendarButton.setContentAreaFilled(false);
    editCalendarButton.setForeground(Color.gray);
    panel.add(editCalendarButton);

    return panel;
  }

  private JButton createSwitchCalendarButton() {
    switchOrAddCalendarButton = new JButton("Switch or Add Calendar");
    switchOrAddCalendarButton.setMargin(new Insets(10, 20, 10, 20));
    switchOrAddCalendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
    switchOrAddCalendarButton.setFocusPainted(false);
    return switchOrAddCalendarButton;
  }

  private JButton createEditEventsSameNameButton() {
    editEventsSameNameButton = new JButton("Edit Events with Same Name");
    editEventsSameNameButton.setMargin(new Insets(10, 20, 10, 20));
    editEventsSameNameButton.setFont(new Font("Arial", Font.PLAIN, 14));
    editEventsSameNameButton.setFocusPainted(false);
    return editEventsSameNameButton;
  }

  private JPanel createMonthNavigationPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    prevButton = new JButton("<");
    prevButton.setFont(new Font("Arial", Font.PLAIN, 12));
    prevButton.setFocusPainted(false);
    panel.add(prevButton);

    monthLabel = new JLabel(formatMonthYearLabel(currentMonth, currentYear));
    monthLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    panel.add(monthLabel);

    nextButton = new JButton(">");
    nextButton.setFont(new Font("Arial", Font.PLAIN, 12));
    nextButton.setFocusPainted(false);
    panel.add(nextButton);

    return panel;
  }


  /**
   * Updates the calendar information displayed in the header.
   * This method should be called by MainView when calendar data changes.
   *
   * @param calendarName the new calendar name from MainView
   * @param timeZone the new timezone from MainView
   */
  @Override
  public void updateCalendarInfo(String calendarName, String timeZone) {
    this.calendarName = calendarName;
    this.timeZone = timeZone;
    updateCalendarLabel();
  }

  @Override
  public void setMonthYear(int month, int year) {
    this.currentMonth = month;
    this.currentYear = year;
    updateMonthLabel();
  }

  private void updateCalendarLabel() {
    calendarLabel.setText(formatCalendarLabel(calendarName, timeZone));
  }

  private void updateMonthLabel() {
    monthLabel.setText(formatMonthYearLabel(currentMonth, currentYear));
  }

  private String formatCalendarLabel(String name, String timezone) {
    return "Calendar: " + name + " (" + timezone + ")";
  }

  private String formatMonthYearLabel(int month, int year) {
    String monthName = Month.of(month).name();
    return "<html><b>" + monthName + "</b> " + year + "</html>";
  }
}
