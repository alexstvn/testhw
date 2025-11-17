package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendarModels;
import java.security.InvalidParameterException;
import java.util.TimeZone;

/**
 * This parses the command of format:
 * 'edit calendar --name name-of-calendar --property property-name new-property-value'.
 */
public class CommandEditCalendar extends AbstractCommand implements InterfaceCommand {
  private InterfaceCalendarModels models;

  /**
   * Constructor for CommandEditCalendar.
   *
   * @param models InterfaceCalendarModels
   */
  public CommandEditCalendar(InterfaceCalendarModels models) {
    this.models = models;
  }

  @Override
  public String execute(TokenReader tokenReader) {

    checkKeyword(tokenReader, "--name", "Expected --name tag.");
    String name = getValue(tokenReader, "Expected calendar name.");
    checkKeyword(tokenReader, "--property", "Expected --property tag or no calendar name.");
    String property = getValue(tokenReader, "No property specified.");
    String newValue = getValue(tokenReader, "new property value must not be empty.");

    if (property.equals("name")) {
      if (newValue.isEmpty()) {
        throw new InvalidParameterException("new name must not be empty.");
      }
      models.setName(name, newValue);
      return "Calendar '" + name + "' renamed to '" + newValue + "'.";
    } else if (property.equals("timezone")) {
      TimeZone timeZone = TimeZone.getTimeZone(newValue);

      if (!timeZone.getID().equalsIgnoreCase(newValue)) {
        throw new IllegalArgumentException("Invalid time zone specified.");
      } else {
        String oldTimeZone = String.valueOf(models.getTimeZone(name).toZoneId());
        models.setTimeZone(name, timeZone);
        return "Calendar '" + name + "' timezone changed from '" + oldTimeZone + "' to '"
            + newValue + "'.";
      }
    } else {
      throw new IllegalArgumentException(
          "Invalid property specified. Expected one of: name, timezone.");
    }
  }
}
