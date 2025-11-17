package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceEvent;
import calendar.model.filter.FilterByDate;
import calendar.model.filter.FilterByDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Queries the calendar and prints out events that happen on a day or dateTime range.
 * - print events on dateString
 * - print events from dateStringTtimeString to dateStringTtimeString
 */
public class CommandPrint extends AbstractCommand {
  private InterfaceCalendar calendar;

  /**
   * Constructor for CommandPrint.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandPrint(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String execute(TokenReader tokenReader) {
    checkKeyword(tokenReader, "events",
        "Usage: print events [on <date>] | [from <start> to <end>]");
    String query = getValue(tokenReader, "Invalid print command format.");

    String printMessage;
    List<InterfaceEvent> events;

    if (query.equals("on")) {
      String input = getValue(tokenReader, "Missing date after 'on'.");
      LocalDate date = LocalDate.parse(input);
      printMessage = query + " " + input;

      events = calendar.filter(new FilterByDate(date));
    } else if (query.equals("from")) {
      LocalDateTime from =
          getDateTime(tokenReader, "Missing start or end date-time for 'from'...'to'.");
      checkKeyword(tokenReader, "to", "Invalid print command format.");
      LocalDateTime to =
          getDateTime(tokenReader, "Missing start or end date-time for 'from'...'to'.");
      printMessage = query + " " + from + " to " + to;

      events = calendar.filter(new FilterByDateTime(from, to));
    } else {
      throw new IllegalArgumentException("Invalid print command format.");
    }

    return printMessage(events, printMessage);
  }

  private String printMessage(List<InterfaceEvent> events, String printMessage) {
    StringBuilder result = new StringBuilder();

    if (events.isEmpty()) {
      return "No events found.";
    }

    result.append("Printing ").append(printMessage).append("...").append(System.lineSeparator());
    for (InterfaceEvent event : events) {
      result.append(event.toString()).append(System.lineSeparator());
    }
    return result.toString().trim();
  }
}
