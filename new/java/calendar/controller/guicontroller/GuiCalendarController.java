package calendar.controller.guicontroller;

import calendar.controller.InterfaceController;
import calendar.model.CalendarModel;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.model.InterfaceSeries;
import calendar.model.filter.FilterByDate;
import calendar.model.filter.FilterByDateRange;
import calendar.model.filter.FilterBySubject;
import calendar.model.filter.InterfaceFilter;
import calendar.view.gui.InterfaceGuiView;
import calendar.view.gui.eventdialog.EditScope;
import calendar.view.gui.eventdialog.EventFormData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Controller for the GUI calendar application.
 * Coordinates interactions between the model and view components.
 */
public class GuiCalendarController implements Features, InterfaceController {
  private final InterfaceCalendarModels models;
  private final InterfaceGuiView view;
  private final List<String> calendarNames;

  private int currentYear;
  private int currentMonth;
  private LocalDate currentDayView;

  /**
   * Initializes controller for multi-calendar model using GUI view along with creating a
   * default calendar.
   *
   * @param models Multi-calendar model.
   * @param view   GUI Version of a view.
   */
  public GuiCalendarController(InterfaceCalendarModels models, InterfaceGuiView view) {
    this.models = models;
    this.view = view;
    this.calendarNames = new ArrayList<>();
    this.currentYear = LocalDate.now().getYear();
    this.currentMonth = LocalDate.now().getMonthValue();
    this.currentDayView = null;

    initializeDefaultCalendar();
    this.view.addFeatures(this);
  }

  private void initializeDefaultCalendar() {
    TimeZone userTimezone = TimeZone.getDefault();
    InterfaceCalendar defaultCalendar = new CalendarModel.CalendarBuilder()
        .name("default")
        .timeZone(userTimezone)
        .build();

    models.add("default", defaultCalendar, userTimezone);
    models.setActiveCalendar("default");
    calendarNames.add("default");
  }

  @Override
  public void run() {
    refreshCurrentView();
    view.display();
  }

  @Override
  public void handleSwitchCalendar(String calendarName) {
    selectCalendar(calendarName);
    refreshCurrentView();
  }

  @Override
  public void handleAddCalendar(String name, TimeZone timeZone) {
    try {
      createCalendar(name, timeZone);
      selectCalendar(name);
      refreshCurrentView();
    } catch (IllegalArgumentException e) {
      view.showErrorDialog("Error Creating Calendar", e.getMessage());
    }
  }

  @Override
  public void handleEditCalendar(String calendarName, TimeZone timeZone) {
    String currentName = getActiveCalendarName();
    TimeZone currentTimezone = getActiveCalendarTimezone();

    if (calendarName == null) {
      return;
    }

    boolean nameChanged = !currentName.equals(calendarName);
    boolean timezoneChanged = !currentTimezone.equals(timeZone);

    if (!nameChanged && !timezoneChanged) {
      return;
    }

    if (timezoneChanged) {
      models.setTimeZone(currentName, timeZone);
    }

    if (nameChanged) {
      models.setName(currentName, calendarName);
      calendarNames.remove(currentName);
      calendarNames.add(calendarName);
      models.setActiveCalendar(calendarName);
    }

    refreshMonthView();
  }

  @Override
  public void handleAddEvent(EventFormData eventInfo) {
    if (eventInfo == null) {
      return;
    }

    try {
      InterfaceCalendar activeCalendar = models.getActiveCalendar();

      EventRequest.RequestBuilder eventRequestBuilder = new EventRequest.RequestBuilder()
          .subject(eventInfo.getSubject())
          .start(eventInfo.getStart())
          .end(eventInfo.getEnd());

      if (eventInfo.isRepeats()) {
        eventRequestBuilder = eventRequestBuilder
            .pattern(eventInfo.getPattern())
            .termination(eventInfo.getTermination());
        activeCalendar.addRecurringEvent(eventRequestBuilder.build());
      } else {
        activeCalendar.addEvent(eventRequestBuilder.build());
      }

      EventRequest eventRequestMade = eventRequestBuilder.build();
      eventInfo.getOptionalFields().forEach((property, value) -> {
        EventRequest request = new EventRequest.RequestBuilder()
            .copy(eventRequestMade)
            .property(property)
            .newValue(value)
            .build();

        if (eventInfo.isRepeats()) {
          activeCalendar.editSeries(request);
        } else {
          activeCalendar.editEvent(request);
        }
      });

      refreshCurrentView();
    } catch (IllegalArgumentException e) {
      view.showErrorDialog("Error Creating Event", e.getMessage());
    }
  }

