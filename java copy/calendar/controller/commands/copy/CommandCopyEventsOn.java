package calendar.controller.commands.copy;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.model.filter.FilterByDate;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * This parses the command 'copy events on dateString --target calendarName to dateString'.
 */
public class CommandCopyEventsOn extends AbstractCopyCommand {
  /**
   * Constructor for CommandCopyEventsOn.
   *
   * @param models         InterfaceCalendarModels
   * @param activeCalendar InterfaceCalendar
   */
  public CommandCopyEventsOn(InterfaceCalendarModels models, InterfaceCalendar activeCalendar) {
    super(models, activeCalendar);
  }

  @Override
  public String execute(TokenReader tokenReader) {
    String error = "Invalid command format. Please enter "
        + "'copy events on <dateString> --target <calendarName> to <dateString>";

    try {
      LocalDate sourceDate = LocalDate.parse(getValue(tokenReader, error));
      checkKeyword(tokenReader, "--target", error);
      String targetCalendarName = getValue(tokenReader, error);
      checkKeyword(tokenReader, "to", error);
      LocalDate targetDate = LocalDate.parse(getValue(tokenReader, error));

      copyEventsOn(sourceDate, targetCalendarName, targetDate);

      return "Events from " + sourceDate + " copied to calendar '"
          + targetCalendarName + "' on " + targetDate + ".";
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format.");
    }
  }

  private void copyEventsOn(LocalDate sourceDate,
                            String targetCalendarName,
                            LocalDate targetDate) {
    FilterByDate filter = new FilterByDate(sourceDate);
    List<InterfaceEvent> eventsOnDate = sourceCalendar.filter(filter);

    executeCopyingManyEvents(eventsOnDate, targetCalendarName, sourceDate, targetDate);
  }
}
