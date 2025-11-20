package calendar.view.guiView;

import calendar.controller.guicontroller.Features;
import calendar.view.guiView.dayView.DayViewPanel;
import calendar.view.guiView.headerView.AddCalendarDialog;
import calendar.view.guiView.headerView.CalendarSelectorDialog;
import calendar.view.guiView.headerView.HeaderPanel;
import calendar.view.guiView.tableMonthTableView.MonthTablePanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainView extends JFrame implements InterfaceGuiView {
  // Action command constants - for MVC pattern
  private static final String ACTION_SWITCH_CALENDAR = "SWITCH_CALENDAR";
  private static final String ACTION_ADD_CALENDAR = "ADD_CALENDAR";
  private static final String ACTION_EDIT_CALENDAR = "EDIT_CALENDAR";
  private static final String ACTION_NEXT_MONTH = "NEXT_MONTH";
  private static final String ACTION_PREV_MONTH = "PREV_MONTH";

  List<Features> featuresListener = new ArrayList<>();
  private HeaderPanel headerPanel;
  private MonthTablePanel monthTablePanel;
  private JPanel headerWrapper;

  // Calendar state - centralized in MainView (data from controller)
  private String activeCalendarName;
  private String activeCalendarTimezone;
  private List<String> allCalendarNames;
  private Features controller;

  public MainView() {
    super("Calendar");
    setSize(1440, 1080);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Initialize calendar state with default values
    this.activeCalendarName = "Loading...";
    this.activeCalendarTimezone = "America/New_York";
    this.allCalendarNames = new ArrayList<>();

    // Create header panel with placeholder values (will be updated when features are added)
    headerPanel = new HeaderPanel(activeCalendarName, activeCalendarTimezone);

    // Create month table panel
    monthTablePanel = new MonthTablePanel();

    // Create single-day view panel
    DayViewPanel dayViewPanel = new DayViewPanel();

    // Wrapper panel for header with padding
    headerWrapper = new JPanel(new BorderLayout());
    headerWrapper.add(headerPanel, BorderLayout.WEST);
    headerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20)); // top, left, bottom, right

    // Main layout
    setLayout(new BorderLayout());
    add(headerWrapper, BorderLayout.NORTH);
    add(monthTablePanel, BorderLayout.CENTER);

    display();
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void addFeatures(Features f) {
    this.featuresListener.add(Objects.requireNonNull(f));
    this.controller = f;

    // Fetch and store calendar information from the controller
    refreshCalendarState();

    // Update the UI with the fetched information
    updateCalendarDisplay();

    // Wire up all buttons to fire action events (MVC pattern)
    wireHeaderPanelButtons();
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

  /**
   * Wires up all buttons in HeaderPanel to fire action events to this view.
   * Follows MVC pattern: View captures events → delegates to Controller.
   */
  private void wireHeaderPanelButtons() {
    // Wire "Switch or Add Calendar" button
    headerPanel.getSwitchOrAddCalendarButton().setActionCommand(ACTION_SWITCH_CALENDAR);
    headerPanel.getSwitchOrAddCalendarButton().addActionListener(this::actionPerfomed);

    // Wire "Edit Calendar" button
    headerPanel.getEditCalendarButton().setActionCommand(ACTION_EDIT_CALENDAR);
    headerPanel.getEditCalendarButton().addActionListener(this::actionPerfomed);

    // Wire navigation buttons
    headerPanel.getPrevButton().setActionCommand(ACTION_PREV_MONTH);
    headerPanel.getPrevButton().addActionListener(this::actionPerfomed);

    headerPanel.getNextButton().setActionCommand(ACTION_NEXT_MONTH);
    headerPanel.getNextButton().addActionListener(this::actionPerfomed);
  }

  @Override
  public void refresh() {
    // Refresh calendar state from controller
    refreshCalendarState();
    // Update the display with refreshed state
    updateCalendarDisplay();
  }

  // ===================== PUBLIC GETTERS FOR CENTRALIZED STATE =====================

  /**
   * Gets the active calendar name.
   * This is the single source of truth for the active calendar in the view.
   *
   * @return the active calendar name
   */
  public String getActiveCalendarName() {
    return activeCalendarName;
  }

  /**
   * Gets the active calendar timezone.
   * This is the single source of truth for the timezone in the view.
   *
   * @return the active calendar timezone ID
   */
  public String getActiveCalendarTimezone() {
    return activeCalendarTimezone;
  }

  /**
   * Gets all calendar names.
   * This is the single source of truth for the calendar list in the view.
   *
   * @return list of all calendar names
   */
  public List<String> getAllCalendarNames() {
    return new ArrayList<>(allCalendarNames);
  }

  /**
   * Handles action events from UI components.
   * Simply delegates ALL logic to the controller (pure MVC pattern).
   */
  @Override
  public void actionPerfomed(ActionEvent e) {
    if (controller == null) {
      return;
    }

    String command = e.getActionCommand();

    // Pure delegation - NO logic in view, ALL logic in controller
    switch (command) {
      case ACTION_SWITCH_CALENDAR:
        controller.handleSwitchCalendar();
        refreshCalendarState(); // Sync view from controller
        updateCalendarDisplay(); // Update display
        break;
      case ACTION_ADD_CALENDAR:
        controller.handleAddCalendar();
        refreshCalendarState(); // Sync view from controller
        updateCalendarDisplay(); // Update display
        break;
      case ACTION_EDIT_CALENDAR:
        controller.handleEditCalendar();
        refreshCalendarState(); // Sync view from controller
        updateCalendarDisplay(); // Update display
        break;
      case ACTION_NEXT_MONTH:
        controller.nextMonth();
        break;
      case ACTION_PREV_MONTH:
        controller.previousMonth();
        break;
      default:
        // Unknown action - ignore
        break;
    }
  }

  // ===================== DIALOG METHODS (Pure UI - No Logic) =====================

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
    return null; // Cancelled
  }

  @Override
  public CalendarInfo showEditCalendarDialog(String calendarName, TimeZone currentTimezone) {
    // TODO: Create EditCalendarDialog
    System.out.println("Edit calendar dialog - to be implemented");
    return null;
  }

  @Override
  public void renderMessage(String message) {

  }

  @Override
  public void setupMonthNavigation(MonthNavigationCallback callback) {
    // OBSOLETE: This method is no longer used with the new action event pattern.
    // Month navigation now works through:
    // 1. User clicks next/prev button
    // 2. Button fires ACTION_NEXT_MONTH or ACTION_PREV_MONTH event
    // 3. actionPerformed() delegates to controller.nextMonth()/previousMonth()
    // 4. Controller updates state and calls view.setMonthYear()
    //
    // This method exists only for interface compatibility.
    // It does nothing because navigation is handled through action events.
  }

  @Override
  public void setMonthYear(int year, int month) {
    // Update both the header and the month table
    headerPanel.setMonthYear(month, year);
    monthTablePanel.setMonthYear(year, month);
  }

  /**
   * Gets the header panel.
   *
   * @return the header panel
   */
  public HeaderPanel getHeaderPanel() {
    return headerPanel;
  }

  /**
   * Gets the month table panel.
   *
   * @return the month table panel
   */
  public MonthTablePanel getMonthTablePanel() {
    return monthTablePanel;
  }
}
