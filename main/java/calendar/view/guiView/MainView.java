package calendar.view.guiView;

import calendar.controller.guicontroller.Features;
import calendar.view.guiView.adapter.IViewEvent;
import calendar.view.guiView.dayView.DayViewPanel;
import calendar.view.guiView.createEventView.CreateEventDialog;
import calendar.view.guiView.dayView.EventDetailsPanel;
import calendar.view.guiView.editEventsSameNameView.EditEventsSameNameDialog;
import calendar.view.guiView.editEventView.EditEventDialog;
import calendar.view.guiView.headerView.AddCalendarDialog;
import calendar.view.guiView.headerView.CalendarSelectorDialog;
import calendar.view.guiView.headerView.EditCalendarDialog;
import calendar.view.guiView.headerView.HeaderPanel;
import calendar.view.guiView.tableMonthTableView.MonthTablePanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainView extends JFrame implements InterfaceGuiView {
  private HeaderPanel headerPanel;
  private MonthTablePanel monthTablePanel;
  private JPanel headerWrapper;

  // Calendar state - centralized in MainView (data from controller)
  private String activeCalendarName;
  private String activeCalendarTimezone;
  private List<String> allCalendarNames;
  private Features controller;

  private CardLayout cardLayout;
  private JPanel mainPanel;
  private JPanel monthViewContainer;
  private DayViewPanel dayViewPanel;

  private EventDetailsPanel eventDetailsPanel;

  private JPanel dayViewContainer;  // Add this field

  public MainView() {
    super("Calendar");
    setSize(1440, 1080);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Initialize calendar state with default values
    this.activeCalendarName = "Loading...";
    this.activeCalendarTimezone = "America/New_York";
    this.allCalendarNames = new ArrayList<>();

    // Create header panel
    headerPanel = new HeaderPanel(activeCalendarName, activeCalendarTimezone);

    // Create month table panel
    monthTablePanel = new MonthTablePanel();

    // Create single-day view panel and event details panel
    dayViewPanel = new DayViewPanel();
    eventDetailsPanel = new EventDetailsPanel();
    eventDetailsPanel.setVisible(false);

    // Set the listener once here
    dayViewPanel.setEventSelectionListener(event -> {
      eventDetailsPanel.showEventDetails(event);
      eventDetailsPanel.setVisible(true);
      dayViewContainer.revalidate();  // Force layout update
      dayViewContainer.repaint();
    });

    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    // Wrapper panel for header with padding
    headerWrapper = new JPanel(new BorderLayout());
    headerWrapper.add(headerPanel, BorderLayout.WEST);
    headerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

    // Month view container
    monthViewContainer = new JPanel(new BorderLayout());
    monthViewContainer.add(headerWrapper, BorderLayout.NORTH);
    monthViewContainer.add(monthTablePanel, BorderLayout.CENTER);

    // Day view container - INCLUDES the event details panel
    dayViewContainer = new JPanel(new BorderLayout());
    dayViewContainer.add(dayViewPanel, BorderLayout.CENTER);
    dayViewContainer.add(eventDetailsPanel, BorderLayout.EAST);

    // Add both containers to the card layout
    mainPanel.add(monthViewContainer, "MONTH");
    mainPanel.add(dayViewContainer, "DAY");  // Changed from just dayViewPanel

    // Main layout
    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    cardLayout.show(mainPanel, "MONTH");

//    display();
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void addFeatures(Features f) {
    this.controller = Objects.requireNonNull(f);

    // Fetch and store calendar information from the controller
    refreshCalendarState();

    // Update the UI with the fetched information
    updateCalendarDisplay();

    // NEW: Use addFeaturesListener instead of setController
    headerPanel.addFeaturesListener(f);

    dayViewPanel.setController(f);
    dayViewPanel.setBackToMonthCallback(() -> {
      eventDetailsPanel.setVisible(false);
      cardLayout.show(mainPanel, "MONTH");
    });
    monthTablePanel.setController(f);
  }

  @Override
  public void renderDayEvents(LocalDate date, List<IViewEvent> events) {
    dayViewPanel.setDate(date);
    dayViewPanel.setCalendarName(activeCalendarName);

    // Hide the event details panel when switching to a new day
    eventDetailsPanel.setVisible(false);

    dayViewPanel.setEventList(events);
  }


  @Override
  public void showDayView(LocalDate date) {
    cardLayout.show(mainPanel, "DAY");
  }

  /**
   * Fetches the latest calendar information from the controller and stores it in MainView.
   * This centralizes all calendar state in MainView.
   */
  private void refreshCalendarState() {
    if (controller != null) {
      this.activeCalendarName = controller.getActiveCalendarName();
      this.activeCalendarTimezone = controller.getActiveCalendarTimezone().getID();
      this.allCalendarNames = controller.getAllCalendarNames();
    }
  }

  /**
   * Updates the calendar display with the stored calendar information.
   * Uses the centralized state stored in MainView fields.
   * Updates the existing HeaderPanel instead of recreating it for efficiency.
   */
  private void updateCalendarDisplay() {
    // Update the existing header panel with new calendar info from MainView state
    headerPanel.updateCalendarInfo(activeCalendarName, activeCalendarTimezone);

    // Refresh the display
    headerWrapper.revalidate();
    headerWrapper.repaint();
  }


  @Override
  public void refresh() {
    // Refresh calendar state from controller
    refreshCalendarState();
    // Update the display with refreshed state
    updateCalendarDisplay();
  }

  /**
   * Refreshes MainView state after calendar operations (switch/edit calendar).
   * Called by HeaderPanel after calendar management actions.
   */
  private void refreshAfterCalendarOperation() {
    refreshCalendarState();
    updateCalendarDisplay();
  }

  // ===================== pop up DIALOG METHODS =====================

  @Override
  public String showCalendarSelectorDialog(List<String> availableCalendars) {
    CalendarSelectorDialog dialog = new CalendarSelectorDialog(this);
    dialog.setCalendars(availableCalendars.toArray(new String[0]));
    dialog.setVisible(true);

    if (dialog.isAddNewCalendar()) {
      // User wants to add - return special marker
      return "::ADD_NEW::";
    }
    return dialog.getSelectedCalendar(); // null if cancelled
  }

  @Override
  public CalendarInfo showAddCalendarDialog() {
    AddCalendarDialog dialog = new AddCalendarDialog(this);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      String name = dialog.getCalendarName();
      TimeZone timezone = dialog.getTimezone();
      return new CalendarInfo(name, timezone);
    }
    return null;
  }

  @Override
  public CalendarInfo showEditCalendarDialog(String calendarName, TimeZone currentTimezone) {
    EditCalendarDialog dialog = new EditCalendarDialog(this, calendarName, currentTimezone, allCalendarNames);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      String name = dialog.getCalendarName();
      TimeZone timezone = dialog.getTimezone();
      return new CalendarInfo(name, timezone);
    }
    return null; // Cancelled
  }

  @Override
  public CreateEventDialog.EventInfo showCreateEventDialog(LocalDate date) {
    CreateEventDialog dialog = new CreateEventDialog(this, date);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      return dialog.getEventInfo();
    }
    return null; // Cancelled
  }

  @Override
  public EditEventDialog.EventInfo showEditEventDialog(IViewEvent event) {
    EditEventDialog dialog =
        new EditEventDialog(this, event);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      return dialog.getEventInfo();
    }
    return null; // Cancelled
  }

  @Override
  public EditEventsSameNameDialog.EditInfo showEditEventsSameNameDialog() {
    EditEventsSameNameDialog dialog =
        new EditEventsSameNameDialog(this);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      return dialog.getEditInfo();
    }
    return null; // Cancelled
  }

  @Override
  public void renderMessage(String message) {

  }

  @Override
  public void setMonthYear(int year, int month) {
    // Update both the header and the month table
    headerPanel.setMonthYear(month, year);
    monthTablePanel.setMonthYear(year, month);
  }

  @Override
  public void setMonthEvents(Map<LocalDate, List<IViewEvent>> events) {
    monthTablePanel.setEvents(events);
  }

  @Override
  public void refreshEventDetails(IViewEvent event) {
    if (eventDetailsPanel.isVisible()) {
      eventDetailsPanel.showEventDetails(event);
    }
  }

  @Override
  public void hideEventDetails() {
    eventDetailsPanel.setVisible(false);
  }
}
