package calendar.model.export;

import calendar.model.InterfaceEvent;

/**
 * Formats an individual event for a particular export file.
 */
public interface InterfaceExportFormat {

  /**
   * Formats an individual event for an export file.
   *
   * @return String representation of the formatted event.
   */
  String format(InterfaceEvent event);

  /**
   * Returns the starting content or header required for the export format.
   *
   * @return A string representing the header or initial content specific to the export format.
   */
  String start();

  /**
   * Returns the ending content or footer required for the export format.
   *
   * @return A string representing the footer or final content specific to the export format.
   */
  String end();
}
