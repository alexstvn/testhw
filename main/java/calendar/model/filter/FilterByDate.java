package calendar.model.filter;

import calendar.model.InterfaceEvent;
import java.time.LocalDate;

/**
 * Determines whether an event is happening on or during this date.
 */
public class FilterByDate implements InterfaceFilter {
  private final LocalDate date;

  /**
   * Initialized the filter with a given date.
   *
   * @param date Date to check whether events fall on this date.
   */
  public FilterByDate(LocalDate date) {
    this.date = date;
  }

  @Override
  public boolean evaluate(InterfaceEvent event) {
    LocalDate start = event.getStartDateTime().toLocalDate();
    LocalDate end = event.getEndDateTime().toLocalDate();
    return !date.isBefore(start) && !date.isAfter(end);
  }
}
