package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendar;
import calendar.model.filter.FilterByDateTime;
import java.time.LocalDateTime;

/**
 * Command to show whether a user is busy or free from a tokenized user input
 * containing the specific date and time.
 * - show status on dateStringTtimeString
 */
public class CommandShowStatus extends AbstractCommand implements InterfaceCommand {
  private final InterfaceCalendar calendar;

  /**
   * Constructor for CommandShowStatus.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandShowStatus(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String execute(TokenReader tokenReader) {
    checkKeyword(tokenReader, "status", "Missing keyword 'status'.");
    checkKeyword(tokenReader, "on", "Missing keyword 'on'.");
    LocalDateTime dateTime =
        LocalDateTime.parse(getValue(tokenReader, "No date/time specified after 'on'."));

    if (calendar.isBusyAt(dateTime)) {
      return "Busy on " + dateTime + " with "
          + calendar.filter(new FilterByDateTime(dateTime)).get(0);
    } else {
      return "Available on " + dateTime;
    }
  }
}
