package calendar.view.guiView.adapter;

import java.time.LocalDateTime;

/**
 * Implementation of view event - adapter between model and view.
 */
public class ViewEventDaily implements IViewEvent {
  private final String subject;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final String location;

  public ViewEventDaily(String subject, LocalDateTime start, LocalDateTime end, String location) {
    this.subject = subject;
    this.start = start;
    this.end = end;
    this.location = location;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return start;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return end;
  }

  @Override
  public String getLocation() {
    return location;
  }


  @Override
  public int compareTo(IViewEvent other) {
    int startCompare = this.getStartDateTime().compareTo(other.getStartDateTime());
    if (startCompare != 0) {
      return startCompare;
    }
    return this.getSubject().compareTo(other.getSubject());
  }
}