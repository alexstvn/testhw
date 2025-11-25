package calendar.model;

import calendar.model.export.InterfaceExportFormat;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Interface for individual calendar events.
 */
public interface InterfaceEvent {
  /**
   * Gets the event subject.
   *
   * @return the subject
   */
  String getSubject();

  /**
   * Gets the event start date and time.
   *
   * @return the start date/time
   */
  LocalDateTime getStartDateTime();

  /**
   * Gets the event end date and time.
   *
   * @return the end date/time
   */
  LocalDateTime getEndDateTime();

  /**
   * Gets the event description.
   *
   * @return the description
   */
  String getDescription();

  /**
   * Gets the event status.
   *
   * @return the status
   */
  EventStatus getStatus();

  /**
   * Gets the event location.
   *
   * @return the location
   */
  String getLocation();

  /**
   * Sets a property of the event.
   *
   * @param property the property name
   * @param value    the new value
   */
  void setProperty(String property, String value);

  /**
   * Formats the event as a CSV entry.
   *
   * @return A single event CSV line (without headers).
   */
  String export(InterfaceExportFormat exportFormat);

  /**
   * Gets the time zone of the event.
   *
   * @return the time zone
   */
  TimeZone getTimeZone();

  /**
   * Adjusts the time zone of the event.
   *
   * @param newTimeZone the new time zone
   */
  void adjustTimeZone(TimeZone newTimeZone);
}
