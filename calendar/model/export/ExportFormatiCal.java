package calendar.model.export;

import calendar.model.EventStatus;
import calendar.model.InterfaceEvent;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * ExportFormatICal formats events to the iCalendar (.ics) standard.
 */
public class ExportFormatiCal implements InterfaceExportFormat {
  private final String calendarName;
  private final ZoneId timeZone;

  /**
   * Constructs an ExportFormatiCal instance for exporting
   * calendar data in the iCalendar (.ics) format.
   *
   * @param calendarName the name of the calendar to be included in the export
   * @param timeZone the time zone of the calendar, which determines how events are formatted
   */
  public ExportFormatiCal(String calendarName, TimeZone timeZone) {
    this.calendarName = calendarName;
    this.timeZone = timeZone.toZoneId();
  }

  @Override
  public String start() {
    return String.join("\n",
        "BEGIN:VCALENDAR",
        "VERSION:2.0",
        "PRODID:-//MyCalendarApp//EN",
        "CALSCALE:GREGORIAN",
        "METHOD:PUBLISH",
        "X-WR-CALNAME:" + calendarName,
        "X-WR-TIMEZONE:" + timeZone.getId()
    );
  }

  @Override
  public String end() {
    return "END:VCALENDAR";
  }

  @Override
  public String format(InterfaceEvent event) {
    DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyyMMdd");

    ZonedDateTime start = event.getStartDateTime().atZone(timeZone);
    ZonedDateTime end = event.getEndDateTime().atZone(timeZone);

    boolean isAllDay = start.toLocalTime().equals(LocalTime.of(8, 0))
        && end.toLocalTime().equals(LocalTime.of(17, 0))
        && start.toLocalDate().equals(end.toLocalDate());

    EventStatus status = event.getStatus();
    final boolean isPrivate = status == EventStatus.PRIVATE;

    StringBuilder sb = new StringBuilder();
    sb.append("BEGIN:VEVENT\n");
    sb.append("SUMMARY:").append(event.getSubject()).append("\n");

    if (isAllDay) {
      sb.append("DTSTART;VALUE=DATE:").append(start.format(dateOnly)).append("\n");
      sb.append("DTEND;VALUE=DATE:").append(end.format(dateOnly)).append("\n");
    } else {
      sb.append("DTSTART;TZID=").append(timeZone.getId()).append(":")
          .append(start.format(dtFormat)).append("\n");
      sb.append("DTEND;TZID=").append(timeZone.getId()).append(":")
          .append(end.format(dtFormat)).append("\n");
    }

    if (!event.getDescription().isEmpty()) {
      sb.append("DESCRIPTION:").append(event.getDescription().replace("\n", "\\n")).append("\n");
    }

    if (!event.getLocation().isEmpty()) {
      sb.append("LOCATION:").append(event.getLocation()).append("\n");
    }

    sb.append("CLASS:").append(isPrivate ? "PRIVATE" : "PUBLIC").append("\n");
    sb.append("STATUS:CONFIRMED\n");
    sb.append("END:VEVENT");

    return sb.toString();
  }
}
