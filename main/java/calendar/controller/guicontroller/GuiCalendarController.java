package calendar.controller.guicontroller;

import static javax.swing.SwingUtilities.getWindowAncestor;

import calendar.controller.InterfaceController;
import calendar.model.CalendarModel;
import calendar.model.EventRequest;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.model.InterfaceSeries;
import calendar.model.filter.FilterByDate;
import calendar.model.filter.FilterBySubject;
import calendar.model.filter.InterfaceFilter;
import calendar.view.guiView.InterfaceGuiView;
import calendar.view.guiView.adapter.IViewEvent;
import calendar.view.guiView.adapter.ViewEventDaily;
import calendar.view.guiView.createEventView.CreateEventDialog;
import calendar.view.guiView.editEventsSameNameView.EditEventsSameNameDialog;
import calendar.view.guiView.editEventView.EditEventDialog;
import java.awt.Component;
import java.awt.Frame;
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
   * Creates a new GUI calendar controller.
   *
   * @param models the calendar models
   * @param view the GUI view
   */
  public GuiCalendarController(InterfaceCalendarModels models, InterfaceGuiView view) {
    this.models = models;
    this.view = view;
    this.calendarNames = new ArrayList<>();
    this.currentYear = LocalDate.now().getYear();
    this.currentMonth = LocalDate.now().getMonthValue();
    this.currentDayView = null;  // No day selected initially

    // Initialize default calendar with user's timezone BEFORE adding features to view
    initializeDefaultCalendar();

    // Add features to view (this will wire up action events and update display)
    this.view.addFeatures(this);
  }

  /**
   * Initializes a default calendar with the user's current timezone.
   * Creates and adds a "default" calendar to the models, then sets it as active.
   */
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
    // Load events for the initial month
    loadEventsForMonth();
    view.display();
  }

  @Override
  public void handleSwitchCalendar() {
    String selectedCalendar = view.showCalendarSelectorDialog(getAllCalendarNames());

    if (selectedCalendar == null) {
      return;
    }

    if (selectedCalendar.equals("::ADD_NEW::")) {
      handleAddCalendar();
      return;
    }

    selectCalendar(selectedCalendar);

    // Update view with new calendar info
    view.refresh();
    view.setMonthYear(currentYear, currentMonth);
    loadEventsForMonth();
  }

  @Override
  public void handleAddCalendar() {
    // Ask view to show add dialog
    InterfaceGuiView.CalendarInfo calendarInfo = view.showAddCalendarDialog();

    if (calendarInfo == null) {
      // User cancelled
      return;
    }

    try {
      // Create the calendar in the model
      createCalendar(calendarInfo.name, calendarInfo.timezone);

      // Select it as active
      selectCalendar(calendarInfo.name);

      // Update view
      view.setMonthYear(currentYear, currentMonth);
      loadEventsForMonth();
    } catch (IllegalArgumentException e) {
      showErrorDialog("Error Creating Calendar", e.getMessage());
    }
  }

  @Override
  public void handleEditCalendar() {
    // Get current calendar info
    String currentName = getActiveCalendarName();
    TimeZone currentTimezone = getActiveCalendarTimezone();

    // Ask view to show edit dialog
    InterfaceGuiView.CalendarInfo updatedInfo =
        view.showEditCalendarDialog(currentName, currentTimezone);

    if (updatedInfo == null) {
      // User cancelled
      return;
    }

    // Check if name changed
    boolean nameChanged = !currentName.equals(updatedInfo.name);
    boolean timezoneChanged = !currentTimezone.equals(updatedInfo.timezone);

    if (!nameChanged && !timezoneChanged) {
      // Nothing changed
      return;
    }

    // Handle timezone change
    if (timezoneChanged) {
      models.setTimeZone(currentName, updatedInfo.timezone);
    }

    // Handle name change (do this last since it affects the key)
    if (nameChanged) {
      models.setName(currentName, updatedInfo.name);
      // Update the calendar names list
      calendarNames.remove(currentName);
      calendarNames.add(updatedInfo.name);
      // Set the renamed calendar as active
      models.setActiveCalendar(updatedInfo.name);
    }

    // Update view
    view.refresh();
    view.setMonthYear(currentYear, currentMonth);
    loadEventsForMonth();
  }

  @Override
  public void handleAddSingleEvent() {
    // Determine the date to pre-populate in the dialog
    LocalDate eventDate = currentDayView != null ? currentDayView : LocalDate.now();

    // Create the dialog
    CreateEventDialog dialog = new CreateEventDialog(
        (Frame) getWindowAncestor(
            (Component) view),
        eventDate
    );

    // Connect the listener (Controller talks to View through listener)
    CreateEventListener listener = new CreateEventListener(dialog, this);
    dialog.addActionListener(listener);

    // Show dialog (blocks until user closes it)
    dialog.setVisible(true);

    // Check if user confirmed
    if (!dialog.isConfirmed()) {
      return;
    }

    // Get event info from dialog
    CreateEventDialog.EventInfo eventInfo = dialog.getEventInfo();
    if (eventInfo == null) {
      return;
    }

    try {
      // Get the active calendar
      InterfaceCalendar activeCalendar = models.getActiveCalendar();

      // Check if this is a recurring event or single event
      if (eventInfo.repeats) {
        // Create recurring event series
        EventRequest eventRequest = new EventRequest.RequestBuilder()
            .subject(eventInfo.subject)
            .start(eventInfo.start)
            .end(eventInfo.end)
            .pattern(eventInfo.pattern)
            .termination(eventInfo.termination)
            .build();

        activeCalendar.addRecurringEvent(eventRequest);

        // Add additional fields to all events in the series using editEvents
        if (!eventInfo.location.isEmpty()) {
          EventRequest locationRequest = new EventRequest.RequestBuilder()
              .subject(eventInfo.subject)
              .start(eventInfo.start)
              .property("location")
              .newValue(eventInfo.location)
              .build();
          activeCalendar.editSeries(locationRequest);
        }

        if (!eventInfo.description.isEmpty()) {
          EventRequest descriptionRequest = new EventRequest.RequestBuilder()
              .subject(eventInfo.subject)
              .start(eventInfo.start)
              .property("description")
              .newValue(eventInfo.description)
              .build();
          activeCalendar.editSeries(descriptionRequest);
        }

        if (eventInfo.isPrivate) {
          EventRequest privateRequest = new EventRequest.RequestBuilder()
              .subject(eventInfo.subject)
              .start(eventInfo.start)
              .property("status")
              .newValue("PRIVATE")
              .build();
          activeCalendar.editSeries(privateRequest);
        }

      } else {
        // Create single event
        EventRequest eventRequest = new EventRequest.RequestBuilder()
            .subject(eventInfo.subject)
            .start(eventInfo.start)
            .end(eventInfo.end)
            .build();

        activeCalendar.addEvent(eventRequest);

        // Now add the additional fields using editEvent
        if (!eventInfo.location.isEmpty()) {
          EventRequest locationRequest = new EventRequest.RequestBuilder()
              .subject(eventInfo.subject)
              .start(eventInfo.start)
              .end(eventInfo.end)
              .property("location")
              .newValue(eventInfo.location)
              .build();
          activeCalendar.editEvent(locationRequest);
        }

        if (!eventInfo.description.isEmpty()) {
          EventRequest descriptionRequest = new EventRequest.RequestBuilder()
              .subject(eventInfo.subject)
              .start(eventInfo.start)
              .end(eventInfo.end)
              .property("description")
              .newValue(eventInfo.description)
              .build();
          activeCalendar.editEvent(descriptionRequest);
        }

        if (eventInfo.isPrivate) {
          EventRequest privateRequest = new EventRequest.RequestBuilder()
              .subject(eventInfo.subject)
              .start(eventInfo.start)
              .end(eventInfo.end)
              .property("status")
              .newValue("PRIVATE")
              .build();
          activeCalendar.editEvent(privateRequest);
        }
      }

      // Refresh the month view
      loadEventsForMonth();

      // If currently viewing a day, refresh the day view too
      if (currentDayView != null) {
        refreshDayView();
      }
    } catch (IllegalArgumentException e) {
      // Show error message to user
      showErrorDialog("Error Creating Event", e.getMessage());
    }
  }

  @Override
  public void handleAddEventSeries() {

  }

  @Override
  public void handleEditEventsWithSameName() {
    // Show dialog to get event name and property to edit
    EditEventsSameNameDialog.EditInfo editInfo =
        view.showEditEventsSameNameDialog();

    if (editInfo == null) {
      // User cancelled
      return;
    }

    try {
      InterfaceCalendar activeCalendar = models.getActiveCalendar();

      // Find all events with the same subject (name)
      InterfaceFilter filter = new FilterBySubject(editInfo.eventName);
      List<InterfaceEvent> matchingEvents = activeCalendar.filter(filter);

      if (matchingEvents.isEmpty()) {
        showErrorDialog("No Events Found",
            "No events found with the name: " + editInfo.eventName);
        return;
      }

      // Edit each matching event individually
      for (InterfaceEvent event : matchingEvents) {
        EventRequest request = new EventRequest.RequestBuilder()
            .subject(event.getSubject())
            .start(event.getStartDateTime())
            .end(event.getEndDateTime())
            .property(editInfo.property)
            .newValue(editInfo.newValue)
            .build();

        activeCalendar.editEvent(request);
      }

      // Refresh views
      loadEventsForMonth();
      if (currentDayView != null) {
        refreshDayView();
      }
    } catch (IllegalArgumentException e) {
      showErrorDialog("Error Editing Events", e.getMessage());
    }
  }

  @Override
  public void handleEditEvent(IViewEvent viewEvent) {
    // Create the edit dialog with current event data
    EditEventDialog dialog = new EditEventDialog(
        (Frame) getWindowAncestor((Component) view),
        viewEvent
    );

    // Connect the listener (Controller talks to View through listener)
    EditEventListener listener = new EditEventListener(dialog);
    dialog.addActionListener(listener);

    // Show dialog (blocks until user closes it)
    dialog.setVisible(true);

    // Check if user confirmed
    if (!dialog.isConfirmed()) {
      return;
    }

    // Get updated event info from dialog
    EditEventDialog.EventInfo updatedInfo = dialog.getEventInfo();
    if (updatedInfo == null) {
      return;
    }

    try {
      InterfaceCalendar activeCalendar = models.getActiveCalendar();

      // Handle based on edit scope
      switch (updatedInfo.editScope) {
        case SINGLE:
          editSingleEvent(viewEvent, updatedInfo, activeCalendar);
          break;
        case ALL:
          editAllEventsInSeries(viewEvent, updatedInfo, activeCalendar);
          break;
        case THIS_AND_FOLLOWING:
          editThisAndFollowingEvents(viewEvent, updatedInfo, activeCalendar);
          break;
      }

      // Refresh views
      loadEventsForMonth();
      if (currentDayView != null) {
        refreshDayView();
      }
      refreshEventDetails(updatedInfo.subject, updatedInfo.start, updatedInfo.end);
    } catch (IllegalArgumentException e) {
      showErrorDialog("Error Editing Event", e.getMessage());
    }
  }

  /**
   * Edits a single event (not part of series or only this occurrence).
   */
  private void editSingleEvent(IViewEvent viewEvent, EditEventDialog.EventInfo updatedInfo,
                               InterfaceCalendar activeCalendar) {
    // Track the current subject/start/end as we make changes
    String currentSubject = viewEvent.getSubject();
    LocalDateTime currentStart = viewEvent.getStartDateTime();
    LocalDateTime currentEnd = viewEvent.getEndDateTime();

    // Check what changed
    boolean subjectChanged = !currentSubject.equals(updatedInfo.subject);
    boolean startChanged = !currentStart.equals(updatedInfo.start);
    boolean endChanged = !currentEnd.equals(updatedInfo.end);

    // Update subject first if changed
    if (subjectChanged) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(currentSubject)
          .start(currentStart)
          .end(currentEnd)
          .property("subject")
          .newValue(updatedInfo.subject)
          .build();
      activeCalendar.editEvent(request);
      currentSubject = updatedInfo.subject;
    }

    // Update start time if changed
    if (startChanged) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(currentSubject)
          .start(currentStart)
          .end(currentEnd)
          .property("start")
          .newValue(updatedInfo.start.toString())
          .build();
      activeCalendar.editEvent(request);
      currentStart = updatedInfo.start;
    }

    // Update end time if changed
    if (endChanged) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(currentSubject)
          .start(currentStart)
          .end(currentEnd)
          .property("end")
          .newValue(updatedInfo.end.toString())
          .build();
      activeCalendar.editEvent(request);
      currentEnd = updatedInfo.end;
    }

    // Update other fields
    updateEventFields(currentSubject, currentStart, currentEnd, updatedInfo, activeCalendar);
  }

  /**
   * Edits all events in the series using the model's editSeries() method.
   * This updates all events with the same subject in the series.
   */
  private void editAllEventsInSeries(IViewEvent viewEvent, EditEventDialog.EventInfo updatedInfo,
                                     InterfaceCalendar activeCalendar) {
    String originalSubject = viewEvent.getSubject();
    LocalDateTime originalStart = viewEvent.getStartDateTime();

    // Use editSeries to update all events with the same subject
    // The model's editSeries() handles finding and updating all events in the series

    // Update subject if changed
    if (!originalSubject.equals(updatedInfo.subject)) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(originalSubject)
          .start(originalStart)
          .property("subject")
          .newValue(updatedInfo.subject)
          .build();
      activeCalendar.editSeries(request);
      originalSubject = updatedInfo.subject;
    }

    // Update start time (time portion only, dates stay the same in series)
    if (!viewEvent.getStartDateTime().equals(updatedInfo.start)) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(originalSubject)
          .start(originalStart)
          .property("start")
          .newValue(updatedInfo.start.toString())
          .build();
      activeCalendar.editSeries(request);
      originalStart = updatedInfo.start;
    }

    // Update end time (time portion only, dates stay the same in series)
    if (!viewEvent.getEndDateTime().equals(updatedInfo.end)) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(originalSubject)
          .start(originalStart)
          .property("end")
          .newValue(updatedInfo.end.toString())
          .build();
      activeCalendar.editSeries(request);
    }

    // Update location for all events in series
    EventRequest locationRequest = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(originalStart)
        .property("location")
        .newValue(updatedInfo.location)
        .build();
    activeCalendar.editSeries(locationRequest);

    // Update description for all events in series
    EventRequest descriptionRequest = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(originalStart)
        .property("description")
        .newValue(updatedInfo.description)
        .build();
    activeCalendar.editSeries(descriptionRequest);

    // Update privacy status for all events in series
    EventRequest privateRequest = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(originalStart)
        .property("status")
        .newValue(updatedInfo.isPrivate ? "PRIVATE" : "PUBLIC")
        .build();
    activeCalendar.editSeries(privateRequest);
  }

  /**
   * Edits this event and all following events using the model's editEvents() method.
   * This updates all events with the same subject that occur on or after this event.
   */
  private void editThisAndFollowingEvents(IViewEvent viewEvent, EditEventDialog.EventInfo updatedInfo,
                                          InterfaceCalendar activeCalendar) {
    String originalSubject = viewEvent.getSubject();
    LocalDateTime eventStart = viewEvent.getStartDateTime();

    // The model's editEvents() method can handle filtering by start date/time
    // We need to pass the original subject and start time to identify which events to edit

    // For "this and following" edits, we need to:
    // 1. Update subject if changed (affects all matching events from this point forward)
    if (!originalSubject.equals(updatedInfo.subject)) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(originalSubject)
          .start(eventStart)
          .property("subject")
          .newValue(updatedInfo.subject)
          .build();
      activeCalendar.editEvents(request);
      originalSubject = updatedInfo.subject;
    }

    // 2. Update start time for this and following events
    if (!viewEvent.getStartDateTime().equals(updatedInfo.start)) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(originalSubject)
          .start(eventStart)
          .property("start")
          .newValue(updatedInfo.start.toString())
          .build();
      activeCalendar.editEvents(request);
      eventStart = updatedInfo.start;
    }

    // 3. Update end time for this and following events
    if (!viewEvent.getEndDateTime().equals(updatedInfo.end)) {
      EventRequest request = new EventRequest.RequestBuilder()
          .subject(originalSubject)
          .start(eventStart)
          .property("end")
          .newValue(updatedInfo.end.toString())
          .build();
      activeCalendar.editEvents(request);
    }

    // 4. Update location for this and following events
    EventRequest locationRequest = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(eventStart)
        .property("location")
        .newValue(updatedInfo.location)
        .build();
    activeCalendar.editEvents(locationRequest);

    // 5. Update description for this and following events
    EventRequest descriptionRequest = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(eventStart)
        .property("description")
        .newValue(updatedInfo.description)
        .build();
    activeCalendar.editEvents(descriptionRequest);

    // 6. Update privacy status for this and following events
    EventRequest privateRequest = new EventRequest.RequestBuilder()
        .subject(originalSubject)
        .start(eventStart)
        .property("status")
        .newValue(updatedInfo.isPrivate ? "PRIVATE" : "PUBLIC")
        .build();
    activeCalendar.editEvents(privateRequest);
  }

  /**
   * Helper method to update location, description, and privacy fields for a single event.
   * Used by editSingleEvent().
   */
  private void updateEventFields(String subject, LocalDateTime start, LocalDateTime end,
                                 EditEventDialog.EventInfo updatedInfo,
                                 InterfaceCalendar activeCalendar) {
    // Update location
    EventRequest locationRequest = new EventRequest.RequestBuilder()
        .subject(subject)
        .start(start)
        .end(end)
        .property("location")
        .newValue(updatedInfo.location)
        .build();
    activeCalendar.editEvent(locationRequest);

    // Update description
    EventRequest descriptionRequest = new EventRequest.RequestBuilder()
        .subject(subject)
        .start(start)
        .end(end)
        .property("description")
        .newValue(updatedInfo.description)
        .build();
    activeCalendar.editEvent(descriptionRequest);

    // Update private flag
    EventRequest privateRequest = new EventRequest.RequestBuilder()
        .subject(subject)
        .start(start)
        .end(end)
        .property("status")
        .newValue(updatedInfo.isPrivate ? "PRIVATE" : "PUBLIC")
        .build();
    activeCalendar.editEvent(privateRequest);
  }

  // MARK: NAVIGATION

  @Override
  public void nextMonth() {
    currentMonth++;
    if (currentMonth > 12) {
      currentMonth = 1;
      currentYear++;
    }
    updateMonthView();
  }

  @Override
  public void previousMonth() {
    currentMonth--;
    if (currentMonth < 1) {
      currentMonth = 12;
      currentYear--;
    }
    updateMonthView();
  }

  /**
   * Updates the view to display the current month and loads events.
   */
  private void updateMonthView() {
    view.setMonthYear(currentYear, currentMonth);
    loadEventsForMonth();
    view.hideEventDetails();
  }

  @Override
  public void loadEventsForMonth() {
    InterfaceCalendar activeCalendar = models.getActiveCalendar();
    if (activeCalendar == null) {
      view.setMonthEvents(new HashMap<>());
      return;
    }

    // Calculate date range for the month view
    // The calendar grid shows 6 weeks (42 days), starting from the first day shown
    LocalDate firstOfMonth = LocalDate.of(currentYear, currentMonth, 1);
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
    LocalDate startDate = firstOfMonth.minusDays(firstDayOfWeek);
    LocalDate endDate = startDate.plusDays(41); // 42 days total (6 weeks)

    // Filter events for this date range
    calendar.model.filter.FilterByDateRange filter =
        new calendar.model.filter.FilterByDateRange(startDate, endDate);
    List<InterfaceEvent> events = activeCalendar.filter(filter);

    // Group events by date
    // For multi-day events, add them to each date they span
    Map<LocalDate, List<IViewEvent>> eventsMap = new HashMap<>();
    for (InterfaceEvent event : events) {
      LocalDate eventStart = event.getStartDateTime().toLocalDate();
      LocalDate eventEnd = event.getEndDateTime().toLocalDate();

      // Add event to each date it spans (within the visible range)
      LocalDate currentDate = eventStart.isBefore(startDate) ? startDate : eventStart;
      LocalDate lastDate = eventEnd.isAfter(endDate) ? endDate : eventEnd;

      while (!currentDate.isAfter(lastDate)) {
        eventsMap.computeIfAbsent(currentDate, k -> new ArrayList<>()).add(new EventAdapter(event));
        currentDate = currentDate.plusDays(1);
      }
    }

    // Pass events to view
    view.setMonthEvents(eventsMap);
  }

  @Override
  public void selectDay(LocalDate date) {
    // Track which day is being viewed
    this.currentDayView = date;

    // Get events for this day from model
    List<InterfaceEvent> modelEvents = getEventsOnDay(date);

    // Convert model events to view events (adapter pattern)
    List<IViewEvent> viewEvents = modelEvents.stream()
        .map(e -> {
          // Check if this event is part of a series
          InterfaceSeries series = models.getActiveCalendar()
              .findSeriesForEvent(e.getSubject(), e.getStartDateTime());
          boolean isPartOfSeries = (series != null);

          return new ViewEventDaily(
              e.getSubject(),
              e.getStartDateTime(),
              e.getEndDateTime(),
              e.getLocation(),
              e.getDescription(),
              e.getStatus().toString(),
              isPartOfSeries
          );
        })
        .collect(Collectors.toList());

    // Update view
    view.renderDayEvents(date, viewEvents);
    view.showDayView(date);
  }


  @Override
  public InterfaceCalendar getActiveCalendar() {
    return models.getActiveCalendar();
  }

  // MARK: QUERY METHODS

  @Override
  public List<InterfaceEvent> getEventsOnDay(LocalDate date) {
    InterfaceCalendar activeCalendar = models.getActiveCalendar();
    if (activeCalendar == null) {
      return List.of();
    }
    return activeCalendar.filter(new FilterByDate(date));
  }

  @Override
  public List<String> getAllCalendarNames() {
    return new ArrayList<>(calendarNames);
  }

  @Override
  public String getActiveCalendarName() {
    InterfaceCalendar activeCalendar = models.getActiveCalendar();
    return activeCalendar != null ? activeCalendar.getName() : "";
  }

  @Override
  public TimeZone getActiveCalendarTimezone() {
    String activeCalendarName = getActiveCalendarName();
    if (activeCalendarName.isEmpty()) {
      return TimeZone.getDefault();
    }
    return models.getTimeZone(activeCalendarName);
  }

  // MARK: PRIVATE HELPER METHODS

  /**
   * Creates a new calendar with the specified name and timezone.
   * This is a private helper method used by handleAddCalendar.
   *
   * @param calendarName name of the calendar to create
   * @param timezone timezone for the calendar
   */
  private void createCalendar(String calendarName, TimeZone timezone) {
    InterfaceCalendar newCalendar = new CalendarModel.CalendarBuilder()
        .name(calendarName)
        .timeZone(timezone)
        .build();

    models.add(calendarName, newCalendar, timezone);

    if (!calendarNames.contains(calendarName)) {
      calendarNames.add(calendarName);
    }
  }

  /**
   * Selects and activates a calendar by name.
   * This is a private helper method used by handle methods.
   *
   * @param calendarName name of the calendar to select
   */
  private void selectCalendar(String calendarName) {
    models.setActiveCalendar(calendarName);
  }

  /**
   * Refreshes the currently displayed day view with updated events.
   * Only refreshes the event list without changing which view is shown.
   */
  private void refreshDayView() {
    if (currentDayView == null) {
      return;
    }

    // Get events for the current day from model
    List<InterfaceEvent> modelEvents = getEventsOnDay(currentDayView);

    // Convert model events to view events (adapter pattern)
    List<IViewEvent> viewEvents = modelEvents.stream()
        .map(e -> {
          // Check if this event is part of a series
          InterfaceSeries series = models.getActiveCalendar()
              .findSeriesForEvent(e.getSubject(), e.getStartDateTime());
          boolean isPartOfSeries = (series != null);

          return new ViewEventDaily(
              e.getSubject(),
              e.getStartDateTime(),
              e.getEndDateTime(),
              e.getLocation(),
              e.getDescription(),
              e.getStatus().toString(),
              isPartOfSeries
          );
        })
        .collect(Collectors.toList());

    // Update only the event list, don't switch views
    view.renderDayEvents(currentDayView, viewEvents);
  }
  /**
   * Shows an error dialog to the user with the specified title and message.
   *
   * @param title the title of the error dialog
   * @param message the error message to display
   */
  private void showErrorDialog(String title, String message) {
    javax.swing.JOptionPane.showMessageDialog(
        null,
        message,
        title,
        javax.swing.JOptionPane.ERROR_MESSAGE
    );
  }

  /**
   * Refreshes the event details panel with the updated event information.
   * Finds the event by its subject, start, and end time and updates the details display.
   */
  private void refreshEventDetails(String subject, LocalDateTime start, LocalDateTime end) {
    if (currentDayView == null) {
      return;
    }

    // Find the updated event in the model
    InterfaceCalendar activeCalendar = models.getActiveCalendar();
    List<InterfaceEvent> events = getEventsOnDay(currentDayView);

    for (InterfaceEvent event : events) {
      if (event.getSubject().equals(subject)
          && event.getStartDateTime().equals(start)
          && event.getEndDateTime().equals(end)) {
        // Found the updated event - convert to view event and show details
        IViewEvent viewEvent = new ViewEventDaily(
            event.getSubject(),
            event.getStartDateTime(),
            event.getEndDateTime(),
            event.getLocation(),
            event.getDescription(),
            event.getStatus().toString(),
            activeCalendar.findSeriesForEvent(event.getSubject(), event.getStartDateTime()) != null
        );

        // Update the details panel through the view
        view.refreshEventDetails(viewEvent);
        break;
      }
    }
  }

}
