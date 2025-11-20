package calendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Represents a function to translate main method arguments into a suitable readable object.
 * Checks to make that the input is one of two types of running modes: interactive or headless.
 */
public class InputFactory {
  /**
   * Turns arguments into suitable readable object for Controller.
   * Makes sure that arguments are valid for running one of two modes.
   * Expected args are: --mode interactive or --mode headless fileName
   *
   * @param args Arguments passed to main file.
   * @return Readable object o parse commands from.
   */
  public static Readable getInput(String[] args) {
    if (args == null || args.length < 2) {
      throw new IllegalArgumentException("Invalid number of arguments.");
    }

    if (!args[0].equalsIgnoreCase("--mode")) {
      throw new IllegalArgumentException("First argument must be '--mode'.");
    }

    String mode = args[1].toLowerCase();

    switch (mode) {
      case "interactive":
        if (args.length != 2) {
          throw new IllegalArgumentException("Interactive mode takes no input file.");
        }
        return new InputStreamReader(System.in);

      case "headless":
        if (args.length != 3) {
          throw new IllegalArgumentException("Headless mode requires an input file.");
        }
        File file = new File(args[2]);
        if (!file.exists() || !file.isFile()) {
          throw new IllegalArgumentException("Input file does not exist: " + args[2]);
        }
        try {
          return new BufferedReader(new FileReader(file));
        } catch (IOException e) {
          throw new IllegalArgumentException("Could not open input file: " + args[2], e);
        }

      default:
        throw new IllegalArgumentException("Unknown mode: " + mode);
    }
  }
}
