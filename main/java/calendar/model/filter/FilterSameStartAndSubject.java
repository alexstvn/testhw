package calendar.model.filter;

import calendar.model.InterfaceEvent;
import java.time.LocalDateTime;

/**
 * Filter that finds events of the same subject and start date/time.
 */
public class FilterSameStartAndSubject implements InterfaceFilter {
  private final String subject;
  private final LocalDateTime start;

  /**
   * Initializes filter using subject and DateTime that represents start date and time.
   *
   * @param subject Subject of the event to look for.
   * @param start Start DateTime of event.
   */
  public FilterSameStartAndSubject(String subject, LocalDateTime start) {
    this.subject = subject;
    this.start = start;
  }

  @Override
  public boolean evaluate(InterfaceEvent event) {
    return event.getSubject().equals(subject)
        && event.getStartDateTime().equals(start);
  }

}
