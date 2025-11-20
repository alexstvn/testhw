package calendar.model.filter;

import calendar.model.InterfaceEvent;
import java.time.LocalDate;

/**
 * Filter that checks if an event overlaps with a specified date range.
 */
public class FilterByDateRange implements InterfaceFilter {
  private final LocalDate startDate;
  private final LocalDate endDate;

  /**
   * Initializes the filter with a date range.
   *
   * @param startDate the start date of the range (inclusive)
   * @param endDate the end date of the range (inclusive)
   */
  public FilterByDateRange(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Override
  public boolean evaluate(InterfaceEvent event) {
    LocalDate eventStart = event.getStartDateTime().toLocalDate();
    LocalDate eventEnd = event.getEndDateTime().toLocalDate();

    return !eventStart.isAfter(endDate) && !eventEnd.isBefore(startDate);
  }
}
