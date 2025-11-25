package calendar.controller.guicontroller;

import calendar.view.gui.eventdialog.EditScope;
import calendar.view.gui.eventdialog.EventFormData;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface defining the controller features for the GUI calendar application.
 * Follows MVC pattern: all business logic resides in the controller,
 * while the view delegates user actions through these methods.
 */
public interface Features {
  /**
   * Handles the switch calendar button click.
   * Shows dialog through view, processes selection, updates model and view.
   */
  void handleSwitchCalendar(String name);

  /**
   * Handles the add calendar button click.
   * Shows dialog through view, creates calendar, updates model and view.
   */
  void handleAddCalendar(String name, TimeZone timeZone);

  /**
   * Handles the edit calendar button click.
   * Shows dialog through view, updates calendar settings, updates model and view.
   */
  void handleEditCalendar(String name, TimeZone timeZone);

  /**
   * Handles the add single event button click.
   * Shows dialog through view, creates event, updates model and view.
   */
  void handleAddEvent(EventFormData newEvent);

  /**
   * Handles the edit events with same name button click.
   * Shows dialog through view, updates multiple events, updates model and view.
   */
  void handleEditEventsWithSameName(String eventName, EventFormData eventFormData,
                                    boolean updateSubject, boolean updateStart, boolean updateEnd);

  /**
   * Handles editing an event when clicked in the day view.
   * Shows edit dialog through view, updates event, updates model and view.
   *
   * @param event the view event to edit
   */
  void handleEditEvent(InterfaceViewEvent event, EventFormData updatedInfo, EditScope scope);

  /**
   * Navigates to the next month in the calendar view.
   */
  void nextMonth();

  /**
   * Navigates to the previous month in the calendar view.
   */
  void previousMonth();

  /**
   * Selects a specific day to view its events.
   *
   * @param date the date to select and view
   */
  void selectDay(LocalDate date);

  /**
   * Gets all calendar names in the system.
   *
   * @return list of calendar names
   */
  List<String> getAllCalendarNames();

  /**
   * Gets the name of the currently active calendar.
   *
   * @return name of the active calendar, or empty string if none is active
   */
  String getActiveCalendarName();

  /**
   * Gets the timezone of the currently active calendar.
   *
   * @return timezone of the active calendar
   */
  TimeZone getActiveCalendarTimezone();
}
