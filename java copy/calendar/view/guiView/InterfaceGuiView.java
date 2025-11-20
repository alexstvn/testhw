package calendar.view.guiView;

import calendar.controller.guicontroller.Features;
import calendar.view.terminalView.InterfaceView;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface for GUI view.
 */
public interface InterfaceGuiView extends InterfaceView {
  /**
   * Displays the GUI.
   */
  void display();

  /**
   * Adds features (controller callbacks) to the view.
   * This hooks up the controller to respond to user interactions.
   *
   * @param f feature implementation (controller) to handle user actions
   */
  void addFeatures(Features f);

  /**
   * Refreshes the GUI.
   */
  void refresh();

  /**
   * Handles an action event triggered by user interaction in the GUI.
   *
   * @param e the ActionEvent object containing details about the action event
   */
  void actionPerfomed(ActionEvent e);

  /**
   * Sets up the month navigation callback that will be invoked when the user
   * navigates to a different month.
   *
   * @param callback the callback to be invoked with the new year and month
   */
  void setupMonthNavigation(MonthNavigationCallback callback);

  /**
   * Updates the display to show the specified month and year.
   *
   * @param year the year to display
   * @param month the month to display (1-12)
   */
  void setMonthYear(int year, int month);

  // ===================== DIALOG METHODS (for Controller to request user input) =====================

  /**
   * Shows a calendar selector dialog and returns the user's selection.
   * Called by the controller when it needs the user to select a calendar.
   *
   * @param availableCalendars list of available calendar names to show
   * @return the selected calendar name, or null if cancelled
   */
  String showCalendarSelectorDialog(List<String> availableCalendars);

  /**
   * Shows an add calendar dialog and returns the calendar information.
   * Called by the controller when it needs the user to create a new calendar.
   *
   * @return CalendarInfo with name and timezone, or null if cancelled
   */
  CalendarInfo showAddCalendarDialog();

  /**
   * Shows an edit calendar dialog for the specified calendar.
   * Called by the controller when it needs the user to edit calendar settings.
   *
   * @param calendarName the calendar to edit
   * @param currentTimezone the current timezone of the calendar
   * @return CalendarInfo with updated settings, or null if cancelled
   */
  CalendarInfo showEditCalendarDialog(String calendarName, TimeZone currentTimezone);

  /**
   * Data class to hold calendar information from dialogs.
   */
  class CalendarInfo {
    public final String name;
    public final TimeZone timezone;

    public CalendarInfo(String name, TimeZone timezone) {
      this.name = name;
      this.timezone = timezone;
    }
  }

  /**
   * Callback interface for month navigation events.
   */
  interface MonthNavigationCallback {
    void onMonthChanged(int year, int month);
  }
}
