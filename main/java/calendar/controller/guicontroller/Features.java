package calendar.controller.guicontroller;

import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceEvent;
import calendar.view.guiView.adapter.IViewEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface defining the controller features for the GUI calendar application.
 * Follows MVC pattern: all business logic resides in the controller,
 * while the view delegates user actions through these methods.
 */
public interface Features {

  // MARK: REFRESH STATE
  /**
   * Runs the calendar application.
   * Initializes the view and loads initial data.
   */
  void run();

  // MARK: BUTTON HANDLERS

  /**
   * Handles the switch calendar button click.
   * Shows dialog through view, processes selection, updates model and view.
   */
  void handleSwitchCalendar();

  /**
   * Handles the add calendar button click.
   * Shows dialog through view, creates calendar, updates model and view.
   */
  void handleAddCalendar();

  /**
   * Handles the edit calendar button click.
   * Shows dialog through view, updates calendar settings, updates model and view.
   */
  void handleEditCalendar();

  /**
   * Handles the add single event button click.
   * Shows dialog through view, creates event, updates model and view.
   */
  void handleAddSingleEvent();

  /**
   * Handles the add event series button click.
   * Shows dialog through view, creates recurring events, updates model and view.
   */
  void handleAddEventSeries();

  /**
   * Handles the edit events with same name button click.
   * Shows dialog through view, updates multiple events, updates model and view.
   */
  void handleEditEventsWithSameName();

  /**
   * Handles editing an event when clicked in the day view.
   * Shows edit dialog through view, updates event, updates model and view.
   *
   * @param event the view event to edit
   */
  void handleEditEvent(IViewEvent event);

  // MARK: NAVIGATION

  /**
   * Loads events for the currently displayed month in the calendar.
   */
  void loadEventsForMonth();

  /**
   * Navigates to the next month in the calendar view.
   */
  void nextMonth();

  /**
   * Navigates to the previous month in the calendar view.
   */
  void previousMonth();

  /**
   * Selects a specific day to view its events.
   *
   * @param date the date to select and view
   */
  void selectDay(LocalDate date);

  // MARK: QUERY METHODS

  /**
   * Gets the currently active calendar.
   *
   * @return the active calendar, or null if none is active
   */
  InterfaceCalendar getActiveCalendar();

  /**
   * Gets all events scheduled on a specific date in the active calendar.
   *
   * @param date the date to query
   * @return list of events on that date
   */
  List<InterfaceEvent> getEventsOnDay(LocalDate date);

  /**
   * Gets all calendar names in the system.
   *
   * @return list of calendar names
   */
  List<String> getAllCalendarNames();

  /**
   * Gets the name of the currently active calendar.
   *
   * @return name of the active calendar, or empty string if none is active
   */
  String getActiveCalendarName();

  /**
   * Gets the timezone of the currently active calendar.
   *
   * @return timezone of the active calendar
   */
  TimeZone getActiveCalendarTimezone();
}
