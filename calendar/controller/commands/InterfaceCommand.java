package calendar.controller.commands;

import calendar.controller.TokenReader;

/**
 * Interface for calendar query operations.
 */
public interface InterfaceCommand {
  /**
   * Parses through a command provided to the controller.
   *
   * @param tokenReader Scanner for token list.
   * @return Output, confirmation, or error of performed command.
   */
  String execute(TokenReader tokenReader) throws Exception;
}
