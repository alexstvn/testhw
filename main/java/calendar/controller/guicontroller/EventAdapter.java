package calendar.controller.guicontroller;

import calendar.model.EventStatus;
import calendar.model.InterfaceEvent;
import calendar.view.guiView.adapter.IViewEvent;
import java.time.LocalDateTime;

public class EventAdapter implements IViewEvent {
  private final InterfaceEvent modelEvent;

  public EventAdapter(InterfaceEvent modelEvent) {
    this.modelEvent = modelEvent;
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
  public boolean isRepeating() {
    return false;
  }

  @Override
  public boolean isPrivate() {
    return modelEvent.getStatus() == EventStatus.PRIVATE;
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
