package calendar.model.filter;

import calendar.model.InterfaceEvent;

/**
 * Filter that matches events by subject name only.
 * Used to find all events with the same name regardless of start/end times.
 */
public class FilterBySubject implements InterfaceFilter {
  private final String subject;

  /**
   * Creates a filter that matches events with the specified subject.
   *
   * @param subject the subject to match
   */
  public FilterBySubject(String subject) {
    this.subject = subject;
  }

  @Override
  public boolean evaluate(InterfaceEvent event) {
    return event.getSubject().equals(subject);
  }
}
