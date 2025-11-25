package calendar.model.filter;

import calendar.model.InterfaceEvent;

/**
 * Filter object to filter events from Calendar.
 */
public interface InterfaceFilter {
  /**
   * Determines whether an event meets the filter conditions.
   *
   * @param event event object to check fields if it meets conditions.
   * @return true if the event meets the filter conditions, false if otherwise.
   */
  boolean evaluate(InterfaceEvent event);
}
