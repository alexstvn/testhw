package calendar.controller.commands.editevents;

import calendar.controller.commands.InterfaceCommand;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;

/**
 * This class handles the command where we events in a series (or a single event) starting from:
 * edit events PropertyName "Subject Name" from YYYY-MM-DDThh:mm with updatedValue.
 */
public class CommandEditEvents extends AbstractEditCommand implements InterfaceCommand {
  private final InterfaceCalendar calendar;

  /**
   * Constructor for CommandEditEvents.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandEditEvents(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String performEdit(EventRequest eventRequest) {
    calendar.editEvents(eventRequest);
    return "Successfully edited events '" + eventRequest.getSubject() + "' "
        + eventRequest.getProperty() + ".";
  }
}
