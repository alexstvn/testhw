package calendar.controller.commands.copy;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.model.filter.FilterByDateRange;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Command Coppy Events Between class that extend the abstract copy.
 */
public class CommandCopyEventsBetween extends AbstractCopyCommand {
  /**
   * Constructor for CommandCopyEventsBetween class.
   *
   * @param models         InterfaceCalendarModels
   * @param activeCalendar InterfaceCalendar
   */
  public CommandCopyEventsBetween(InterfaceCalendarModels models,
                                  InterfaceCalendar activeCalendar) {
    super(models, activeCalendar);
  }

  @Override
  public String execute(TokenReader tokenReader) {
    String error = "Invalid command format. Please enter "
        + "'copy events between <dateString> and <dateString> "
        + "--target <calendarName> to <dateString>'";
    try {
      LocalDate sourceStartDate = LocalDate
          .parse(getValue(tokenReader, "Expected start date after 'between'."));
      checkKeyword(tokenReader, "and", "Expected 'and' keyword.");
      LocalDate sourceEndDate = LocalDate
          .parse(getValue(tokenReader, "Expected end date after 'and'."));
      if (sourceEndDate.isBefore(sourceStartDate)) {
        throw new IllegalArgumentException("End date must be after or equal to start date.");
      }

      checkKeyword(tokenReader, "--target", error);
      String targetCalendarName = getValue(tokenReader, "Expected target calendar name.");
      checkKeyword(tokenReader, "to", error);
      LocalDate targetDate = LocalDate
          .parse(getValue(tokenReader, "Expected target date after 'to'."));

      copyEventsBetween(sourceStartDate, sourceEndDate, targetCalendarName, targetDate);
      return "Events from " + sourceStartDate + " to " + sourceEndDate + " copied to calendar '"
          + targetCalendarName + "' on " + targetDate + ".";
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format.");
    }
  }

  private void copyEventsBetween(LocalDate sourceStartDate, LocalDate sourceEndDate,
                                 String targetCalendarName, LocalDate targetDate) {
    FilterByDateRange filter = new FilterByDateRange(sourceStartDate, sourceEndDate);
    List<InterfaceEvent> eventsInRange = sourceCalendar.filter(filter);

    executeCopyingManyEvents(eventsInRange, targetCalendarName, sourceStartDate, targetDate);
  }
}
