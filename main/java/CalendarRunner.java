import calendar.controller.guicontroller.GuiCalendarController;
import calendar.model.CalendarModelsImpl;
import calendar.model.InterfaceCalendarModels;
import calendar.view.guiView.MainView;
import calendar.view.guiView.InterfaceGuiView;

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
    InterfaceGuiView view = new MainView();
    //Readable input = InputFactory.getInput(args);
    //InterfaceController controller = new MultiCalendarController(models, input, view);
    GuiCalendarController controller = new GuiCalendarController(models,view);

    controller.run();
  }
}