  @Override
  public void handleEditEventsWithSameName(String eventName, EventFormData updatedInfo,
                                           boolean updateSubject,
                                           boolean updateStart, boolean updateEnd) {
    if (updatedInfo == null) {
      return;
    }

    try {
      InterfaceCalendar activeCalendar = models.getActiveCalendar();

      InterfaceFilter filter = new FilterBySubject(eventName);
      List<InterfaceEvent> matchingEvents = activeCalendar.filter(filter);

      if (matchingEvents.isEmpty()) {
        view.showErrorDialog("No Events Found",
            "No events found with the name: " + eventName);
        return;
      }

      for (InterfaceEvent event : matchingEvents) {
        EventRequest.RequestBuilder requestBuilder = new EventRequest.RequestBuilder()
            .subject(event.getSubject())
            .start(event.getStartDateTime())
            .end(event.getEndDateTime());

        if (updateSubject) {
          EventRequest request = requestBuilder
              .property("subject")
              .newValue(updatedInfo.getSubject())
              .build();
          activeCalendar.editEvent(request);
          requestBuilder.subject(updatedInfo.getSubject());
        }

        if (updateStart) {
          LocalDateTime newStart = LocalDateTime.of(event.getStartDateTime().toLocalDate(),
              updatedInfo.getStart().toLocalTime());

          EventRequest request = requestBuilder
              .property("start")
              .newValue(newStart.toString())
              .build();
          activeCalendar.editEvent(request);
          requestBuilder.start(newStart);
        }

        if (updateEnd) {
          LocalDateTime newEnd = LocalDateTime.of(event.getEndDateTime().toLocalDate(),
              updatedInfo.getEnd().toLocalTime());

          EventRequest request = requestBuilder
              .property("end")
              .newValue(newEnd.toString())
              .build();
          activeCalendar.editEvent(request);
          requestBuilder.end(newEnd);
        }

        EventRequest finalRequest = requestBuilder.build();
        updateEventFields(finalRequest, updatedInfo, activeCalendar, EditScope.SINGLE);
      }

      refreshMonthView();
    } catch (IllegalArgumentException e) {
      view.showErrorDialog("Error Editing Events", e.getMessage());
    }
  }

  @Override
  public void handleEditEvent(InterfaceViewEvent viewEvent, EventFormData updatedInfo,
                              EditScope scope) {
    if (updatedInfo == null) {
      return;
    }

    try {
      InterfaceCalendar activeCalendar = models.getActiveCalendar();

      switch (scope) {
        case ALL:
          editAllEventsInSeries(viewEvent, updatedInfo, activeCalendar);
          break;
        case THIS_AND_FOLLOWING:
          editThisAndFollowingEvents(viewEvent, updatedInfo, activeCalendar);
          break;
        default:
          editSingleEvent(viewEvent, updatedInfo, activeCalendar);
          break;
      }

      refreshCurrentView();
    } catch (IllegalArgumentException e) {
      view.showErrorDialog("Error Editing Event", e.getMessage());
    }
  }

  private void editSingleEvent(InterfaceViewEvent viewEvent, EventFormData updatedInfo,
                               InterfaceCalendar activeCalendar) {
    String currentSubject = viewEvent.getSubject();
    LocalDateTime currentStart = viewEvent.getStartDateTime();
    LocalDateTime currentEnd = viewEvent.getEndDateTime();

    boolean subjectChanged = !currentSubject.equals(updatedInfo.getSubject());
    boolean startChanged = !currentStart.equals(updatedInfo.getStart());
    boolean endChanged = !currentEnd.equals(updatedInfo.getEnd());

    EventRequest.RequestBuilder requestBuilder = new EventRequest.RequestBuilder()
        .subject(currentSubject)
        .start(currentStart)
        .end(currentEnd);

    if (subjectChanged) {
      EventRequest request = requestBuilder
          .property("subject")
          .newValue(updatedInfo.getSubject())
          .build();
      activeCalendar.editEvent(request);
      requestBuilder.subject(updatedInfo.getSubject());
    }

    if (startChanged) {
      EventRequest request = requestBuilder
          .property("start")
          .newValue(updatedInfo.getStart().toString())
          .build();
      activeCalendar.editEvent(request);
      requestBuilder.start(updatedInfo.getStart());
    }

    if (endChanged) {
      EventRequest request = requestBuilder
          .property("end")
          .newValue(updatedInfo.getEnd().toString())
          .build();
      activeCalendar.editEvent(request);
      requestBuilder.end(updatedInfo.getEnd());
    }

    updateEventFields(requestBuilder.build(), updatedInfo, activeCalendar, EditScope.SINGLE);
  }

