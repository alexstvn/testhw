package calendar.view.gui;

import calendar.controller.guicontroller.Features;
import calendar.controller.guicontroller.InterfaceViewEvent;
import calendar.view.simple.InterfaceView;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Interface for the GUI view of the calendar application.
 */
public interface InterfaceGuiView extends InterfaceView {

  /**
   * Display the view to the user.
   */
  void display();

  /**
   * Add features/callbacks to the view.
   * Wires up the controller to handle user interactions.
   *
   * @param f the features/controller to add
   */
  void addFeatures(Features f);

  /**
   * Refresh the view with the latest calendar information.
   */
  void refresh();

  // ===================== DIALOG METHODS =====================

  /**
   * Shows a dialog to select a calendar from available options.
   *
   * @param availableCalendars list of calendar names to choose from
   */
  void showCalendarSelectorDialog(List<String> availableCalendars);

  /**
   * Shows dialog to edit events of a specified name.
   */
  void showEditEventsSameNameDialog();

  /**
   * Shows a dialog to add a new calendar.
   */
  void showAddCalendarDialog();

  /**
   * Shows a dialog to edit an existing calendar.
   *
   * @param calendarName    the current calendar name
   * @param currentTimezone the current timezone
   */
  void showEditCalendarDialog(String calendarName, TimeZone currentTimezone);

  /**
   * Shows an error dialog with the given title and message.
   *
   * @param title   the dialog title
   * @param message the error message to display
   */
  void showErrorDialog(String title, String message);

  /**
   * Shows a dialog to create an event on given date.
   *
   * @param date Date currently being viewed to pre-populate date fields.
   */
  void showCreateEventDialog(LocalDate date);

  /**
   * Shows dialog to edit a given read-only event.
   *
   * @param event Event specified to edit.
   */
  void showEditEventDialog(InterfaceViewEvent event);

  // ===================== RENDER METHODS =====================


  /**
   * Renders a month view with events.
   *
   * @param year         the year to display
   * @param month        the month to display (1-12)
   * @param calendarName the name of the active calendar
   * @param timezone     the timezone ID
   * @param events       map of dates to their events for the month
   */
  void renderMonth(int year, int month, String calendarName, String timezone,
                   Map<LocalDate, List<InterfaceViewEvent>> events);

  /**
   * Renders a day view with events for a specific date.
   *
   * @param date   the date to display
   * @param events list of events for that day
   */
  void renderDay(LocalDate date, List<InterfaceViewEvent> events,
                 Map<LocalDate, List<InterfaceViewEvent>> monthEvents);

  /**
   * Simple data class for calendar information.
   */
  class CalendarInfo {
    public final String name;
    public final TimeZone timezone;

    public CalendarInfo(String name, TimeZone timezone) {
      this.name = name;
      this.timezone = timezone;
    }
  }
}