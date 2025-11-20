package calendar.view.guiView.headerView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.Month;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Header panel that displays calendar information and navigation controls.
 * Receives all calendar data from MainView (parent), which is the single source of truth.
 */
public class HeaderPanel extends JPanel implements InterfaceHeaderPanel {
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

  // Current month/year for navigation
  private int currentYear;
  private int currentMonth;

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
    vstack.add(Box.createVerticalStrut(20));
    vstack.add(createMonthNavigationPanel());
    vstack.add(Box.createVerticalStrut(20));

    add(vstack);
  }

  private JPanel createCalendarNamePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    calendarLabel = new JLabel(formatCalendarLabel(calendarName, timeZone));
    calendarLabel.setFont(new Font("Arial", Font.BOLD, 24));
    calendarLabel.setForeground(new Color(34, 139, 34));
    panel.add(calendarLabel);

    editCalendarButton = new JButton("✎");
    editCalendarButton.setFont(new Font("Arial", Font.PLAIN, 20));
    editCalendarButton.setBackground(Color.red);
    editCalendarButton.setFocusPainted(false);
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


  @Override
  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
    updateCalendarLabel();
  }

  /**
   * Updates the calendar information displayed in the header.
   * This method should be called by MainView when calendar data changes.
   *
   * @param calendarName the new calendar name from MainView
   * @param timeZone the new timezone from MainView
   */
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

  @Override
  public JButton getPrevButton() {
    return prevButton;
  }

  @Override
  public JButton getNextButton() {
    return nextButton;
  }

  @Override
  public JButton getEditCalendarButton() {
    return editCalendarButton;
  }

  @Override
  public JButton getSwitchOrAddCalendarButton() {
    return switchOrAddCalendarButton;
  }

  // Note: Calendar switching and month navigation now handled through action events
  // MainView wires buttons to fire ACTION_SWITCH_CALENDAR, ACTION_NEXT_MONTH, etc.
  // Controller handles all logic through handleSwitchCalendar(), nextMonth(), previousMonth()

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