  private void editAllEventsInSeries(InterfaceViewEvent viewEvent, EventFormData updatedInfo,
                                     InterfaceCalendar activeCalendar) {
    String originalSubject = viewEvent.getSubject();
    LocalDateTime originalStart = viewEvent.getStartDateTime();

    EventRequest.RequestBuilder requestBuilder = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(originalStart);

    if (!originalSubject.equals(updatedInfo.getSubject())) {
      EventRequest request = requestBuilder
          .property("subject")
          .newValue(updatedInfo.getSubject())
          .build();
      activeCalendar.editSeries(request);
      requestBuilder.subject(updatedInfo.getSubject());
    }

    if (!viewEvent.getStartDateTime().equals(updatedInfo.getStart())) {
      EventRequest request = requestBuilder
          .property("start")
          .newValue(updatedInfo.getStart().toString())
          .build();
      activeCalendar.editSeries(request);
      requestBuilder.start(updatedInfo.getStart());
    }

    if (!viewEvent.getEndDateTime().equals(updatedInfo.getEnd())) {
      EventRequest request = requestBuilder
          .property("end")
          .newValue(updatedInfo.getEnd().toString())
          .build();
      activeCalendar.editSeries(request);
    }

    updateEventFields(requestBuilder.build(), updatedInfo, activeCalendar, EditScope.ALL);
  }

  private void editThisAndFollowingEvents(InterfaceViewEvent viewEvent, EventFormData updatedInfo,
                                          InterfaceCalendar activeCalendar) {
    String originalSubject = viewEvent.getSubject();
    LocalDateTime eventStart = viewEvent.getStartDateTime();

    EventRequest.RequestBuilder requestBuilder = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(eventStart);

    if (!originalSubject.equals(updatedInfo.getSubject())) {
      EventRequest request = requestBuilder
          .property("subject")
          .newValue(updatedInfo.getSubject())
          .build();
      activeCalendar.editEvents(request);
      requestBuilder.subject(updatedInfo.getSubject());
    }

    if (!viewEvent.getStartDateTime().equals(updatedInfo.getStart())) {
      EventRequest request = requestBuilder
          .property("start")
          .newValue(updatedInfo.getStart().toString())
          .build();
      activeCalendar.editEvents(request);
      requestBuilder.start(updatedInfo.getStart());
    }

    if (!viewEvent.getEndDateTime().equals(updatedInfo.getEnd())) {
      EventRequest request = requestBuilder
          .property("end")
          .newValue(updatedInfo.getEnd().toString())
          .build();
      activeCalendar.editEvents(request);
    }

    updateEventFields(requestBuilder.build(), updatedInfo, activeCalendar,
        EditScope.THIS_AND_FOLLOWING);
  }

  private void updateEventFields(EventRequest eventRequestMade, EventFormData updatedInfo,
                                 InterfaceCalendar activeCalendar, EditScope scope) {
    updatedInfo.getOptionalFields().forEach((property, value) -> {
      EventRequest request = new EventRequest.RequestBuilder()
          .copy(eventRequestMade)
          .property(property)
          .newValue(value)
          .build();

      if (scope == EditScope.THIS_AND_FOLLOWING) {
        activeCalendar.editEvents(request);
      } else if (scope == EditScope.ALL) {
        activeCalendar.editSeries(request);
      } else {
        activeCalendar.editEvent(request);
      }
    });
  }

  @Override
  public void nextMonth() {
    currentMonth++;
    if (currentMonth > 12) {
      currentMonth = 1;
      currentYear++;
    }
    refreshMonthView();
  }

  @Override
  public void previousMonth() {
    currentMonth--;
    if (currentMonth < 1) {
      currentMonth = 12;
      currentYear--;
    }
    refreshMonthView();
  }

  @Override
  public void selectDay(LocalDate date) {
    currentDayView = date;
    refreshDayView();
  }

