package calendar.controller.commands.editevents;

import calendar.controller.TokenReader;
import calendar.controller.commands.AbstractCommand;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;
import java.time.LocalDateTime;

/**
 * This class handles the command:
 * edit event PropertyName "Subject Name" from YYYY-MM-DDThh:mm
 * to YYYY-MM-DDThh:mm with updatedValue.
 */
public class CommandEditEvent extends AbstractCommand {
  private final InterfaceCalendar calendar;

  /**
   * Constructor for CommandEditEvent.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandEditEvent(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String execute(TokenReader tokenReader) {
    final String property = getValue(tokenReader, "Expected property name.");
    final String subject = getValue(tokenReader, "Expected subject name.");

    checkKeyword(tokenReader, "from", "Expected 'from' after event subject.");
    final LocalDateTime start = getDateTime(tokenReader, "Expected start date and time.");

    checkKeyword(tokenReader, "to",
        "Expected 'to' after start datetime for single event edit.");
    final LocalDateTime end = getDateTime(tokenReader,
        "Missing end datetime for single event edit.");

    checkKeyword(tokenReader, "with", "Missing 'with' keyword.");

    String newValue = (property.equals("start") || property.equals("end"))
        ? getDateTime(tokenReader, "Expected updated property value.").toString()
        : getValue(tokenReader, "Expected updated property value.");

    EventRequest request = new EventRequest.RequestBuilder()
        .subject(subject)
        .start(start)
        .end(end)
        .property(property)
        .newValue(newValue)
        .build();

    calendar.editEvent(request);

    return "Successfully edited event '" + subject + "' " + property + ".";
  }
}
