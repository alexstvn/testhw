package calendar.view.guiView.adapter;

import java.time.LocalDateTime;

/**
 * Read-only view representation of an event.
 * Decouples the view from the model's InterfaceEvent.
 */
public interface IViewEvent extends Comparable<IViewEvent> {
  String getSubject();
  LocalDateTime getStartDateTime();
  LocalDateTime getEndDateTime();
  String getLocation();

}