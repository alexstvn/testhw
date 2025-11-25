package calendar.model;

import java.time.LocalDateTime;

/**
 * Represents a validator to be run once the command is parsed to check if date time inputs
 * are valid once an event is retrieved.
 */
class EventValidator {

  /**
   * Validates and checks to make sure that the end is not before or equal to the start.
   *
   * @param start Start date/time of an event.
   * @param end   End date/time of an event.
   */
  public void validateEventTimes(LocalDateTime start, LocalDateTime end) {
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("End datetime cannot be before start datetime");
    }

    boolean sameDay = start.toLocalDate().equals(end.toLocalDate());
    // Checks for same time same day events
    if (sameDay && !end.toLocalTime().isAfter(start.toLocalTime())) {
      throw new IllegalArgumentException("End time must be after start time");
    }
  }

  /**
   * Helper to ensure updated date-time stays on the same date.
   *
   * @param original The original start date-time.
   * @param updated The updated date-time.
   * @param fieldName The name of the property being validated (for clear messages).
   * @throws IllegalArgumentException if the dates differ.
   */
  public void validateSameDate(LocalDateTime original, LocalDateTime updated, String fieldName) {
    if (!original.toLocalDate().equals(updated.toLocalDate())) {
      throw new IllegalArgumentException("Updated " + fieldName + " must be the same date");
    }
  }

  /**
   * Validates property edits for series operations by checking to see if its updated end or start
   * results in a different day.
   */
  public void validateSeriesPropertyEdit(LocalDateTime start, String property, String newValue) {
    if (property.equals("end") || property.equals("start")) {
      LocalDateTime newTime = LocalDateTime.parse(newValue);
      validateSameDate(start, newTime, property + "DateTime");
    }
  }
}
