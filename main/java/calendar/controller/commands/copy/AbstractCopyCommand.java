package calendar.controller.commands.copy;

import static java.time.temporal.ChronoUnit.DAYS;

import calendar.controller.commands.AbstractCommand;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.model.InterfaceSeries;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Abstract class extending AbstractCommand to provide functionality for copying events.
 */
public abstract class AbstractCopyCommand extends AbstractCommand {
  protected InterfaceCalendar sourceCalendar;
  protected InterfaceCalendarModels models;

  /**
   * Initializes the general copying multiple events command.
   *
   * @param models         Models of multiple calendars.
   * @param activeCalendar The active calendar we are currently sourcing from.
   */
  protected AbstractCopyCommand(InterfaceCalendarModels models, InterfaceCalendar activeCalendar) {
    this.sourceCalendar = activeCalendar;
    this.models = models;
  }

  /**
   * Copies series events from a series to the target calendar.
   */
  protected void copyEventSeries(Map<InterfaceSeries, List<InterfaceEvent>> seriesMap,
                                 InterfaceCalendar targetCalendar,
                                 long daysDifference,
                                 TimeZone sourceTimeZone,
                                 TimeZone targetTimeZone) {
    for (Map.Entry<InterfaceSeries, List<InterfaceEvent>> entry : seriesMap.entrySet()) {
      InterfaceSeries fullSeries = entry.getKey();
      List<InterfaceEvent> filteredSeriesEvents = entry.getValue();

      String pattern = fullSeries.getPattern();

      InterfaceEvent firstEvent = filteredSeriesEvents.get(0);

      LocalDateTime newStart =
          translateDateTime(firstEvent.getStartDateTime(), daysDifference, targetTimeZone);
      LocalDateTime newEnd =
          translateDateTime(firstEvent.getEndDateTime(), daysDifference, targetTimeZone);

      int occurrences = filteredSeriesEvents.size();

      EventRequest eventRequest = new EventRequest.RequestBuilder()
          .subject(firstEvent.getSubject())
          .start(newStart)
          .end(newEnd)
          .pattern(pattern)
          .termination(String.valueOf(occurrences))
          .build();

      targetCalendar.addRecurringEvent(eventRequest);
    }
  }

  /**
   * Copies standalone events to the target calendar.
   */
  protected void copyStandaloneEvents(List<InterfaceEvent> standaloneEvents,
                                      InterfaceCalendar targetCalendar,
                                      long daysDifference,
                                      TimeZone targetTimeZone) {
    for (InterfaceEvent event : standaloneEvents) {
      LocalDateTime newStart =
          translateDateTime(event.getStartDateTime(), daysDifference, targetTimeZone);
      LocalDateTime newEnd =
          translateDateTime(event.getEndDateTime(), daysDifference, targetTimeZone);

      EventRequest eventRequest = new EventRequest.RequestBuilder()
          .subject(event.getSubject())
          .start(newStart)
          .end(newEnd)
          .build();
      targetCalendar.addEvent(eventRequest);
    }
  }

  /**
   * Copies many, filtered events with their associated series into the
   * target calendar with new timezone.
   *
   * @param filteredEvents     List of events that were queried to copy into calendar.
   * @param targetCalendarName Name of target calendar to copy events into.
   * @param sourceDate         Retrieves the starting date of the original events.
   * @param targetDate         Date to start copied events from in target calendar.
   */
  protected void executeCopyingManyEvents(List<InterfaceEvent> filteredEvents,
                                          String targetCalendarName,
                                          LocalDate sourceDate,
                                          LocalDate targetDate) {
    InterfaceCalendar targetCalendar = models.get(targetCalendarName);
    TimeZone targetTimeZone = models.getTimeZone(targetCalendarName);
    TimeZone sourceTimeZone = sourceCalendar.getTimeZone();

    long daysDifference = DAYS.between(sourceDate, targetDate);

    Map<InterfaceSeries, List<InterfaceEvent>> seriesMap = new HashMap<>();
    List<InterfaceEvent> standaloneEvents = new ArrayList<>();

    for (InterfaceEvent event : filteredEvents) {
      InterfaceSeries series = sourceCalendar.findSeriesForEvent(
          event.getSubject(), event.getStartDateTime());

      if (series != null) {
        seriesMap.putIfAbsent(series, new ArrayList<>());
        seriesMap.get(series).add(event);
      } else {
        standaloneEvents.add(event);
      }
    }

    copyEventSeries(seriesMap, targetCalendar, daysDifference,
        sourceTimeZone, targetTimeZone);

    copyStandaloneEvents(standaloneEvents, targetCalendar, daysDifference, targetTimeZone);
  }

  /**
   * Translates the datetime of a field into the update start date and timezone.
   *
   * @param original       Field (either start or end) to update.
   * @param daysDifference Amount of days between source date and target date.
   * @param targetTimeZone TimeZone of calendar events are being copied to.
   * @return Updated field that would be in target date and timezone.
   */
  private LocalDateTime translateDateTime(LocalDateTime original, long daysDifference,
                                          TimeZone targetTimeZone) {
    LocalDateTime shiftOriginal = original.plusDays(daysDifference);
    return convertTimezone(shiftOriginal, targetTimeZone);
  }

  /**
   * Convert the timezone of the singleEvent.
   *
   * @param dateTime       LocalDateTime to convert
   * @param targetTimeZone timeZone to convert to
   * @return LocalDateTime
   */
  private LocalDateTime convertTimezone(LocalDateTime dateTime,
                                        TimeZone targetTimeZone) {
    TimeZone sourceTimeZone = sourceCalendar.getTimeZone();

    int sourceOffset = sourceTimeZone.getOffset(
        java.sql.Timestamp.valueOf(dateTime).getTime());
    int targetOffset = targetTimeZone.getOffset(
        java.sql.Timestamp.valueOf(dateTime).getTime());
    int offsetDifference = targetOffset - sourceOffset;

    long hoursDifference = offsetDifference / (1000 * 60 * 60);

    return dateTime.plusHours(hoursDifference);
  }

}
