package calendar.model.filter;

import calendar.model.InterfaceEvent;
import java.time.LocalDateTime;

/**
 * Evaluates if an event partly or completely lies in the given interval.
 */
public class FilterByDateTime implements InterfaceFilter {
  private final LocalDateTime start;
  private final LocalDateTime end;

  /**
   * Initializes filter with date time range.
   *
   * @param start Inclusive start date and time range query.
   * @param end   Inclusive end date and time of range query.
   */
  public FilterByDateTime(LocalDateTime start, LocalDateTime end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Initializes a moment-based filter (single instant).
   *
   * @param dateTime the moment to check (treated as range start == end)
   */
  public FilterByDateTime(LocalDateTime dateTime) {
    this(dateTime, dateTime);
  }


  @Override
  public boolean evaluate(InterfaceEvent event) {
    return !event.getEndDateTime().isBefore(start) && !event.getStartDateTime().isAfter(end);
  }
}