  /**
   * Central method to refresh whichever view is currently active.
   * If in day view, refresh day view. Otherwise, refresh month view.
   */
  private void refreshCurrentView() {
    if (currentDayView != null) {
      refreshDayView();
    } else {
      refreshMonthView();
    }
  }

  /**
   * Refreshes the month view with current month's events.
   */
  private void refreshMonthView() {
    currentDayView = null; // Clear day view state
    Map<LocalDate, List<InterfaceViewEvent>> monthEvents = loadEventsForMonth();
    view.renderMonth(currentYear, currentMonth, getActiveCalendarName(),
        getActiveCalendarTimezone().getID(), monthEvents);
  }

  /**
   * Refreshes the day view with current day's events.
   */
  private void refreshDayView() {
    List<InterfaceEvent> modelEvents = getEventsOnDay(currentDayView);
    List<InterfaceViewEvent> viewEvents = convertToViewEvents(modelEvents);
    view.renderDay(currentDayView, viewEvents, loadEventsForMonth());
  }

  /**
   * Converts model events to view events with series information.
   */
  private List<InterfaceViewEvent> convertToViewEvents(List<InterfaceEvent> modelEvents) {
    return modelEvents.stream()
        .map(e -> {
          InterfaceSeries series = models.getActiveCalendar()
              .findSeriesForSpecificEvent(e.getSubject(), e.getStartDateTime(), e.getEndDateTime());

          // This checks to see if event actually matches with event in series.
          boolean isRepeating = series != null;

          return new EventAdapter(e, isRepeating);
        })
        .collect(Collectors.toList());
  }

  private Map<LocalDate, List<InterfaceViewEvent>> loadEventsForMonth() {
    InterfaceCalendar activeCalendar = models.getActiveCalendar();

    LocalDate firstOfMonth = LocalDate.of(currentYear, currentMonth, 1);
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
    LocalDate startDate = firstOfMonth.minusDays(firstDayOfWeek);
    LocalDate endDate = startDate.plusDays(41);

    List<InterfaceEvent> events = activeCalendar.filter(new FilterByDateRange(startDate, endDate));

    Map<LocalDate, List<InterfaceViewEvent>> eventsMap = new HashMap<>();
    for (InterfaceEvent event : events) {
      LocalDate eventStart = event.getStartDateTime().toLocalDate();
      LocalDate eventEnd = event.getEndDateTime().toLocalDate();

      // Checks for date where the start of an event is before the first date of the month view
      LocalDate currentDate = eventStart.isBefore(startDate) ? startDate : eventStart;

      // Checks for date where the end of an event is after the last date of the month view
      LocalDate lastDate = eventEnd.isAfter(endDate) ? endDate : eventEnd;

      boolean isRepeating = getActiveCalendar().findSeriesForEvent(
          event.getSubject(), event.getStartDateTime()) != null;

      while (!currentDate.isAfter(lastDate)) {
        eventsMap.computeIfAbsent(currentDate, k -> new ArrayList<>())
            .add(new EventAdapter(event, isRepeating));
        currentDate = currentDate.plusDays(1);
      }
    }

    return eventsMap;
  }

  private List<InterfaceEvent> getEventsOnDay(LocalDate date) {
    InterfaceCalendar activeCalendar = models.getActiveCalendar();
    return activeCalendar.filter(new FilterByDate(date));
  }

  @Override
  public InterfaceCalendar getActiveCalendar() {
    return models.getActiveCalendar();
  }

  @Override
  public List<String> getAllCalendarNames() {
    return new ArrayList<>(calendarNames);
  }

  @Override
  public String getActiveCalendarName() {
    InterfaceCalendar activeCalendar = models.getActiveCalendar();
    return activeCalendar.getName();
  }

  @Override
  public TimeZone getActiveCalendarTimezone() {
    String activeCalendarName = getActiveCalendarName();
    return models.getTimeZone(activeCalendarName);
  }

  private void createCalendar(String calendarName, TimeZone timezone) {
    InterfaceCalendar newCalendar = new CalendarModel.CalendarBuilder()
        .name(calendarName)
        .timeZone(timezone)
        .build();

    models.add(calendarName, newCalendar, timezone);
    calendarNames.add(calendarName);
  }

  private void selectCalendar(String calendarName) {
    models.setActiveCalendar(calendarName);
  }
}