package calendar.controller.commands.editevents;

import calendar.controller.TokenReader;
import calendar.controller.commands.AbstractCommand;
import calendar.model.EventRequest;
import java.time.LocalDateTime;

/**
 * This class is used to resolve the duplicate code logic for editing events vs editing series.
 */
public abstract class AbstractEditCommand extends AbstractCommand {
  @Override
  public String execute(TokenReader tokenReader) throws Exception {
    String property = getValue(tokenReader, "Expected property name.");
    String subject = getValue(tokenReader, "Expected subject name.");
    checkKeyword(tokenReader, "from", "Expected 'from' after event subject.");
    LocalDateTime start = getDateTime(tokenReader, "Expected start date and time.");
    checkKeyword(tokenReader, "with", "Missing 'with' keyword.");
    String newValue = getValue(tokenReader, "Expected updated property value.");

    EventRequest eventRequest = new EventRequest.RequestBuilder()
        .subject(subject)
        .start(start)
        .property(property)
        .newValue(newValue)
        .build();

    return performEdit(eventRequest);
  }

  /**
   * Allows for the execution of editing events or series accordingly.
   *
   * @param eventRequest Parameters for editing events or series.
   * @return Confirmation message that edit was successfully performed.
   */
  public abstract String performEdit(EventRequest eventRequest);
}
