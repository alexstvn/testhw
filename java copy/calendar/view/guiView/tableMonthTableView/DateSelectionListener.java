package calendar.view.guiView.tableMonthTableView;

import java.time.LocalDate;

/**
 * Listener interface for date selection events in the calendar.
 */
public interface DateSelectionListener {
  /**
   * Called when a date is selected.
   *
   * @param date the selected date
   */
  void onDateSelected(LocalDate date);
}
