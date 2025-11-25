package calendar.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Parameter object used to retrieve needed fields for editing and adding events.
 * Allows flexibility by not requiring end date/time for edit events and edit series.
 */
public class EventRequest {
  private final String subject;
  private final LocalDateTime start;
  private final LocalDateTime end;

  private final String property;
  private final String newValue;

  private final String pattern;
  private final String termination;

  private static final LocalTime START_TIME_OF_DAY = LocalTime.of(8, 0);
  private static final LocalTime END_TIME_OF_DAY = LocalTime.of(17, 0);

  /**
   * Initializes the request using the builder object.
   *
   * @param builder Builder used initialize the fields.
   */
  private EventRequest(RequestBuilder builder) {
    this.subject = builder.subject;
    this.start = builder.start;
    this.end = builder.end;
    this.property = builder.property;
    this.newValue = builder.newValue;
    this.pattern = builder.pattern;
    this.termination = builder.termination;
  }

  /**
   * Retrieves the subject of a requested event.
   *
   * @return Subject of desired event.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Retrieves start date/time object of requested event.
   *
   * @return Start date/time of desired event.
   */
  public LocalDateTime getStart() {
    return start;
  }

  /**
   * Retrieves end date/time object of requested event.
   *
   * @return End date/time of desired event.
   */
  public LocalDateTime getEnd() {
    return end;
  }

  /**
   * Retrieves property label of event field.
   *
   * @return Property label of desired event field.
   */
  public String getProperty() {
    return property;
  }

  /**
   * Retrieves new value of a desired property to edit in event.
   *
   * @return Value to set property to of desired event.
   */
  public String getNewValue() {
    return newValue;
  }

  /**
   * Retrieves pattern string for creating a recurring event.
   *
   * @return Pattern string for recurring event.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Retrieves termination string for creating a recurring event.
   *
   * @return Termination field for recurring event.
   */
  public String getTermination() {
    return termination;
  }

  /**
   * Initializes an event request.
   */
  public static class RequestBuilder {
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;

    private String property;
    private String newValue;

    private String pattern;
    private String termination;

    /**
     * Sets subject of event request.
     *
     * @param subject Subject of event/series to retrieve.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets start date/time of event request.
     *
     * @param start Date/time object representing an event's start field.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder start(LocalDateTime start) {
      this.start = start;
      return this;
    }

    /**
     * Sets end date/time of event request.
     *
     * @param end Date/time object representing an event's end field.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder end(LocalDateTime end) {
      this.end = end;
      return this;
    }

    /**
     * Sets the desired property to edit in an event.
     *
     * @param property Property field to edit in event/series.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder property(String property) {
      this.property = property;
      return this;
    }

    /**
     * Sets the designated updated value for the given property.
     *
     * @param newValue New value for a given property in event/series.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder newValue(String newValue) {
      this.newValue = newValue;
      return this;
    }

    /**
     * Sets the designated date for an all day event.
     *
     * @param date Date for an all day event.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder date(LocalDate date) {
      this.start = date.atTime(START_TIME_OF_DAY);
      this.end = date.atTime(END_TIME_OF_DAY);
      return this;
    }

    /**
     * Sets the designated pattern for a recurring event series.
     *
     * @param pattern Date for a recurring event series.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder pattern(String pattern) {
      this.pattern = pattern;
      return this;
    }

    /**
     * Sets the designated termination condition for a recurring event series.
     *
     * @param termination Termination condition for a recurring event series.
     * @return Updated builder object to initialize event request.
     */
    public RequestBuilder termination(String termination) {
      this.termination = termination;
      return this;
    }

    /**
     * Initializes an event request using this builder.
     *
     * @return Initialized event request.
     */
    public EventRequest build() {
      return new EventRequest(this);
    }
  }
}
