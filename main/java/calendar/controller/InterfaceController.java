package calendar.controller;

import calendar.model.InterfaceCalendar;

/**
 * Interface for calendar controller operations.
 */
public interface InterfaceController {
  /**
   * Starts the controller main loop.
   */
  void run();

  /**
   * Retrieves the active working calendar. If no active calendar has been set, return null.
   *
   * @return The active working calendar, otherwise is null.
   */
  InterfaceCalendar getActiveCalendar();
}
