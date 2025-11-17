package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Command to create calendar events from a tokenized user input.
 * Parses commands like:
 * - create event "Event Subject" from YYYY-MM-DDThh:mm to YYYY-MM-DDThh:mm
 * - create event "Event Subject" on YYYY-MM-DD
 * - create event "Event Subject" on YYYY-MM-DD repeats PATTERN for N times
 * - create event "Event Subject" from YYYY-MM-DDThh:mm
 * to YYYY-MM-DDThh:mm repeats PATTERN for N times
 * - create event "Event Subject" from YYYY-MM-DDThh:mm
 * to YYYY-MM-DDThh:mm repeats PATTERN until YYYY-MM-DD
 */
public class CommandCreateEvent extends AbstractCommand {
  private final InterfaceCalendar calendar;

  /**
   * Constructor for CommandCreateEvent.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandCreateEvent(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String execute(TokenReader tokenReader) {

    String subject = getValue(tokenReader, "Expected subject name.");
    EventRequest.RequestBuilder requestBuilder = new EventRequest.RequestBuilder().subject(subject);

    if (tokenReader.hasNext()) {
      if (tokenReader.peek().equals("from")) {
        requestBuilder = parseFrom(tokenReader, requestBuilder);
      } else if (tokenReader.peek().equals("on")) {
        requestBuilder = parsesOn(tokenReader, requestBuilder);
      } else {
        throw new IllegalArgumentException("Expected 'from' or 'on' after event subject.");
      }
    } else {
      throw new IllegalArgumentException("Expected 'from' or 'on' after event subject.");
    }

    parseRepeats(tokenReader, requestBuilder);

    return "Event '" + subject + "' created successfully.";
  }

  /**
   * Parses for a timed event.
   */
  private EventRequest.RequestBuilder parseFrom(TokenReader tokenReader,
                                                EventRequest.RequestBuilder requestBuilder) {
    tokenReader.next();
    LocalDateTime start = getDateTime(tokenReader, "Expected start date and time.");

    checkKeyword(tokenReader, "to", "Expected 'to' after start datetime.");
    LocalDateTime end = getDateTime(tokenReader, "Expected end date and time 'to' keyword.");

    return requestBuilder.start(start).end(end);
  }

  /**
   * Parses for an all-day event.
   */
  private EventRequest.RequestBuilder parsesOn(TokenReader tokenReader,
                                               EventRequest.RequestBuilder requestBuilder) {
    tokenReader.next();
    try {
      LocalDate date = LocalDate.parse(tokenReader.next());
      return requestBuilder.date(date);
    } catch (DateTimeParseException e) {
      throw new InvalidParameterException("Invalid date format. Expected: YYYY-MM-DD.");
    }
  }

  /**
   * Parses the 'repeats' clause if present.
   */
  private void parseRepeats(TokenReader reader, EventRequest.RequestBuilder requestBuilder) {
    if (reader.hasNext()) {
      checkKeyword(reader, "repeats", "Expected 'repeats' keyword if another keyword exists.");
    } else {
      calendar.addEvent(requestBuilder.build());
      return;
    }

    String pattern = getValue(reader, "Expected pattern string.");
    String repeatType = getValue(reader, "Expected 'for' or 'until' after repeats pattern.");

    if (!repeatType.equals("for") && !repeatType.equals("until")) {
      throw new IllegalArgumentException("Expected 'for' or 'until' after repeats pattern.");
    }

    String termination = getValue(reader, "Expected ending date or max occurences.");

    if (repeatType.equals("for")) {
      checkKeyword(reader, "times", "Expected 'times' after repeat count.");
    }

    calendar.addRecurringEvent(requestBuilder.pattern(pattern).termination(termination).build());
  }
}
