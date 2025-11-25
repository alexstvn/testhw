package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Implementation of a recurring event series that manages multiple related events.
 */
public class EventSeries implements InterfaceSeries {
  private final List<InterfaceEvent> series;
  String pattern;

  private EventSeries(List<InterfaceEvent> series, String pattern) {
    this.series = series;
    this.pattern = pattern;
  }

  @Override
  public List<InterfaceEvent> getSeries() {
    return series;
  }

  @Override
  public String getPattern() {
    return pattern;
  }

  // ====== CORE EDITING LOGIC ======

  @Override
  public void editSeries(String property, String newValue) {
    for (InterfaceEvent event : series) {
      if (property.equals("end") || property.equals("start")) {
        LocalDate date = event.getStartDateTime().toLocalDate();
        LocalTime newTime = LocalDateTime.parse(newValue).toLocalTime();

        String updatedValue = date.atTime(newTime).toString();
        event.setProperty(property, updatedValue);
      } else {
        event.setProperty(property, newValue);
      }
    }
  }

  @Override
  public void editStartingFrom(LocalDateTime startDateTime, String property, String newValue) {
    for (InterfaceEvent event : series) {
      if (!event.getStartDateTime().isBefore(startDateTime)) {
        if (property.equals("end")) {
          LocalDate date = event.getStartDateTime().toLocalDate();
          LocalTime newTime = LocalDateTime.parse(newValue).toLocalTime();
          String updatedValue = date.atTime(newTime).toString();
          event.setProperty(property, updatedValue);
        } else {
          event.setProperty(property, newValue);
        }
      }
    }
  }

  @Override
  public InterfaceSeries editStartStartingFrom(LocalDateTime startDateTime,
                                               LocalDateTime newStartTime) {
    List<InterfaceEvent> newSeries = new ArrayList<>();
    LocalTime newTime = newStartTime.toLocalTime();

    Iterator<InterfaceEvent> it = series.iterator();
    while (it.hasNext()) {
      InterfaceEvent event = it.next();

      if (!event.getStartDateTime().isBefore(startDateTime)) {
        it.remove();

        LocalDateTime newStartDateTime = event.getStartDateTime().toLocalDate().atTime(newTime);

        event.setProperty("start", newStartDateTime.toString());

        newSeries.add(event);
      }
    }
    return new SeriesBuilder().setList(newSeries).build();
  }

  @Override
  public InterfaceEvent findEvent(String subject, LocalDateTime startDateTime) {
    for (InterfaceEvent event : series) {
      if (event.getSubject().equals(subject) && event.getStartDateTime().equals(startDateTime)) {
        return event;
      }
    }
    return null;
  }

  @Override
  public void adjustTimeZone(TimeZone newTimeZone) {
    for (InterfaceEvent event : series) {
      event.adjustTimeZone(newTimeZone);
      if (!event.getStartDateTime().toLocalDate().equals(event.getEndDateTime().toLocalDate())) {
        throw new RuntimeException("Cannot change timezone to '" + newTimeZone.getID()
            + "'. Event series '" + event.getSubject()
            + "' would span multiple days, violating event constraints.");
      }
    }
  }

  @Override
  public void removeEvent(InterfaceEvent event) {
    series.remove(event);
  }

  // ====== BUILDER ======

  /**
   * Builder class for creating EventSeries instances.
   */
  public static class SeriesBuilder {
    private final List<InterfaceEvent> series = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subject;
    private int occurrences;
    private final List<DayOfWeek> daysOfWeek = new ArrayList<>();
    private String pattern;
    private TimeZone timeZone;

    /**
     * Sets the subject for all events in the series.
     *
     * @param subject the event subject
     * @return this builder
     */
    public SeriesBuilder setSubject(String subject) {
      this.subject = subject;
      this.timeZone = TimeZone.getTimeZone("America/New_York");
      return this;
    }

    /**
     * Sets the start and end times for events in the series.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return this builder
     */
    public SeriesBuilder setTimes(LocalTime startTime, LocalTime endTime) {
      this.startTime = startTime;
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the days of the week when events should occur.
     *
     * @param pattern string pattern like "MWF" for Monday/Wednesday/Friday
     * @return this builder
     */
    public SeriesBuilder setDaysOfWeek(String pattern) {
      for (char c : pattern.toCharArray()) {
        switch (c) {
          case 'M':
            daysOfWeek.add(DayOfWeek.MONDAY);
            break;
          case 'T':
            daysOfWeek.add(DayOfWeek.TUESDAY);
            break;
          case 'W':
            daysOfWeek.add(DayOfWeek.WEDNESDAY);
            break;
          case 'R':
            daysOfWeek.add(DayOfWeek.THURSDAY);
            break;
          case 'F':
            daysOfWeek.add(DayOfWeek.FRIDAY);
            break;
          case 'S':
            daysOfWeek.add(DayOfWeek.SATURDAY);
            break;
          case 'U':
            daysOfWeek.add(DayOfWeek.SUNDAY);
            break;
          default:
            throw new IllegalArgumentException("Invalid pattern: " + c);
        }
      }
      this.pattern = pattern;
      return this;
    }

    /**
     * Sets the termination condition for the series.
     *
     * @param termination either a number of occurrences or an end date
     * @return this builder
     */
    public SeriesBuilder setTermination(String termination) {
      try {
        occurrences = Integer.parseInt(termination);
        return this;
      } catch (NumberFormatException e) {
        try {
          endDate = LocalDate.parse(termination,
              DateTimeFormatter.ofPattern("yyyy-MM-dd"));
          return this;
        } catch (DateTimeParseException e1) {
          throw new IllegalArgumentException(
              "Invalid format for ending repeating events: " + termination);
        }
      }
    }

    /**
     * Sets the start date for the series.
     *
     * @param start the start date
     * @return this builder
     */
    public SeriesBuilder setStartDate(LocalDate start) {
      this.startDate = start;
      return this;
    }

    /**
     * Sets a pre-existing list of events for the series.
     *
     * @param newSeries the list of events
     * @return this builder
     */
    public SeriesBuilder setList(List<InterfaceEvent> newSeries) {
      series.addAll(newSeries);
      return this;
    }

    /**
     * Sets the time zone for the series.
     *
     * @param newTimeZone the time zone to use
     * @return this builder
     */
    public SeriesBuilder setTimeZone(TimeZone newTimeZone) {
      this.timeZone = newTimeZone;
      return this;
    }

    private void initializeEvents() {
      LocalDate current = startDate;
      series.add(new SingleEvent.SingleEventBuilder()
          .setSubject(subject)
          .setStart(current.atTime(startTime))
          .setEnd(current.atTime(endTime))
          .setTimeZone(timeZone)
          .build());
      current = current.plusDays(1);

      while ((occurrences == 0 || series.size() < occurrences)
          && (endDate == null || !current.isAfter(endDate))) {

        if (daysOfWeek.contains(current.getDayOfWeek())) {
          series.add(new SingleEvent.SingleEventBuilder()
              .setSubject(subject)
              .setStart(current.atTime(startTime))
              .setEnd(current.atTime(endTime))
              .setTimeZone(timeZone)
              .build());
        }
        current = current.plusDays(1);
      }
    }

    /**
     * Builds the EventSeries instance.
     *
     * @return the built EventSeries
     */
    public EventSeries build() {
      if (series.isEmpty()) {
        initializeEvents();
      }
      return new EventSeries(series, pattern);
    }
  }
}
