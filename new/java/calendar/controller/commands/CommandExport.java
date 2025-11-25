package calendar.controller.commands;

import calendar.controller.TokenReader;
import calendar.model.InterfaceCalendar;
import calendar.model.export.ExportFormatCsv;
import calendar.model.export.ExportFormatiCal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Handles when user commands an export of the calendar.
 *
 */
public class CommandExport extends AbstractCommand implements InterfaceCommand {
  private InterfaceCalendar calendar;

  /**
   * Constructor for CommandExport.
   *
   * @param calendar InterfaceCalendar
   */
  public CommandExport(InterfaceCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public String execute(TokenReader tokenReader) {

    checkKeyword(tokenReader, "cal", "Missing calendar argument.");
    String fileName = getValue(tokenReader, "Missing filename argument.");

    if (!(fileName.endsWith(".csv") || fileName.endsWith(".ical"))) {
      throw new IllegalArgumentException("Missing csv or ical file ending.");
    } else if (fileName.endsWith(".ical")) {
      fileName = fileName.replace(".ical", ".ics");
    }
    File file = new File(fileName);
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      if (!parentDir.mkdirs()) {
        throw new IllegalArgumentException("Failed to create parent directory.");
      }
    }

    try (PrintWriter writer = new PrintWriter(file)) {
      if (fileName.endsWith(".csv")) {
        writeEvents(writer, calendar.export(new ExportFormatCsv()));
      } else {
        writeEvents(writer,
            calendar.export((new ExportFormatiCal(calendar.getName(), calendar.getTimeZone()))));
      }
      return "Calendar exported to: " + file.getAbsolutePath();
    } catch (RuntimeException | FileNotFoundException e) {
      throw new IllegalArgumentException("Failed to export calendar.");
    }
  }

  private void writeEvents(PrintWriter writer, List<String> events) {
    for (String event : events) {
      writer.println(event);
    }
  }
}
