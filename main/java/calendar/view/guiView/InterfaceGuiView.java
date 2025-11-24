package calendar.view.guiView;

import calendar.controller.guicontroller.Features;
import calendar.view.guiView.adapter.IViewEvent;
import calendar.view.guiView.editEventsSameNameView.EditEventsSameNameDialog;
import calendar.view.guiView.editEventView.EditEventDialog;
import calendar.view.terminalView.InterfaceView;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
   * Updates the display to show the specified month and year.
   *
   * @param year the year to display
   * @param month the month to display (1-12)
   */
  void setMonthYear(int year, int month);

  /**
   * Sets the events to display on the month table view.
   *
   * @param events map of dates to list of events for that date
   */
  void setMonthEvents(Map<LocalDate, List<IViewEvent>> events);

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
   * Shows a create event dialog.
   * Called by the controller when the user wants to create a new event.
   *
   * @param date the initial date to populate in the dialog
   * @return EventInfo with event details, or null if cancelled
   */
  calendar.view.guiView.createEventView.CreateEventDialog.EventInfo showCreateEventDialog(
      java.time.LocalDate date);

  /**
   * Shows an edit event dialog.
   * Called by the controller when the user wants to edit an existing event.
   *
   * @param event View-only version of event
   * @return EventInfo with updated event details, or null if cancelled
   */
  EditEventDialog.EventInfo showEditEventDialog(IViewEvent event);

  /**
   * Shows an edit events with same name dialog.
   * Called by the controller when the user wants to edit all events with the same name.
   *
   * @return EditInfo with event name, property, and new value, or null if cancelled
   */
  EditEventsSameNameDialog.EditInfo showEditEventsSameNameDialog();

  void hideEventDetails();

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
   * Renders events for a specific day in the day view panel.
   *
   * @param date the date to display
   * @param events the events on that date
   */
  void renderDayEvents(LocalDate date, List<IViewEvent> events);

  /**
   * Switches to day view for a specific date.
   *
   * @param date the date to show
   */
  void showDayView(LocalDate date);

  /**
   * Refreshes the event details panel with updated event information.
   *
   * @param event the updated event to display
   */
  void refreshEventDetails(IViewEvent event);
}
