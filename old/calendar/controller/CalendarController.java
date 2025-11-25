package calendar.controller;

import calendar.controller.commands.CommandCreateEvent;
import calendar.controller.commands.CommandExport;
import calendar.controller.commands.CommandPrint;
import calendar.controller.commands.CommandShowStatus;
import calendar.controller.commands.InterfaceCommand;
import calendar.controller.commands.editevents.CommandEditEvent;
import calendar.controller.commands.editevents.CommandEditEvents;
import calendar.controller.commands.editevents.CommandEditSeries;
import calendar.model.InterfaceCalendar;
import calendar.view.InterfaceView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Base controller for handling generic calendar commands.
 * Supports compound commands like "create calendar" vs "create event".
 */
public class CalendarController implements InterfaceController {
  protected final InterfaceCalendar model; // May be null in multi-calendar mode
  protected final Readable inputStream;
  protected final InterfaceView view;
  protected final Map<String, Function<InterfaceCalendar, InterfaceCommand>> commands;

  /**
   * Initializes a new controller.
   *
   * @param model       InterfaceCalendar
   * @param inputStream Readable
   * @param view        InterfaceView
   */
  public CalendarController(InterfaceCalendar model, Readable inputStream, InterfaceView view) {
    this.model = model;
    this.inputStream = Objects.requireNonNull(inputStream);
    this.view = Objects.requireNonNull(view);

    this.commands = new HashMap<>();
    initializeDefaultCommands();
  }

  /**
   * Defines default single-calendar commands.
   * Subclasses (like MultiCalendarController) can extend this.
   */
  protected void initializeDefaultCommands() {
    commands.put("create event", (InterfaceCalendar cal) -> new CommandCreateEvent(cal));
    commands.put("edit event", (InterfaceCalendar cal) -> new CommandEditEvent(cal));
    commands.put("edit events", (InterfaceCalendar cal) -> new CommandEditEvents(cal));
    commands.put("edit series", (InterfaceCalendar cal) -> new CommandEditSeries(cal));
    commands.put("print", (InterfaceCalendar cal) -> new CommandPrint(cal));
    commands.put("export", (InterfaceCalendar cal) -> new CommandExport(cal));
    commands.put("show", (InterfaceCalendar cal) -> new CommandShowStatus(cal));
  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(this.inputStream);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      if (line.isEmpty()) {
        continue;
      }

      line = normalizeWhitespace(line);
      List<String> tokens = tokenize(line);

      TokenReader tokenReader = new TokenReader(tokens);
      String commandKey = determineCommandKey(tokenReader);

      if (commandKey.equals("exit")) {
        view.renderMessage("Exited calendar.");
        return;
      }

      InterfaceCalendar currentModel = getActiveCalendar();
      if (currentModel == null && commands.containsKey(commandKey)) {
        if (!commandKey.equals("create calendar")
            && !commandKey.equals("use calendar")
            && !commandKey.equals("edit calendar")) {
          view.renderMessage("Error: Calendar not selected. Use 'use calendar' command.");
          continue;
        }
      }

      Function<InterfaceCalendar, InterfaceCommand> commandConstructor = commands.get(commandKey);

      if (commandConstructor == null) {
        view.renderMessage("Invalid command: " + commandKey);
        continue;
      }

      try {
        InterfaceCommand commandToRun = commandConstructor.apply(currentModel);
        String result = commandToRun.execute(tokenReader);
        view.renderMessage(result);
      } catch (Exception e) {
        view.renderMessage("Error: " + e.getMessage());
      }
    }

    view.renderMessage("Error: No exit command provided.");
  }

  // ===================== Helper Methods =====================

  /**
   * Determines the appropriate command key based on tokenized user input.
   * Handles compound commands such as:
   * - "create event"
   * - "edit events"
   * - "copy events on"
   * - "use calendar"
   */
  private String determineCommandKey(TokenReader tokenReader) {

    String commandWord = tokenReader.next();
    String commandKey = commandWord;

    if (tokenReader.hasNext()
        && (commandWord.equals("create")
        || commandWord.equals("edit")
        || commandWord.equals("use"))) {
      String possibleSub = tokenReader.next();
      String combinedKey = commandWord + " " + possibleSub;
      if (commands.containsKey(combinedKey)) {
        commandKey = combinedKey;
      }
    }

    // Handle compound "copy ..." commands (copy event(s) on/between)
    if (tokenReader.hasNext() && commandWord.equals("copy")) {
      String possibleSub = tokenReader.next().toLowerCase();
      String combinedKey = commandWord + " " + possibleSub;

      if (tokenReader.hasNext() && possibleSub.equals("events")) {
        String thirdToken = tokenReader.next().toLowerCase();
        if (thirdToken.equals("on") || thirdToken.equals("between")) {
          combinedKey = commandWord + " " + possibleSub + " " + thirdToken;
        }
      }
      if (commands.containsKey(combinedKey)) {
        commandKey = combinedKey;
      }
    }
    return commandKey;
  }


  private String normalizeWhitespace(String input) {
    return input.trim().replaceAll("\\s+", " ");
  }

  /**
   * Splits a command line into tokens, keeping quoted strings intact.
   * Example: create "Lunch Meeting" at "Main Office"
   */
  private List<String> tokenize(String line) {
    List<String> tokens = new ArrayList<>();
    Scanner tokenScanner = new Scanner(line);
    tokenScanner.useDelimiter("\"");
    boolean insideQuotes = false;

    while (tokenScanner.hasNext()) {
      String part = tokenScanner.next();
      if (insideQuotes) {
        tokens.add(part.trim());
      } else {
        String[] words = part.trim().split("\\s+");
        for (String word : words) {
          if (!word.isEmpty()) {
            tokens.add(word);
          }
        }
      }
      insideQuotes = !insideQuotes;
    }
    return tokens;
  }

  // ===================== Active Calendar Access =====================

  /**
   * Returns the active calendar. In multi-calendar mode, overridden
   * to return the model-managed active calendar.
   */
  @Override
  public InterfaceCalendar getActiveCalendar() {
    return model;
  }
}
