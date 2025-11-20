package calendar.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface for managing event series.
 */
public interface InterfaceSeries {
  /**
   * Gets all events in the series.
   *
   * @return list of events
   */
  List<InterfaceEvent> getSeries();

  /**
   * Returns pattern string associated with series.
   *
   * @return Pattern string associated with series.
   */
  String getPattern();

  /**
   * Edits all events in the series.
   *
   * @param property the property to edit
   * @param newValue the new value
   */
  void editSeries(String property, String newValue);

  /**
   * Edits events starting from a specific date/time.
   *
   * @param startDateTime the starting date/time
   * @param property      the property to edit
   * @param newValue      the new value
   */
  void editStartingFrom(LocalDateTime startDateTime, String property, String newValue);

  /**
   * Edits the start time for events starting from a specific date/time.
   *
   * @param startDateTime the starting date/time
   * @param newStartTime  the new start time
   * @return a new series with the updated events
   */
  InterfaceSeries editStartStartingFrom(LocalDateTime startDateTime, LocalDateTime newStartTime);

  /**
   * Removes an event from the series.
   *
   * @param event the event to remove
   */
  void removeEvent(InterfaceEvent event);

  /**
   * Finds an event in the series by subject and start time.
   *
   * @param subject       the event subject
   * @param startDateTime the start date/time
   * @return the event or null if not found
   */
  InterfaceEvent findEvent(String subject, LocalDateTime startDateTime);

  /**
   * Adjusts the time zone of the series.
   *
   * @param newTimeZone the new time zone
   */
  void adjustTimeZone(TimeZone newTimeZone);
}
