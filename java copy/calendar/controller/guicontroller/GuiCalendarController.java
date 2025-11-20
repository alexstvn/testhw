package calendar.controller.guicontroller;

import calendar.model.CalendarModel;
import calendar.model.InterfaceCalendar;
import calendar.model.InterfaceCalendarModels;
import calendar.model.InterfaceEvent;
import calendar.view.guiView.InterfaceGuiView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Controller for the GUI calendar application.
 * Coordinates interactions between the model and view components.
 */
public class GuiCalendarController implements Features {
  private final InterfaceCalendarModels models;
  private final InterfaceGuiView view;
  private final List<String> calendarNames;

  private int currentYear;
  private int currentMonth;

  /**
   * Creates a new GUI calendar controller.
   *
   * @param models the calendar models
   * @param view the GUI view
   */
  public GuiCalendarController(InterfaceCalendarModels models, InterfaceGuiView view) {
    this.models = models;
    this.view = view;
    this.calendarNames = new java.util.ArrayList<>();
    this.currentYear = LocalDate.now().getYear();
    this.currentMonth = LocalDate.now().getMonthValue();

    // Initialize default calendar with user's timezone BEFORE adding features to view
    initializeDefaultCalendar();

    // Add features to view (this will wire up action events and update display)
    this.view.addFeatures(this);

    // Note: View coordination now happens through action events
    // MainView wires buttons → fires ActionEvents → calls controller methods
    // No need for callback setup
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
    view.display();
  }

  @Override
  public InterfaceCalendar getActiveCalendar() {
    return models.getActiveCalendar();
  }

  // ===================== ACTION HANDLERS (All Logic Here) =====================

  @Override
  public void handleSwitchCalendar() {
    // Ask view to show dialog with current calendar list
    String selectedCalendar = view.showCalendarSelectorDialog(getAllCalendarNames());

    if (selectedCalendar == null) {
      // User cancelled
      return;
    }

    if (selectedCalendar.equals("::ADD_NEW::")) {
      // User wants to add a new calendar instead
      handleAddCalendar();
      return;
    }

    // User selected a calendar - update model
    selectCalendar(selectedCalendar);

    // Update view
    view.setMonthYear(currentYear, currentMonth);
  }

  @Override
  public void handleAddCalendar() {
    // Ask view to show add dialog
    InterfaceGuiView.CalendarInfo calendarInfo = view.showAddCalendarDialog();

    if (calendarInfo == null) {
      // User cancelled
      return;
    }

    // Create the calendar in the model
    createCalendar(calendarInfo.name, calendarInfo.timezone);

    // Select it as active
    selectCalendar(calendarInfo.name);

    // Update view
    view.setMonthYear(currentYear, currentMonth);
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

    // TODO: Implement edit logic
    // This might involve renaming and/or changing timezone
    System.out.println("Edit calendar logic - to be implemented");
  }

  // ===================== FEATURES =====================

  @Override
  public void createCalendar(String calendarName, TimeZone timezone) {
    // Create a new calendar with the specified name and timezone
    InterfaceCalendar newCalendar = new CalendarModel.CalendarBuilder()
        .name(calendarName)
        .timeZone(timezone)
        .build();

    // Add to models
    models.add(calendarName, newCalendar, timezone);

    // Track the calendar name
    if (!calendarNames.contains(calendarName)) {
      calendarNames.add(calendarName);
    }
  }

  @Override
  public void selectCalendar(String calendarName) {
    // Set the selected calendar as active
    models.setActiveCalendar(calendarName);

    // Update the view to reflect the new active calendar
    String timezone = models.getTimeZone(calendarName).getID();
    view.setMonthYear(currentYear, currentMonth);
  }

  @Override
  public void renameCalendar(String oldName, String newName) {

  }

  @Override
  public void changeCalendarTimezone(String calendarName, TimeZone newTimezone) {

  }

  @Override
  public void createSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {

  }

  @Override
  public void createAllDayEvent(String subject, LocalDate date) {

  }

  @Override
  public void createEventSeries(String subject, LocalDateTime start, LocalDateTime end,
                                String pattern, int occurrences) {

  }

  @Override
  public void createEventSeriesUntil(String subject, LocalDateTime start, LocalDateTime end,
                                     String pattern, LocalDate endDate) {

  }

  @Override
  public void editSingleEvent(String subject, LocalDateTime originalStart, String property,
                              String newValue) {

  }

  @Override
  public void editAllEventsWithSubject(String subject, String property, String newValue) {

  }

  @Override
  public void editEventsFromDate(String subject, LocalDate fromDate, String property,
                                 String newValue) {

  }

  @Override
  public void editSeries(String subject, String property, String newValue) {

  }

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
   * Updates the view to display the current month.
   */
  private void updateMonthView() {
    view.setMonthYear(currentYear, currentMonth);

    // TODO: Load and display events for this month
    // loadEventsForMonth(currentYear, currentMonth);
  }

  @Override
  public void selectDay(LocalDate date) {

  }

  @Override
  public List<InterfaceEvent> getEventsOnDay(LocalDate date) {
    return List.of();
  }

  @Override
  public List<String> getAllCalendarNames() {
    return new java.util.ArrayList<>(calendarNames);
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

}
