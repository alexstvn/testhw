package calendar.controller;

import calendar.controller.commands.CommandCreateCalendar;
import calendar.controller.commands.CommandEditCalendar;
import calendar.controller.commands.CommandUseCalendar;
import calendar.controller.commands.copy.CommandCopyEvent;
import calendar.controller.commands.copy.CommandCopyEventsBetween;
import calendar.controller.commands.copy.CommandCopyEventsOn;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.view.terminalView.InterfaceView;
import java.util.Objects;

/**
 * Controller that extends CalendarController to support multiple calendars.
 */
public class MultiCalendarController extends CalendarController {
  private final InterfaceCalendarModels models;

  /**
   * Constructor for MultiCalendarController.
   *
   * @param models InterfaceCalendarModels
   * @param inputStream Readable
   * @param view InterfaceView
   */
  public MultiCalendarController(InterfaceCalendarModels models, Readable inputStream,
                                 InterfaceView view) {
    super(null, inputStream, view);
    this.models = Objects.requireNonNull(models);

    commands.put("create calendar",
        (InterfaceCalendar activeCal) -> new CommandCreateCalendar(models));
    commands.put("use calendar", (InterfaceCalendar activeCal) -> new CommandUseCalendar(models));
    commands.put("edit calendar", (InterfaceCalendar activeCal) -> new CommandEditCalendar(models));
    commands.put("copy event",
        (InterfaceCalendar activeCal) -> new CommandCopyEvent(models, activeCal));
    commands.put("copy events on",
        (InterfaceCalendar activeCal) -> new CommandCopyEventsOn(models, activeCal));
    commands.put("copy events between",
        (InterfaceCalendar activeCal) -> new CommandCopyEventsBetween(models, activeCal));
  }

  @Override
  public void run() {
    super.run();
  }

  @Override
  public InterfaceCalendar getActiveCalendar() {
    return models.getActiveCalendar();
  }
}
