package calendar.controller.commands.editevents;

import calendar.controller.commands.InterfaceCommand;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;

/**
 * This class handles the command:
 * edit series PropertyName "Subject Name" from YYYY-MM-DDThh:mm with updatedValue.
 */
public class CommandEditSeries extends AbstractEditCommand implements InterfaceCommand {
  private InterfaceCalendar calendar;

  /**
   * Constructor for CommandEditSeries.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandEditSeries(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String performEdit(EventRequest eventRequest) {
    calendar.editSeries(eventRequest);
    return "Successfully edited series '" + eventRequest.getSubject() + "' "
        + eventRequest.getProperty() + ".";
  }

}
