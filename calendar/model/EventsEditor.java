package calendar.model;

import calendar.model.filter.FilterExactEvent;
import calendar.model.filter.FilterSameStartAndSubject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedSet;

/**
 * Package-protected helper class to be used by CalendarModel.
 * Entails edit implementation for editing events and series as well as helper functions.
 */
class EventsEditor {
  private final SortedSet<InterfaceEvent> allEvents;
  private final List<InterfaceSeries> allSeries;
  private final EventValidator eventValidator;
  private final CalendarModel calendarModel;

  /**
   * Initialized the event editor by loading in the list of events and series and the model.
   * We load in the list of events and series since there is not public getter for these fields
   * (and making one would allow other classes to access and make edits to these fields).
   *
   * @param allEvents     List of all events in the calendar.
   * @param allSeries     List of all the series in the calendar.
   * @param calendarModel Calendar model that is being edited.
   */
  public EventsEditor(SortedSet<InterfaceEvent> allEvents, List<InterfaceSeries> allSeries,
                      CalendarModel calendarModel) {
    this.allEvents = allEvents;
    this.allSeries = allSeries;
    this.calendarModel = calendarModel;
    this.eventValidator = new EventValidator();
  }

  /**
   * Edits a single event. If the event is part of a series and the start time is being
   * edited, the event is removed from the series.
   *
   * @param eventRequest Contains subject, start, end, property
   *                     and new value needed to edit a single event.
   * @throws IllegalArgumentException if event not found or edit would create duplicate
   */
  public void editEvent(EventRequest eventRequest) {
    String subject = eventRequest.getSubject();
    LocalDateTime start = eventRequest.getStart();
    LocalDateTime end = eventRequest.getEnd();
    String property = eventRequest.getProperty();
    String newValue = eventRequest.getNewValue();

    InterfaceEvent event = findEvent(subject, start, end);
    if (event == null) {
      throw new IllegalArgumentException("Event not found.");
    }

    allEvents.remove(event);

    try {
      validateEditForDuplicates(event, property, newValue);

      if (property.equals("start")) {
        InterfaceSeries series = calendarModel.findSeriesForEvent(subject, start);
        if (series != null) {
          series.removeEvent(event);
        }
      }

      event.setProperty(property, newValue);

    } catch (Exception e) {
      allEvents.add(event);
      throw e;
    }

    allEvents.add(event);
  }

  /**
   * Edits all events with the given subject and start time.
   * If a series exists with this subject/start, edits all events in that series starting
   * from the specified date-time. Otherwise, edits all individual events matching the
   * subject and start time.
   *
   * @param eventRequest Contains subject, start, end, property
   *                     and new value needed to edit a single event.
   */
  public void editEvents(EventRequest eventRequest) {
    String subject = eventRequest.getSubject();
    LocalDateTime start = eventRequest.getStart();
    String property = eventRequest.getProperty();
    String newValue = eventRequest.getNewValue();

    InterfaceSeries series = calendarModel.findSeriesForEvent(subject, start);

    if (series != null) {
      editSeriesFromDate(series, start, property, newValue);
    } else {
      editAllMatchingEvents(subject, start, property, newValue);
    }
  }


  /**
   * Edits an entire series of recurring events.
   * If no series exists with the given subject/start, edits all individual events
   * matching the subject and start time.
   *
   * @param eventRequest Contains subject, start, end, property
   *                     and new value needed to edit a single event.
   */
  public void editSeries(EventRequest eventRequest) {
    String subject = eventRequest.getSubject();
    LocalDateTime start = eventRequest.getStart();
    String property = eventRequest.getProperty();
    String newValue = eventRequest.getNewValue();

    InterfaceSeries series = calendarModel.findSeriesForEvent(subject, start);

    if (series != null) {
      eventValidator.validateSeriesPropertyEdit(start, property, newValue);
      series.editSeries(property, newValue);
    } else {
      editAllMatchingEvents(subject, start, property, newValue);
    }
  }

  // ====================== PRIVATE HELPER METHODS ======================

  /**
   * Validates that an edit won't create a duplicate event.
   */
  private void validateEditForDuplicates(InterfaceEvent event, String property, String newValue) {
    if (!property.equals("start") && !property.equals("end") && !property.equals("subject")) {
      return; // No duplicate check needed for other properties
    }

    String newSubject = property.equals("subject") ? newValue : event.getSubject();
    LocalDateTime newStart = property.equals("start")
        ? LocalDateTime.parse(newValue)
        : event.getStartDateTime();
    LocalDateTime newEnd = property.equals("end")
        ? LocalDateTime.parse(newValue)
        : event.getEndDateTime();

    eventValidator.validateEventTimes(newStart, newEnd);

    // Create temporary event to check for duplicates
    InterfaceEvent tempEvent = new SingleEvent.SingleEventBuilder()
        .setSubject(newSubject)
        .setStart(newStart)
        .setEnd(newEnd)
        .build();

    if (allEvents.contains(tempEvent)) {
      throw new IllegalArgumentException("Cannot edit event: would create duplicate event");
    }
  }

  /**
   * Edits all events in a series starting from a specific date.
   */
  private void editSeriesFromDate(InterfaceSeries series, LocalDateTime start,
                                  String property, String newValue) {
    eventValidator.validateSeriesPropertyEdit(start, property, newValue);
    if (property.equals("start")) {
      LocalDateTime newStart = LocalDateTime.parse(newValue);
      InterfaceSeries newSeries = series.editStartStartingFrom(start, newStart);
      allSeries.add(newSeries);
    } else {
      series.editStartingFrom(start, property, newValue);
    }
  }

  /**
   * Edits all individual events matching the given subject and start time.
   */
  private void editAllMatchingEvents(String subject, LocalDateTime start,
                                     String property, String newValue) {
    List<InterfaceEvent> matchingEvents = calendarModel.filter(
        new FilterSameStartAndSubject(subject, start));

    if (matchingEvents.isEmpty()) {
      throw new IllegalArgumentException("Event not found.");
    }

    for (InterfaceEvent event : matchingEvents) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(subject)
          .start(start)
          .end(event.getEndDateTime())
          .property(property)
          .newValue(newValue).build();
      editEvent(request);
    }
  }

  /**
   * Searches for a specific if the subject, start, end time are given.
   * If not, finds the first occurrence of an event with the given subject and start.
   *
   * @param subject Subject of the event to search for.
   * @param start   Start date and time of the event to look for.
   * @param end     End date and time for an event to look for.
   * @return The event object that matches the subject, start, and end (if given).
   */
  private InterfaceEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    eventValidator.validateEventTimes(start, end);
    List<InterfaceEvent> events = calendarModel.filter(new FilterExactEvent(subject, start, end));

    if (events.isEmpty()) {
      return null;
    } else {
      return events.get(0);
    }
  }
}