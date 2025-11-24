package calendar.view.guiView.headerView;

/**
 * Interface for the header panel that displays calendar name and navigation.
 * Receives all calendar data from MainView (parent component).
 */
public interface InterfaceHeaderPanel {
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
}
