import calendar.InputFactory;
import calendar.controller.InterfaceController;
import calendar.controller.MultiCalendarController;
import calendar.model.CalendarModelsImpl;
import calendar.model.InterfaceCalendarModels;
import calendar.view.InterfaceView;
import calendar.view.SimpleView;

/**
 * Program runner for the Calendar Application.
 * Supports both interactive and headless modes.
 */
public class CalendarRunner {

  /**
   * Main method that handles command line arguments and runs the application.
   * Usage: java CalendarRunner --mode interactive
   * java CalendarRunner --mode headless commands.txt
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    InterfaceCalendarModels models = new CalendarModelsImpl();
    InterfaceView view = new SimpleView(System.out);
    Readable input = InputFactory.getInput(args);
    InterfaceController controller = new MultiCalendarController(models, input, view);
    controller.run();
  }
}
