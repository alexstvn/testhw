package calendar.view.guiView.tableMonthTableView;

import calendar.model.InterfaceEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface for the month table panel that displays a calendar grid with events.
 * Receives all data from MainView (parent component), following MVC pattern.
 */
public interface InterfaceMonthTablePanel {

  /**
   * Sets the month and year to display.
   * Called by MainView when the month changes.
   *
   * @param year  the year (from MainView/Controller)
   * @param month the month (1-12) (from MainView/Controller)
   */
  void setMonthYear(int year, int month);

  /**
   * Sets the events to display on the calendar.
   * Called by MainView with event data from Controller.
   *
   * @param events map of dates to list of events on that date (from Controller)
   */
  void setEvents(Map<LocalDate, List<InterfaceEvent>> events);

  /**
   * Sets the currently selected date.
   *
   * @param date the date to select
   */
  void setSelectedDate(LocalDate date);

  /**
   * Gets the currently selected date.
   *
   * @return the selected date, or null if none selected
   */
  LocalDate getSelectedDate();

  /**
   * Refreshes the calendar display.
   */
  void refresh();

  /**
   * Sets a listener to be notified when a date is clicked.
   *
   * @param listener the date selection listener
   */
  void setDateSelectionListener(DateSelectionListener listener);
}
