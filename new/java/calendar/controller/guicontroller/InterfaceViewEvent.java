package calendar.controller.guicontroller;

import java.time.LocalDateTime;

/**
 * Read-only view representation of an event.
 * Decouples the view from the model's InterfaceEvent.
 */
public interface InterfaceViewEvent extends Comparable<InterfaceViewEvent> {
  /**
   * Retrieves the subject of an event.
   *
   * @return Subject of event.
   */
  String getSubject();

  /**
   * Retrieves the start date and time of an event.
   *
   * @return Start date and time as a LocalDateTime object.
   */
  LocalDateTime getStartDateTime();

  /**
   * Retrieves the end date and time of an event.
   *
   * @return End date and time of an event as a LocalDateTime object.
   */
  LocalDateTime getEndDateTime();

  /**
   * Retrieves the location of an event.
   *
   * @return Location of an event, if initialized.
   */
  String getLocation();

  /**
   * Determines whether or not an event is part of a series.
   *
   * @return True if the event is part of a series, false if otherwise.
   */
  boolean repeats();

  /**
   * Retrieves the description of an event if initialized.
   *
   * @return The description of an event.
   */
  String getDescription();

  /**
   * Retrieves the status of an event.
   *
   * @return True if the event is private, false if public.
   */
  boolean isPrivate();

}