import calendar.InputFactory;
import calendar.controller.InterfaceController;
import calendar.controller.MultiCalendarController;
import calendar.controller.guicontroller.GuiCalendarController;
import calendar.model.CalendarModelsImpl;
import calendar.model.InterfaceCalendarModels;
import calendar.view.gui.MainView;
import calendar.view.simple.InterfaceView;
import calendar.view.simple.SimpleView;

/**
 * Program runner for the Calendar Application.
 * Supports GUI, interactive, and headless modes.
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
    InterfaceController controller;
    if (args.length == 0) {
      controller = new GuiCalendarController(models, new MainView());
    } else {
      Readable input = InputFactory.getInput(args);
      controller = new MultiCalendarController(models, input, view);
    }
    controller.run();
  }
}
