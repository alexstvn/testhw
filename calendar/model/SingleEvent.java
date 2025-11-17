package calendar.model;

import calendar.model.export.InterfaceExportFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Implementation of a single calendar event.
 */
public class SingleEvent implements InterfaceEvent {
  private String subject;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;
  private String location;
  private EventStatus status;
  private TimeZone timeZone;

  private SingleEvent(String subject, LocalDateTime startDateTime,
                      LocalDateTime endDateTime, TimeZone timeZone) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.timeZone = timeZone;
    this.description = "";
    this.location = "";
    this.status = EventStatus.PUBLIC;
  }

  // =========== GETTER METHODS ============

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public EventStatus getStatus() {
    return status;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public TimeZone getTimeZone() {
    return this.timeZone;
  }

  // SETTERS

  @Override
  public void setProperty(String property, String newValue) {
    switch (property) {
      case "subject":
        subject = newValue;
        break;
      case "description":
        description = newValue;
        break;
      case "location":
        location = newValue;
        break;
      case "status":
        if (newValue.equalsIgnoreCase("PUBLIC")) {
          status = EventStatus.PUBLIC;
        } else if (newValue.equalsIgnoreCase("PRIVATE")) {
          status = EventStatus.PRIVATE;
        } else {
          throw new IllegalArgumentException("Invalid status value - must be PUBLIC or PRIVATE.");
        }
        break;
      case "start":
        startDateTime = LocalDateTime.parse(newValue);
        break;
      case "end":
        endDateTime = LocalDateTime.parse(newValue);
        break;
      default:
        throw new IllegalArgumentException("Unknown property: " + property);
    }
  }


  @Override
  public void adjustTimeZone(TimeZone newTimeZone) {
    this.startDateTime = convertToNewTimeZone(this.startDateTime, newTimeZone);
    this.endDateTime = convertToNewTimeZone(this.endDateTime, newTimeZone);

    this.timeZone = newTimeZone;
  }

  private LocalDateTime convertToNewTimeZone(LocalDateTime datetime, TimeZone newTimeZone) {
    ZonedDateTime currentZoned = datetime.atZone(this.timeZone.toZoneId());
    ZonedDateTime newZoned = currentZoned.withZoneSameInstant(newTimeZone.toZoneId());
    return newZoned.toLocalDateTime();
  }

  // ========= OTHER METHODS ==========

  @Override
  public String export(InterfaceExportFormat format) {
    return format.format(this);
  }

  @Override
  public String toString() {
    String loc = "";
    if (!location.isEmpty()) {
      loc = " in " + location;
    }

    return subject
        + " starting on " + startDateTime.toLocalDate()
        + " at " + startDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        + ", ending on " + endDateTime.toLocalDate()
        + " at " + endDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        + loc;
  }

  /**
   * Builder class for creating SingleEvent instances.
   */
  public static class SingleEventBuilder {
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;
    private TimeZone timeZone;

    /**
     * Sets the subject for the event.
     *
     * @param s the subject
     * @return this builder
     */
    public SingleEventBuilder setSubject(String s) {
      this.subject = s;
      return this;
    }

    /**
     * Sets the start date and time for the event.
     *
     * @return this builder
     */
    public SingleEventBuilder setStart(LocalDateTime t) {
      this.start = t;
      return this;
    }

    /**
     * Sets the end date and time for the event.
     *
     * @return this builder
     */
    public SingleEventBuilder setEnd(LocalDateTime t) {
      this.end = t;
      return this;
    }

    /**
     * Sets the time zone for the event.
     *
     * @param tz The time zone to use.
     * @return this builder
     */
    public SingleEventBuilder setTimeZone(TimeZone tz) {
      this.timeZone = tz;
      return this;
    }

    /**
     * Builds the SingleEvent instance.
     *
     * @return the built SingleEvent
     */
    public InterfaceEvent build() {
      return new SingleEvent(subject, start, end, timeZone);
    }
  }
}
