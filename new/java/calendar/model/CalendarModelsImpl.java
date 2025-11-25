package calendar.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Implementation of the InterfaceCalendarModels
 * for managing multiple calendars and their corresponding time zones.
 */
public class CalendarModelsImpl implements InterfaceCalendarModels {
  private final Map<String, InterfaceCalendar> calModels;
  private final Map<String, TimeZone> calTimeZones;
  private InterfaceCalendar activeCalendar;

  /**
   * Constructs a new CalendarModelsImpl.
   */
  public CalendarModelsImpl() {
    calModels = new HashMap<>();
    calTimeZones = new HashMap<>();
  }

  @Override
  public void add(String calendarName, InterfaceCalendar calendar, TimeZone timeZone) {
    if (calendarName.isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be empty");
    }

    checkDuplicateCalendar(calendarName);
    calModels.put(Objects.requireNonNull(calendarName), Objects.requireNonNull(calendar));
    calTimeZones.put(Objects.requireNonNull(calendarName), Objects.requireNonNull(timeZone));
  }

  @Override
  public InterfaceCalendar get(String calendarName) {
    checkExistingCalendar(calendarName);
    return calModels.get(calendarName);
  }

  @Override
  public void setName(String oldName, String newName) {
    checkExistingCalendar(oldName);
    checkDuplicateCalendar(newName);

    TimeZone timeZone = calTimeZones.get(oldName);
    InterfaceCalendar calendar = calModels.get(oldName);
    calendar.setName(newName);

    this.remove(oldName);
    this.add(newName, calendar, timeZone);
  }

  @Override
  public TimeZone getTimeZone(String calendarName) {
    checkExistingCalendar(calendarName);
    return calTimeZones.get(Objects.requireNonNull(calendarName));
  }

  @Override
  public InterfaceCalendar getActiveCalendar() {
    return activeCalendar;
  }

  @Override
  public void setActiveCalendar(String name) {
    this.activeCalendar = this.get(Objects.requireNonNull(name));
  }

  @Override
  public void setTimeZone(String name, TimeZone timeZone) {
    InterfaceCalendar calendar = calModels.get(Objects.requireNonNull(name));
    InterfaceCalendar newCalendar = calendar.adjustedTimeZone(Objects.requireNonNull(timeZone));

    calTimeZones.replace(name, Objects.requireNonNull(timeZone));
    calModels.replace(name, Objects.requireNonNull(newCalendar));
  }

  private void checkExistingCalendar(String calendarName) {
    if (!calModels.containsKey(Objects.requireNonNull(calendarName))) {
      throw new IllegalArgumentException("Calendar '" + calendarName + "' does not exist.");
    }
  }

  private void checkDuplicateCalendar(String calendarName) {
    if (calModels.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar '" + calendarName + "' already exists.");
    }
  }

  private void remove(String calendarName) {
    calModels.remove(Objects.requireNonNull(calendarName));
    calTimeZones.remove(Objects.requireNonNull(calendarName));
  }
}
