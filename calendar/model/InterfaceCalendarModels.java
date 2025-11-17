package calendar.model;

import java.util.TimeZone;

/**
 * Interface for managing calendar models.
 */
public interface InterfaceCalendarModels {
  /**
   * Adds a calendar to the model.
   *
   * @param name name of the calendar.
   * @param model calendar model.
   * @param timezone timezone of the calendar.
   */
  void add(String name, InterfaceCalendar model, TimeZone timezone);

  /**
   * Gets a calendar by name.
   *
   * @param calendarName name of the calendar.
   * @return calendar model.
   */
  InterfaceCalendar get(String calendarName);

  /**
   * Renames a calendar.
   *
   * @param oldName old name of the calendar.
   * @param newName new name of the calendar.
   */
  void setName(String oldName, String newName);

  /**
   * Gets the time zone of a calendar.
   *
   * @param calendarName name of the calendar.
   * @return time zone of the calendar.
   */
  TimeZone getTimeZone(String calendarName);

  /**
   * Gets the active calendar.
   *
   * @return active calendar.
   */
  InterfaceCalendar getActiveCalendar();

  /**
   * Sets the active calendar.
   *
   * @param name name of the calendar.
   */
  void setActiveCalendar(String name);

  /**
   * Sets the time zone of a calendar.
   *
   * @param name name of the calendar.
   * @param timeZone time zone of the calendar.
   */
  void setTimeZone(String name, TimeZone timeZone);
}
