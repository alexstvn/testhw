package calendar.model;

import calendar.model.export.InterfaceExportFormat;
import calendar.model.filter.FilterByDateTime;
import calendar.model.filter.InterfaceFilter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Implementation of the ICalendar interface that provides core calendar functionality.
 * This class manages events and event series, providing methods for creation, editing,
 * querying, and exporting calendar data.
 * The start/end hours of events are limited 8 AM - 5 PM due to the hh:mm format.
 */
public class CalendarModel implements InterfaceCalendar {
  private final SortedSet<InterfaceEvent> allEvents;
  private final List<InterfaceSeries> allSeries;

  private static final LocalTime START_TIME_OF_DAY = LocalTime.of(8, 0);
  private static final LocalTime END_TIME_OF_DAY = LocalTime.of(17, 0);

  private final EventsEditor editor;
  private final EventValidator eventValidator;

  private String name;
  private TimeZone timeZone;

  /**
   * Constructs a new CalendarModel with empty event storage.
   */
  private CalendarModel(String name, TimeZone timeZone, SortedSet<InterfaceEvent> allEvents,
                        List<InterfaceSeries> allSeries) {
    this.name = name;
    this.timeZone = timeZone;
    this.allEvents = allEvents;
    this.allSeries = allSeries;

    this.eventValidator = new EventValidator();
    this.editor = new EventsEditor(allEvents, allSeries, this);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public TimeZone getTimeZone() {
    return timeZone;
  }

  // ========== ADDING ==========
  @Override
  public void addEvent(EventRequest eventRequest) {
    LocalDateTime start = eventRequest.getStart();
    LocalDateTime end = eventRequest.getEnd();
    String subject = eventRequest.getSubject();

    eventValidator.validateEventTimes(start, end);

    InterfaceEvent event = new SingleEvent.SingleEventBuilder()
        .setSubject(subject)
        .setStart(start)
        .setEnd(end)
        .setTimeZone(timeZone)
        .build();

    if (!allEvents.add(event)) {
      throw new IllegalArgumentException("Duplicate event already exists");
    }
  }

  @Override
  public void addRecurringEvent(EventRequest eventRequest) {
    String subject = eventRequest.getSubject();
    LocalDateTime start = eventRequest.getStart();
    LocalDateTime end = eventRequest.getEnd();
    String pattern = eventRequest.getPattern();
    String termination = eventRequest.getTermination();

    if (!start.toLocalDate().equals(end.toLocalDate())) {
      throw new IllegalArgumentException("Start time and end time must be the same date");
    }

    eventValidator.validateEventTimes(start, end);

    InterfaceSeries series = new EventSeries.SeriesBuilder()
        .setSubject(subject)
        .setTimes(start.toLocalTime(), end.toLocalTime())
        .setStartDate(start.toLocalDate())
        .setDaysOfWeek(pattern)
        .setTermination(termination)
        .setTimeZone(timeZone)
        .build();


    for (InterfaceEvent e : series.getSeries()) {
      if (!allEvents.add(e)) {
        throw new IllegalArgumentException("Duplicate event in series already exists");
      }
    }

    allSeries.add(series);
  }

  // ========== EDITING ==========
  @Override
  public void editEvent(EventRequest eventRequest) {
    editor.editEvent(eventRequest);
  }

  @Override
  public void editEvents(EventRequest eventRequest) {
    editor.editEvents(eventRequest);
  }

  @Override
  public void editSeries(EventRequest eventRequest) {
    editor.editSeries(eventRequest);
  }


  // ========== MISCELLANEOUS QUERIES ==========
  @Override
  public List<InterfaceEvent> filter(InterfaceFilter filter) {
    List<InterfaceEvent> events = new ArrayList<>();

    for (InterfaceEvent event : allEvents) {
      if (filter.evaluate(event)) {
        events.add(event);
      }
    }

    return events;
  }

  @Override
  public List<String> export(InterfaceExportFormat exportFormat) {
    List<String> result = new ArrayList<>();
    result.add(exportFormat.start());
    for (InterfaceEvent event : allEvents) {
      result.add(event.export(exportFormat));
    }
    result.add(exportFormat.end());
    return result;
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    return !this.filter(new FilterByDateTime(dateTime)).isEmpty();
  }

  // ============== MULTI CALENDAR FUNCTIONS ===============
  @Override
  public InterfaceCalendar adjustedTimeZone(TimeZone newTimeZone) {
    SortedSet<InterfaceEvent> newEvents = new TreeSet<>(allEvents);
    List<InterfaceSeries> newSeries = new ArrayList<>(allSeries);

    for (InterfaceSeries series : newSeries) {
      series.adjustTimeZone(newTimeZone);
    }

    for (InterfaceEvent event : newEvents) {
      if (!event.getTimeZone().equals(newTimeZone)) {
        event.adjustTimeZone(newTimeZone);
      }
    }

    return new CalendarModel.CalendarBuilder().name(name).timeZone(newTimeZone)
        .series(newSeries).events(newEvents).build();
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public InterfaceSeries findSeriesForEvent(String subject, LocalDateTime start) {
    for (InterfaceSeries s : allSeries) {
      if (s.findEvent(subject, start) != null) {
        return s;
      }
    }
    return null;
  }

  // ========== CALENDAR BUILDER ==========

  /**
   * Builds a Calendar model by either creating a copy or creating a new calendar.
   */
  public static class CalendarBuilder {
    private String name;
    private TimeZone timeZone;
    private SortedSet<InterfaceEvent> events;
    private List<InterfaceSeries> series;

    /**
     * Constructor for CalendarBuilder.
     */
    public CalendarBuilder() {
      this.name = "";
      this.timeZone = TimeZone.getTimeZone("America/New_York");
      this.events = new TreeSet<>(Comparator
          .comparing(InterfaceEvent::getStartDateTime)
          .thenComparing(InterfaceEvent::getEndDateTime)
          .thenComparing(InterfaceEvent::getSubject));
      this.series = new ArrayList<>();
    }

    /**
     * Sets the name of the calendar.
     *
     * @param name The name of the calendar.
     * @return This builder.
     */
    public CalendarBuilder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the time zone for the calendar.
     *
     * @param timeZone The time zone to use.
     * @return This builder.
     */
    public CalendarBuilder timeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
      return this;
    }

    /**
     * Sets the events for the calendar.
     *
     * @param events The events to use.
     * @return This builder.
     */
    protected CalendarBuilder events(SortedSet<InterfaceEvent> events) {
      this.events = events;
      return this;
    }

    /**
     * Sets the series for the calendar.
     *
     * @param series The series to use.
     * @return This builder.
     */
    protected CalendarBuilder series(List<InterfaceSeries> series) {
      this.series = series;
      return this;
    }

    /**
     * Builds the calendar instance.
     *
     * @return The built calendar.
     */
    public InterfaceCalendar build() {
      return new CalendarModel(name, timeZone, events, series);
    }
  }
}
