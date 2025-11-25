package calendar.controller.guicontroller;

import calendar.model.EventStatus;
import calendar.model.InterfaceEvent;
import java.time.LocalDateTime;

/**
 * Converts an event model into a read-only version.
 */
public class EventAdapter implements InterfaceViewEvent {
  private final InterfaceEvent modelEvent;
  private final boolean repeats;

  /**
   * Initialized the viewer for the event using the event itself.
   *
   * @param modelEvent Event model used to get read-only values.
   * @param repeats True if part of a series, false if otherwise.
   */
  public EventAdapter(InterfaceEvent modelEvent, boolean repeats) {
    this.modelEvent = modelEvent;
    this.repeats = repeats;
  }

  @Override
  public String getSubject() {
    return modelEvent.getSubject();
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return modelEvent.getStartDateTime();
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return modelEvent.getEndDateTime();
  }

  @Override
  public String getLocation() {
    return modelEvent.getLocation();
  }

  @Override
  public String getDescription() {
    return modelEvent.getDescription();
  }

  @Override
  public boolean repeats() {
    return repeats;
  }

  @Override
  public boolean isPrivate() {
    return modelEvent.getStatus() == EventStatus.PRIVATE;
  }

  @Override
  public int compareTo(InterfaceViewEvent other) {
    int startCompare = this.getStartDateTime().compareTo(other.getStartDateTime());
    if (startCompare != 0) {
      return startCompare;
    }
    return this.getSubject().compareTo(other.getSubject());
  }
}
