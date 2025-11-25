package calendar.model.export;

import calendar.model.EventStatus;
import calendar.model.InterfaceEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ExportFormatCsv is responsible for formatting calendar events
 * in a CSV (Comma-Separated Values) format.
 */
public class ExportFormatCsv implements InterfaceExportFormat {
  @Override
  public String format(InterfaceEvent event) {
    String subject = event.getSubject();
    EventStatus status = event.getStatus();
    String description = event.getDescription();
    String location = event.getLocation();

    LocalDate startDate = event.getStartDateTime().toLocalDate();
    LocalDate endDate = event.getEndDateTime().toLocalDate();
    LocalTime startTime = event.getStartDateTime().toLocalTime();
    LocalTime endTime = event.getEndDateTime().toLocalTime();

    boolean isAllDay = startDate.equals(endDate)
        && startTime.equals(LocalTime.of(8, 0))
        && endTime.equals(LocalTime.of(17, 0));

    boolean isPrivate = status == EventStatus.PRIVATE;

    return subject
        + "," + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        + "," + startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        + "," + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        + "," + endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        + "," + isAllDay
        + "," + description
        + "," + location
        + "," + isPrivate;
  }

  @Override
  public String start() {
    return "Subject,Start Date,Start Time,End Date,End Time,"
        + "All Day Event,Description,Location,Private";
  }

  @Override
  public String end() {
    return "";
  }
}
