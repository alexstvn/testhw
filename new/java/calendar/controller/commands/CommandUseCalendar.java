package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendarModels;

/**
 * This parses the command 'use calendar --name calName'.
 */
public class CommandUseCalendar extends AbstractCommand implements InterfaceCommand {
  private InterfaceCalendarModels models;

  /**
   * Constructor for CommandUseCalendar.
   *
   * @param models InterfaceCalendarModels
   */
  public CommandUseCalendar(InterfaceCalendarModels models) {
    this.models = models;
  }

  @Override
  public String execute(TokenReader tokenReader) {
    checkKeyword(tokenReader, "--name", "Expected '--name' tag.");
    String name = getValue(tokenReader, "Expected calendar name.");
    if (tokenReader.hasNext()) {
      throw new IllegalArgumentException("Invalid calendar name.");
    }

    models.setActiveCalendar(name);
    return "Now using calendar '" + name + "' (" + models.getTimeZone(name).getID() + ")";
  }
}
