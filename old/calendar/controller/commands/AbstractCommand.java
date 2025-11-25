package calendar.controller.commands;

import calendar.controller.TokenReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/**
 * Abstraction for implementing a command entered by the user in the Controller.
 * Extends a general command for the controller by converting datetime to proper time value.
 */
public abstract class AbstractCommand implements InterfaceCommand {

  /**
   * Converts a string of the format YYYY-MM-DDThh:mm into the appropriate datetime object.
   *
   * @param input String of a format YYYY-MM-DDThh:mm
   * @return Converted DateTime object whose value reflects the user's input.
   */
  protected LocalDateTime parseLocalDateTime(String input) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    try {
      return LocalDateTime.parse(input, formatter);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(
          "Invalid datetime format (" + input
              + "). Expected 'YYYY-MM-DDThh:mm'"
      );
    }
  }

  /**
   * Get the value of the next token.
   *
   * @param tokenReader token reader
   * @param error error message to specify what value was expected.
   * @return the value of the next token
   */
  protected String getValue(TokenReader tokenReader, String error) {
    try {
      return tokenReader.next();
    } catch (Exception e) {
      throw new IllegalArgumentException(error);
    }
  }

  /**
   * Check if the next token is the specified keyword.
   *
   * @param tokenReader token reader
   * @param keyword keyword to check for
   * @param error error message to specify what keyword was expected
   */
  protected void checkKeyword(TokenReader tokenReader, String keyword, String error) {
    if (!tokenReader.peek().equals(keyword)) {
      throw new IllegalArgumentException(error);
    } else {
      tokenReader.next();
    }
  }

  /**
   * Get the next token as a datetime.
   *
   * @param tokenReader token reader
   * @param error error message of what date was expected
   * @return the next token as a datetime
   */
  protected LocalDateTime getDateTime(TokenReader tokenReader, String error) {
    try {
      return parseLocalDateTime(tokenReader.next());
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(error);
    }
  }
}
