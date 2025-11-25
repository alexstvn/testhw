package calendar.controller;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Helper class to read tokens sequentially, including support for optional arguments.
 */
public class TokenReader {
  private final List<String> tokens;
  private int index = 0;

  /**
   * Constructs a new TokenReader.
   *
   * @param tokens List of tokens to read.
   */
  public TokenReader(List<String> tokens) {
    this.tokens = tokens;
  }

  /**
   * check if there is another token to read.
   *
   * @return true if there is another token to read, false otherwise.
   */
  public boolean hasNext() {
    return index < tokens.size();
  }

  /**
   * Reads the next token.
   *
   * @return the next token.
   */
  public String next() {
    if (!hasNext()) {
      throw new NoSuchElementException("Invalid command: not enough arguments.");
    }
    return tokens.get(index++);
  }

  /**
   * Get the token without advancing the index.
   *
   * @return the token.
   */
  public String peek() {
    if (!hasNext()) {
      throw new NoSuchElementException("Invalid command: not enough arguments.");
    }
    return tokens.get(index);
  }
}

