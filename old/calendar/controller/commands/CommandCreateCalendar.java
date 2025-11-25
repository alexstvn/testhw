package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.CalendarModel;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import java.util.TimeZone;

/**
 * This parses the command 'create calendar --name calName --timezone area/location'.
 */
public class CommandCreateCalendar extends AbstractCommand implements InterfaceCommand {
  private InterfaceCalendarModels models;

  /**
   * Constructor for CommandCreateCalendar.
   *
   * @param models InterfaceCalendarModels
   */
  public CommandCreateCalendar(InterfaceCalendarModels models) {
    this.models = models;
  }

  @Override
  public String execute(TokenReader tokenReader) {

    checkKeyword(tokenReader, "--name", "Expected --name tag.");
    String name = getValue(tokenReader, "Name must not be empty.");
    checkKeyword(tokenReader, "--timezone", "Expected --timezone tag or missing calendar name.");
    String tzInput = getValue(tokenReader, "Time Zone must not be empty.");
    TimeZone timeZone = TimeZone.getTimeZone(tzInput);

    if (!timeZone.getID().equalsIgnoreCase(tzInput)) {
      throw new IllegalArgumentException("Invalid time zone specified.");
    }

    InterfaceCalendar calendar =
        new CalendarModel.CalendarBuilder().name(name).timeZone(timeZone).build();
    models.add(name, calendar, timeZone);

    return "Calendar '" + name + "' created successfully.";
  }
}
