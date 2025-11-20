package calendar.model.filter;

import calendar.model.InterfaceEvent;
import java.time.LocalDateTime;

/**
 * Searches for an exact event (determined by the subject and start/end of the event).
 */
public class FilterExactEvent implements InterfaceFilter {
  private final InterfaceFilter sameSubjectAndStart;
  private final LocalDateTime end;

  /**
   * Initializes filter by requiring subject and DateTime objects start and end.
   *
   * @param subject Subject of the specified event.
   * @param start Start date and time of the event.
   * @param end End date and time of the event.
   */
  public FilterExactEvent(String subject, LocalDateTime start, LocalDateTime end) {
    this.sameSubjectAndStart = new FilterSameStartAndSubject(subject, start);
    this.end = end;
  }

  @Override
  public boolean evaluate(InterfaceEvent event) {
    return sameSubjectAndStart.evaluate(event)
        && event.getEndDateTime().equals(end);
  }
}
