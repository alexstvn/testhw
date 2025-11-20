package calendar.view.guiView.headerView;

import javax.swing.JButton;

/**
 * Interface for the header panel that displays calendar name and navigation.
 * Receives all calendar data from MainView (parent component).
 */
public interface InterfaceHeaderPanel {
  /**
   * Sets the calendar name to display.
   *
   * @param calendarName the name of the calendar
   */
  void setCalendarName(String calendarName);

  /**
   * Updates the calendar information (name and timezone).
   * Called by MainView when calendar data changes.
   *
   * @param calendarName the name of the calendar from MainView
   * @param timeZone the timezone ID from MainView
   */
  void updateCalendarInfo(String calendarName, String timeZone);

  /**
   * Sets the month and year to display.
   *
   * @param month the month (1-12)
   * @param year the year
   */
  void setMonthYear(int month, int year);

  /**
   * Gets the previous month button.
   *
   * @return the previous month button
   */
  JButton getPrevButton();

  /**
   * Gets the next month button.
   *
   * @return the next month button
   */
  JButton getNextButton();

  /**
   * Gets the edit calendar button.
   *
   * @return the edit calendar button
   */
  JButton getEditCalendarButton();

  /**
   * Gets the switch or add calendar button.
   * This button should be wired to fire action events by the parent view.
   *
   * @return the switch or add calendar button
   */
  JButton getSwitchOrAddCalendarButton();
}
