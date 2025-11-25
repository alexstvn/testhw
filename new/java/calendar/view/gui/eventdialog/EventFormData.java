package calendar.view.gui.eventdialog;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class acts as a way to hold necessary information used to create and edit events
 * from the dialog boxes.
 */
public class EventFormData {
  private final String subject;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final Map<String, String> optionalFields;

  private final boolean repeats;
  private final String pattern;      // e.g., "M,T,W,Th,F"
  private final String termination;  // e.g., "date:2025-12-31" or "occurrences:10"

  private EventFormData(EventFormBuilder builder) {
    this.subject = builder.subject;
    this.start = builder.start;
    this.end = builder.end;
    this.optionalFields = builder.optionalFields;
    this.repeats = builder.repeats;
    this.pattern = builder.pattern;
    this.termination = builder.termination;
  }

  /**
   * Returns the subject (title) of the event.
   *
   * @return the event subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Returns the starting date and time for the event.
   *
   * @return the event start date/time
   */
  public LocalDateTime getStart() {
    return start;
  }

  /**
   * Returns the ending date and time for the event.
   *
   * @return the event end date/time
   */
  public LocalDateTime getEnd() {
    return end;
  }

  /**
   * Returns a map containing optional event fields such as location,
   * description, and privacy status.
   *
   * @return an unmodifiable map of optional field names to values
   */
  public Map<String, String> getOptionalFields() {
    return optionalFields;
  }

  /**
   * Indicates whether the event repeats using a recurrence rule.
   *
   * @return {@code true} if the event repeats, {@code false} otherwise
   */
  public boolean isRepeats() {
    return repeats;
  }

  /**
   * Returns the recurrence pattern associated with the event.
   * For example: {@code "M,T,W,Th,F"}.
   *
   * @return the recurrence pattern, or {@code null} if no pattern is set
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Returns the termination rule for the recurrence pattern.
   * Examples include: 2025-12-31 or 10
   *
   * @return the termination rule, or {@code null} if the event does not repeat
   */
  public String getTermination() {
    return termination;
  }

  /**
   * Used to gradually initialize the results of an event dialog box.
   */
  public static class EventFormBuilder {
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;
    Map<String, String> optionalFields;

    private boolean repeats;
    private String pattern;      // e.g., "M,T,W,Th,F"
    private String termination;

    /**
     * Creates a new builder for constructing an EventFormData instance.
     * All optional fields are initialized to an empty map.
     */
    public EventFormBuilder() {
      this.optionalFields = new HashMap<>();
    }

    /**
     * Builds and returns a new EventFormData instance containing all
     * fields previously set on this builder.
     *
     * @return a populated EventFormData object
     */
    public EventFormData build() {
      return new EventFormData(this);
    }

    /**
     * Sets the event subject.
     *
     * @param subject the title or name of the event
     * @return this builder, for method chaining
     */
    public EventFormBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the starting date and time for the event.
     *
     * @param start the event start date/time
     * @return this builder, for method chaining
     */
    public EventFormBuilder start(LocalDateTime start) {
      this.start = start;
      return this;
    }

    /**
     * Sets the ending date and time for the event.
     *
     * @param end the event end date/time
     * @return this builder, for method chaining
     */
    public EventFormBuilder end(LocalDateTime end) {
      this.end = end;
      return this;
    }

    /**
     * Sets the event location as an optional field.
     *
     * @param location the event location
     * @return this builder, for method chaining
     */
    public EventFormBuilder location(String location) {
      optionalFields.put("location", location);
      return this;
    }

    /**
     * Sets the event description as an optional field.
     *
     * @param description descriptive text about the event
     * @return this builder, for method chaining
     */
    public EventFormBuilder description(String description) {
      optionalFields.put("description", description);
      return this;
    }

    /**
     * Sets the event privacy status as an optional field.
     *
     * @param isPrivate {@code true} if the event is private, {@code false} if public
     * @return this builder, for method chaining
     */
    public EventFormBuilder isPrivate(boolean isPrivate) {
      String status = isPrivate ? "PRIVATE" : "PUBLIC";
      optionalFields.put("status", status);
      return this;
    }

    /**
     * Specifies whether this event is a recurring event.
     *
     * @param repeats True if the event is a recurring event.
     * @return this builder, for method chaining
     */
    public EventFormBuilder repeats(boolean repeats) {
      this.repeats = repeats;
      return this;
    }

    /**
     * Sets the recurrence pattern for the event in format of "MWR", "SU", etc.
     *
     * @param pattern a string describing the recurrence days or pattern
     * @return this builder, for method chaining
     */
    public EventFormBuilder pattern(String pattern) {
      this.pattern = pattern;
      return this;
    }

    /**
     * Sets the termination condition for the event's recurrence.
     *
     * @param termination the recurrence termination rule
     * @return this builder, for method chaining
     */
    public EventFormBuilder termination(String termination) {
      this.termination = termination;
      return this;
    }

  }
}
