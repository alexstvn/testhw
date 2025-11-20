package calendar.controller.guicontroller;

import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface representing GUI-specific features and callbacks.
 * This interface defines all user interactions available through the graphical interface.
 */
public interface Features {

  // ============================ CALENDAR MANAGEMENT ============================

  void run();

  InterfaceCalendar getActiveCalendar();

  /**
   * Handles the request to switch calendars.
   * Shows dialog through view, processes selection, updates model and view.
   */
  void handleSwitchCalendar();

  /**
   * Handles the request to add a new calendar.
   * Shows dialog through view, creates calendar, updates model and view.
   */
  void handleAddCalendar();

  /**
   * Handles the request to edit the current calendar.
   * Shows dialog through view, updates calendar settings, updates model and view.
   */
  void handleEditCalendar();

  /**
   * Creates a new calendar with the specified name and timezone.
   *
   * @param calendarName name of the calendar to create
   * @param timezone timezone for the calendar
   */
  void createCalendar(String calendarName, TimeZone timezone);

  /**
   * Selects and activates a calendar by name.
   * This makes the specified calendar the active one for viewing and editing.
   *
   * @param calendarName name of the calendar to select
   */
  void selectCalendar(String calendarName);

  /**
   * Renames the specified calendar.
   *
   * @param oldName current name of the calendar
   * @param newName new name for the calendar
   */
  void renameCalendar(String oldName, String newName);

  /**
   * Changes the timezone of the specified calendar.
   *
   * @param calendarName name of the calendar
   * @param newTimezone new timezone to set
   */
  void changeCalendarTimezone(String calendarName, TimeZone newTimezone);

  // ============================ EVENT CREATION ============================

  /**
   * Creates a single, non-recurring event.
   *
   * @param subject name/title of the event
   * @param start start date and time of the event
   * @param end end date and time of the event
   */
  void createSingleEvent(String subject, LocalDateTime start, LocalDateTime end);

  /**
   * Creates an all-day event on a specific date.
   *
   * @param subject name/title of the event
   * @param date date of the all-day event
   */
  void createAllDayEvent(String subject, LocalDate date);

  /**
   * Creates a recurring event with a specified pattern and number of occurrences.
   *
   * @param subject name/title of the event
   * @param start start date and time of the first occurrence
   * @param end end date and time of the first occurrence
   * @param pattern repeating pattern (e.g., "MTW" for Mon/Tue/Wed, "FS" for Fri/Sat)
   * @param occurrences number of times the event should repeat
   */
  void createEventSeries(String subject, LocalDateTime start, LocalDateTime end,
                         String pattern, int occurrences);

  /**
   * Creates a recurring event with a specified pattern and end date.
   *
   * @param subject name/title of the event
   * @param start start date and time of the first occurrence
   * @param end end date and time of the first occurrence
   * @param pattern repeating pattern (e.g., "MTW" for Mon/Tue/Wed)
   * @param endDate date when the recurring event should stop
   */
  void createEventSeriesUntil(String subject, LocalDateTime start, LocalDateTime end,
                              String pattern, LocalDate endDate);

  // ============================ EVENT EDITING ============================

  /**
   * Edits a single specific event by its start time.
   *
   * @param subject current subject/title of the event to edit
   * @param originalStart original start time to identify the specific event
   * @param property property to modify ("subject", "start", "end", etc.)
   * @param newValue new value for the property
   */
  void editSingleEvent(String subject, LocalDateTime originalStart, String property,
                       String newValue);

  /**
   * Edits all events with a given subject name.
   *
   * @param subject subject/title of the events to edit
   * @param property property to modify ("subject", "start", "end", etc.)
   * @param newValue new value for the property
   */
  void editAllEventsWithSubject(String subject, String property, String newValue);

  /**
   * Edits all events with a given subject starting from a specific date.
   *
   * @param subject subject/title of the events to edit
   * @param fromDate start editing events from this date onwards
   * @param property property to modify
   * @param newValue new value for the property
   */
  void editEventsFromDate(String subject, LocalDate fromDate, String property, String newValue);

  /**
   * Edits a recurring event series.
   *
   * @param subject subject/title of the series to edit
   * @param property property to modify
   * @param newValue new value for the property
   */
  void editSeries(String subject, String property, String newValue);

  // ============================ NAVIGATION ============================

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

  // ============================ QUERY/VIEW OPERATIONS ============================

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
   * @return name of the active calendar, or null if none is active
   */
  String getActiveCalendarName();

  /**
   * Gets the timezone of the currently active calendar.
   *
   * @return timezone of the active calendar
   */
  TimeZone getActiveCalendarTimezone();
}
