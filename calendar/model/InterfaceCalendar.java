package calendar.model;

import calendar.model.export.InterfaceExportFormat;
import calendar.model.filter.InterfaceFilter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface for calendar operations including event management and querying.
 */
public interface InterfaceCalendar {

  // ============================ ADD METHODS ============================

  /**
   * Gets the name of the calendar.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets the time zone of the calendar.
   *
   * @return the time zone
   */
  TimeZone getTimeZone();

  /**
   * Adds a single event to the calendar.
   *
   * @param eventRequest Contains subject, start, and end needed to edit a single event.
   */
  void addEvent(EventRequest eventRequest);


  /**
   * Adds a recurring event to the calendar.
   *
   * @param eventRequest Contains subject, start, end, repeating pattern,
   *                     and termination condition needed to edit a single event.
   */
  void addRecurringEvent(EventRequest eventRequest);


  // ============================ EDIT METHODS ============================

  /**
   * Edits a single event given a parameter object.
   *
   * @param eventRequest Contains subject, start, end, property
   *                     and new value needed to edit a single event.
   */
  void editEvent(EventRequest eventRequest);

  /**
   * Edits one or more events.
   *
   * @param eventRequest Contains subject, start, end, property
   *                     and new value needed to edit a single event.
   */
  void editEvents(EventRequest eventRequest);

  /**
   * Edits an event series.
   *
   * @param eventRequest Contains subject, start, end, property
   *                     and new value needed to edit a single event.
   */
  void editSeries(EventRequest eventRequest);

  // ============================ QUERY + OTHER METHODS ============================

  /**
   * Filters the calendar based on the specified criteria.
   *
   * @param filter filter object used to check if certain events meet the requirement of.
   * @return List of events that meet the requirements of {@Code filter}
   */
  List<InterfaceEvent> filter(InterfaceFilter filter);


  /**
   * Exports the calendar events as a list of Strings.
   */
  List<String> export(InterfaceExportFormat format);

  /**
   * Determined whether a user is busy at the specified date and time.
   *
   * @param dateTime Date and time to check status.
   * @return True if an event overlaps with that date and time, false if otherwise.
   */
  boolean isBusyAt(LocalDateTime dateTime);


  /**
   * Finds the series that contains an event with the given subject and start time.
   *
   * @param subject the event subject
   * @param start   the start date and time of the event
   * @return the series that contains the specified event, null if the event is not part of a series
   */
  InterfaceSeries findSeriesForEvent(String subject, LocalDateTime start);

  // =============== MULTI CAL OPERATIONS =================

  /**
   * Adjusts the time zone of the calendar.
   *
   * @param timeZone the new time zone
   * @return A calendar model with the adjusted time zone.
   */
  InterfaceCalendar adjustedTimeZone(TimeZone timeZone);

  /**
   * Renames the calendar.
   *
   * @param name the new name
   */
  void setName(String name);

}
