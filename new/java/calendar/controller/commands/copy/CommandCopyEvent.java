package calendar.controller.commands.copy;

import calendar.controller.TokenReader;
import calendar.controller.commands.AbstractCommand;
import calendar.controller.commands.InterfaceCommand;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.model.filter.FilterSameStartAndSubject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This parses the command 'copy event eventName on
 * dateStringTtimeString --target calendarName to dateStringTtimeString'.
 */
public class CommandCopyEvent extends AbstractCommand implements InterfaceCommand {
  private InterfaceCalendarModels models;
  private InterfaceCalendar activeCalendar;

  /**
   * constructor for CommandCopyEvent.
   *
   * @param models         InterfaceCalendarModels
   * @param activeCalendar InterfaceCalendar
   */
  public CommandCopyEvent(InterfaceCalendarModels models, InterfaceCalendar activeCalendar) {
    this.models = models;
    this.activeCalendar = activeCalendar;
  }

  @Override
  public String execute(TokenReader tokenReader) {
    String error = "Invalid command format. Please enter "
        + "'copy event <eventName> on <dateStringTtimeString> --target "
        + "<calendarName> to <dateStringTtimeString>'";

    final String eventName = getValue(tokenReader, error);

    checkKeyword(tokenReader, "on", error);
    LocalDateTime sourceDateTime = getDateTime(tokenReader, error);

    checkKeyword(tokenReader, "--target", error);
    String targetCalendarName = getValue(tokenReader, error);

    checkKeyword(tokenReader, "to", error);
    LocalDateTime targetDateTime = getDateTime(tokenReader, error);

    InterfaceCalendar targetCalendar = this.models.get(targetCalendarName);

    FilterSameStartAndSubject filter = new FilterSameStartAndSubject(eventName, sourceDateTime);
    List<InterfaceEvent> matchingEvents = activeCalendar.filter(filter);

    if (matchingEvents.isEmpty()) {
      throw new IllegalArgumentException("Event '" + eventName + "' does not exist.");
    }

    for (InterfaceEvent event : matchingEvents) {
      LocalDateTime ogStart = event.getStartDateTime();
      LocalDateTime ogEnd = event.getEndDateTime();
      Duration duration = Duration.between(ogStart, ogEnd);
      LocalDateTime newEnd = targetDateTime.plus(duration);

      EventRequest eventRequest = new EventRequest.RequestBuilder()
          .subject(event.getSubject())
          .start(targetDateTime)
          .end(newEnd)
          .build();

      targetCalendar.addEvent(eventRequest);
    }

    String dateStr = targetDateTime.toLocalDate().toString();
    String timeStr = targetDateTime.toLocalTime().toString();

    return "Event '" + eventName + "' copied to calendar '"
        + targetCalendarName + "' on " + dateStr + " at " + timeStr + ".";
  }
}
