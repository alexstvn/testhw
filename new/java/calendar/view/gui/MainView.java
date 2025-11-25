package calendar.view.gui;

import calendar.controller.guicontroller.Features;
import calendar.controller.guicontroller.InterfaceViewEvent;
import calendar.view.gui.dayview.DayViewPanel;
import calendar.view.gui.dayview.EventDetailsPanel;
import calendar.view.gui.eventdialog.CreateEventDialog;
import calendar.view.gui.eventdialog.CreateEventListener;
import calendar.view.gui.eventdialog.EditEventDialog;
import calendar.view.gui.eventdialog.EditEventListener;
import calendar.view.gui.eventdialog.EditEventsSameNameDialog;
import calendar.view.gui.eventdialog.EditScope;
import calendar.view.gui.eventdialog.EventFormData;
import calendar.view.gui.headerview.AddCalendarDialog;
import calendar.view.gui.headerview.CalendarSelectorDialog;
import calendar.view.gui.headerview.EditCalendarDialog;
import calendar.view.gui.headerview.HeaderPanel;
import calendar.view.gui.monthview.MonthTablePanel;
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

/**
 * Main application that puts together all elements of application.
 */
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
  private JPanel dayViewContainer;

  /**
   * Assembles the application and puts together the panels as well as initializing fields
   * needed to keep track of current display calendar.
   */
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
    headerPanel = new HeaderPanel(activeCalendarName, activeCalendarTimezone, this);

    // Create month table panel
    monthTablePanel = new MonthTablePanel();
    monthTablePanel.setSelectDayConsumer(date -> {
      controller.selectDay(date);
    });


    // Create single-day view panel and event details panel
    dayViewPanel = new DayViewPanel(this);
    eventDetailsPanel = new EventDetailsPanel();
    eventDetailsPanel.setVisible(false);

    // Set the listener once here
    dayViewPanel.setEventSelectionListener(event -> {
      eventDetailsPanel.showEventDetails(event);
      eventDetailsPanel.setVisible(true);
      dayViewContainer.revalidate();
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
    mainPanel.add(dayViewContainer, "DAY");

    // Main layout
    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    cardLayout.show(mainPanel, "MONTH");
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void addFeatures(Features f) {
    this.controller = Objects.requireNonNull(f);

    refreshCalendarState();
    updateCalendarDisplay();

    headerPanel.addFeaturesListener(f);

    dayViewPanel.wireButtons();
    dayViewPanel.setBackToMonthCallback(() -> {
      eventDetailsPanel.setVisible(false);
      cardLayout.show(mainPanel, "MONTH");
    });
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
    headerPanel.updateCalendarInfo(activeCalendarName, activeCalendarTimezone);
    headerWrapper.revalidate();
    headerWrapper.repaint();
  }

  @Override
  public void refresh() {
    refreshCalendarState();
    updateCalendarDisplay();
  }

  // ===================== DIALOG METHODS =====================

  @Override
  public void showCalendarSelectorDialog(List<String> availableCalendars) {
    CalendarSelectorDialog dialog = new CalendarSelectorDialog(this);
    dialog.setCalendars(availableCalendars.toArray(new String[0]));
    dialog.setVisible(true);

    String name = dialog.getSelectedCalendar();

    if (dialog.isAddNewCalendar()) {
      showAddCalendarDialog();
    }
    controller.handleSwitchCalendar(name);
  }

  @Override
  public void showAddCalendarDialog() {
    AddCalendarDialog dialog = new AddCalendarDialog(this);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      String name = dialog.getCalendarName();
      TimeZone timezone = dialog.getTimezone();
      controller.handleAddCalendar(name, timezone);
    }
  }

  @Override
  public void showEditCalendarDialog(String calendarName, TimeZone currentTimezone) {
    EditCalendarDialog dialog =
        new EditCalendarDialog(this, calendarName, currentTimezone, allCalendarNames);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      String name = dialog.getCalendarName();
      TimeZone timezone = dialog.getTimezone();
      controller.handleEditCalendar(name, timezone);
    }
  }

  @Override
  public void showErrorDialog(String title, String message) {
    javax.swing.JOptionPane.showMessageDialog(
        null,
        message,
        title,
        javax.swing.JOptionPane.ERROR_MESSAGE
    );
  }

  @Override
  public void showEditEventsSameNameDialog() {
    EditEventsSameNameDialog dialog = new EditEventsSameNameDialog(this);

    // Wire the dialog internally within the view
    dialog.addActionListener(e -> {
      String command = e.getActionCommand();

      if (command.equals(EditEventsSameNameDialog.ACTION_OK)) {
        if (dialog.validateInputs()) {
          // Collect all the data from the dialog
          String eventName = dialog.getEventName();
          EventFormData updateInfo = dialog.getEventInfo();

          // Close dialog
          dialog.setConfirmed(true);
          dialog.dispose();

          // Pass data to controller
          controller.handleEditEventsWithSameName(
              eventName,
              updateInfo,
              dialog.shouldUpdateSubject(),
              dialog.shouldUpdateStartTime(),
              dialog.shouldUpdateEndTime()
          );
        }
      } else if (command.equals(EditEventsSameNameDialog.ACTION_CANCEL)) {
        dialog.setConfirmed(false);
        dialog.dispose();
      }
    });

    dialog.setVisible(true);
  }

  @Override
  public void showCreateEventDialog(LocalDate currentDate) {
    LocalDate eventDate = currentDate != null ? currentDate : LocalDate.now();

    CreateEventDialog dialog = new CreateEventDialog(this, eventDate);

    CreateEventListener listener = new CreateEventListener(dialog);
    dialog.addActionListener(listener);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      controller.handleAddEvent(dialog.getEventInfo());
    }
  }

  @Override
  public void showEditEventDialog(InterfaceViewEvent event) {
    EditEventDialog dialog = new EditEventDialog(this, event);

    EditEventListener listener = new EditEventListener(dialog);
    dialog.addActionListener(listener);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      EventFormData updatedInfo = dialog.getEventInfo();
      EditScope scope = dialog.getScope();
      controller.handleEditEvent(event, updatedInfo, scope);
    }
  }

  // ===================== RENDER METHODS =====================

  @Override
  public void renderMonth(int year, int month, String calendarName, String timezone,
                          Map<LocalDate, List<InterfaceViewEvent>> events) {
    refresh();
    hideEventDetails();

    // Update header
    headerPanel.updateCalendarInfo(calendarName, timezone);
    headerPanel.setMonthYear(month, year);

    // Update month table
    monthTablePanel.setMonthYear(year, month);
    monthTablePanel.setEvents(events);

    // Show month view
    cardLayout.show(mainPanel, "MONTH");
    monthViewContainer.revalidate();
    monthViewContainer.repaint();
  }

  @Override
  public void renderDay(LocalDate date, List<InterfaceViewEvent> events,
                        Map<LocalDate, List<InterfaceViewEvent>> monthEvents) {
    refresh();
    hideEventDetails();
    monthTablePanel.setEvents(monthEvents);

    // Render the day events
    renderDayEvents(date, events);

    // Show day view
    cardLayout.show(mainPanel, "DAY");
    dayViewContainer.revalidate();
    dayViewContainer.repaint();
  }

  /**
   * Updates the day view with events for a specific date.
   * This is used internally to update the day panel without switching views.
   *
   * @param date   the date to display
   * @param events list of events for that day
   */
  private void renderDayEvents(LocalDate date, List<InterfaceViewEvent> events) {
    dayViewPanel.setDate(date);
    dayViewPanel.setCalendarName(activeCalendarName);

    // Update event details panel with new events
    eventDetailsPanel.refreshCurrentEvent(events);

    // Update day panel with events
    dayViewPanel.setEventList(events);
  }


  /**
   * Hides the event details panel.
   */
  private void hideEventDetails() {
    eventDetailsPanel.setVisible(false);
  }

  @Override
  public void renderMessage(String message) {

  }
}