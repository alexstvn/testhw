package calendar.view.gui.dayview;

import calendar.controller.guicontroller.InterfaceViewEvent;
import calendar.view.gui.InterfaceGuiView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * This is used to generate a UI day-view of the calendar
 * (all events happening within a specific date).
 */
public class DayViewPanel extends JPanel {
  private static final String ACTION_CREATE_EVENT = "CREATE_EVENT";
  private static final String ACTION_BACK_TO_MONTH = "BACK_TO_MONTH";

  // Constants for sizing
  private static final int PIXELS_PER_HOUR = 60; // Height per hour
  private static final int MIN_EVENT_HEIGHT = 40; // Minimum height for readability
  private static final int ALL_DAY_EVENT_HEIGHT = 70; // Fixed height for all-day events
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);

  private final List<InterfaceViewEvent> eventList;
  private final JPanel eventContainer;
  private final JLabel howToEditLable;
  private final JLabel dateLabel;
  private final JLabel calendarNameLabel;
  private final JButton backToMonth;
  private final JButton createEventButton;
  private LocalDate currentDate;
  private String calendarName;
  private Runnable backToMonthCallback;

  private Consumer<InterfaceViewEvent> eventSelectionListener;

  private final InterfaceGuiView parentView;

  /**
   * Creates a panel of a day's schedule.
   *
   * @param parentView Refers to the main view that is used to manage connections with controller.
   */
  public DayViewPanel(InterfaceGuiView parentView) {
    eventList = new ArrayList<>();
    this.parentView = parentView;
    this.currentDate = LocalDate.now();
    this.calendarName = "";

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(Color.WHITE);

    // Create header panel (buttons and date)
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
    headerPanel.setBackground(Color.WHITE);
    headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    headerPanel.setAlignmentX(LEFT_ALIGNMENT);
    headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

    // Button panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.setAlignmentX(LEFT_ALIGNMENT);

    backToMonth = new JButton("← Back to Month");
    backToMonth.setFont(new Font("Arial", Font.PLAIN, 14));
    backToMonth.setFocusPainted(false);

    createEventButton = new JButton("Create Event");
    createEventButton.setFont(new Font("Arial", Font.PLAIN, 14));
    createEventButton.setFocusPainted(false);

    howToEditLable = new JLabel();
    howToEditLable.setText("Right-click on event to edit");
    howToEditLable.setFont(new Font("Arial", Font.PLAIN, 14));
    howToEditLable.setForeground(Color.gray);

    buttonPanel.add(backToMonth);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(createEventButton);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(howToEditLable);
    buttonPanel.add(Box.createHorizontalGlue());

    // Calendar name label
    calendarNameLabel = new JLabel();
    calendarNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
    calendarNameLabel.setForeground(new Color(34, 139, 34));
    calendarNameLabel.setAlignmentX(LEFT_ALIGNMENT);

    // Date label
    dateLabel = new JLabel();
    dateLabel.setFont(new Font("Arial", Font.BOLD, 28));
    dateLabel.setForeground(new Color(50, 50, 50));
    dateLabel.setAlignmentX(LEFT_ALIGNMENT);
    updateDateLabel();

    // Assemble header
    headerPanel.add(buttonPanel);
    headerPanel.add(Box.createVerticalStrut(20));
    headerPanel.add(calendarNameLabel);
    headerPanel.add(Box.createVerticalStrut(5));
    headerPanel.add(dateLabel);

    // Create events container with scroll
    eventContainer = new JPanel();
    eventContainer.setLayout(new BoxLayout(eventContainer, BoxLayout.Y_AXIS));
    eventContainer.setBackground(Color.WHITE);
    eventContainer.setBorder(new EmptyBorder(0, 20, 20, 20));

    JScrollPane scrollPane = new JScrollPane(eventContainer);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setAlignmentX(LEFT_ALIGNMENT);

    // Add components to main panel
    add(headerPanel);
    add(scrollPane);
  }

  /**
   * Registers a listener that is triggered when an event widget is selected
   * via left-click. This allows the main view or controller to respond to
   * event selection actions (e.g., showing event details).
   *
   * @param listener A consumer that receives the selected {@link InterfaceViewEvent}.
   */
  public void setEventSelectionListener(Consumer<InterfaceViewEvent> listener) {
    this.eventSelectionListener = listener;
  }

  /**
   * Sets a callback that is executed when the user clicks the
   * "Back to Month" button. This allows the parent view to switch
   * back to the month-view screen.
   *
   * @param callback A runnable to execute when navigating back to month view.
   */
  public void setBackToMonthCallback(Runnable callback) {
    this.backToMonthCallback = callback;
  }

  /**
   * Connects button components to their respective action handlers.
   * This should be called after construction so that UI buttons properly
   * trigger create-event and back-navigation behavior.
   */
  public void wireButtons() {
    createEventButton.setActionCommand(ACTION_CREATE_EVENT);
    createEventButton.addActionListener(this::actionPerformed);

    backToMonth.setActionCommand(ACTION_BACK_TO_MONTH);
    backToMonth.addActionListener(this::actionPerformed);
  }


  private void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    switch (command) {
      case ACTION_CREATE_EVENT:
        parentView.showCreateEventDialog(currentDate);
        break;
      case ACTION_BACK_TO_MONTH:
        handleBackToMonth();
        break;
      default:
        break;
    }
  }

  private void handleBackToMonth() {
    if (backToMonthCallback != null) {
      backToMonthCallback.run();
    }
  }

  /**
   * Sets the date currently being displayed by the day view and updates
   * the date label accordingly.
   *
   * @param date The date to display in this daily schedule panel.
   */
  public void setDate(LocalDate date) {
    this.currentDate = date;
    updateDateLabel();
  }

  /**
   * Sets the name of the calendar currently being viewed and updates
   * the header to reflect the new calendar name.
   *
   * @param calendarName The name of the selected calendar.
   */
  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
    updateCalendarNameLabel();
  }

  /**
   * Replaces the list of events shown in this day view with the given list,
   * then redraws the UI to reflect the updated schedule.
   *
   * @param eventList A list of events occurring on this day.
   */
  public void setEventList(List<InterfaceViewEvent> eventList) {
    this.eventList.clear();
    this.eventList.addAll(eventList);
    redrawEvents();
  }

  /**
   * Updates the date label text to reflect the currently selected date.
   * Uses a long weekday/month format for readability.
   */
  private void updateDateLabel() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    dateLabel.setText(currentDate.format(dtf));
  }

  /**
   * Updates the calendar name label. Hides the label entirely if the
   * calendar name is null or empty.
   */
  private void updateCalendarNameLabel() {
    if (calendarName != null && !calendarName.isEmpty()) {
      calendarNameLabel.setText("Calendar: " + calendarName);
      calendarNameLabel.setVisible(true);
    } else {
      calendarNameLabel.setVisible(false);
    }
  }

  /**
   * Redraws all events for the current day. This method clears the
   * event container, sorts events, separates all-day and timed events,
   * and creates UI widgets for each. It also adds section headers and
   * spacing for clarity.
   */
  private void redrawEvents() {
    eventContainer.removeAll();

    if (eventList.isEmpty()) {
      eventContainer.add(Box.createVerticalStrut(40));
      JLabel noEvents = new JLabel("No events scheduled for this day");
      noEvents.setFont(new Font("Arial", Font.ITALIC, 14));
      noEvents.setForeground(new Color(150, 150, 150));
      noEvents.setAlignmentX(LEFT_ALIGNMENT);
      eventContainer.add(noEvents);
    } else {
      Collections.sort(eventList);

      // Separate all-day events from timed events
      List<InterfaceViewEvent> allDayEvents = new ArrayList<>();
      List<InterfaceViewEvent> timedEvents = new ArrayList<>();

      for (InterfaceViewEvent event : eventList) {
        if (isAllDayEvent(event)) {
          allDayEvents.add(event);
        } else {
          timedEvents.add(event);
        }
      }

      // Add all-day events section
      if (!allDayEvents.isEmpty()) {
        eventContainer.add(Box.createVerticalStrut(10));
        JLabel allDayHeader = new JLabel("All-Day Events");
        allDayHeader.setFont(new Font("Arial", Font.BOLD, 13));
        allDayHeader.setForeground(new Color(100, 100, 100));
        allDayHeader.setAlignmentX(LEFT_ALIGNMENT);
        eventContainer.add(allDayHeader);
        eventContainer.add(Box.createVerticalStrut(10));

        for (InterfaceViewEvent event : allDayEvents) {
          EventDayWidget eventWidget = new EventDayWidget(
              event,
              new Color(240, 240, 240),
              ALL_DAY_EVENT_HEIGHT
          );
          eventWidget.setAlignmentX(LEFT_ALIGNMENT);
          attachEventClickListener(eventWidget);
          eventContainer.add(eventWidget);
          eventContainer.add(Box.createVerticalStrut(8));
        }

        if (!timedEvents.isEmpty()) {
          eventContainer.add(Box.createVerticalStrut(15));
        }
      }

      // Add timed events section
      if (!timedEvents.isEmpty()) {
        eventContainer.add(Box.createVerticalStrut(10));
        JLabel timedHeader = new JLabel("Scheduled Events");
        timedHeader.setFont(new Font("Arial", Font.BOLD, 13));
        timedHeader.setForeground(new Color(100, 100, 100));
        timedHeader.setAlignmentX(LEFT_ALIGNMENT);
        eventContainer.add(timedHeader);
        eventContainer.add(Box.createVerticalStrut(10));

        for (InterfaceViewEvent event : timedEvents) {
          int height = calculateEventHeight(event);
          EventDayWidget eventWidget = new EventDayWidget(
              event,
              new Color(240, 245, 250),
              height
          );
          eventWidget.setAlignmentX(LEFT_ALIGNMENT);
          attachEventClickListener(eventWidget);
          eventContainer.add(eventWidget);
          eventContainer.add(Box.createVerticalStrut(8));
        }
      }
    }

    eventContainer.revalidate();
    eventContainer.repaint();
  }

  /**
   * Determines whether a given event should be classified as an
   * all-day event (8 AM - 5 PM).
   *
   * @param event the event to evaluate
   * @return true if the event is an all-day event, false otherwise
   */
  private boolean isAllDayEvent(InterfaceViewEvent event) {
    LocalDate startDate = event.getStartDateTime().toLocalDate();
    LocalDate endDate = event.getEndDateTime().toLocalDate();
    LocalTime startTime = event.getStartDateTime().toLocalTime();
    LocalTime endTime = event.getEndDateTime().toLocalTime();

    return startDate.equals(endDate)
        && startTime.equals(ALL_DAY_START)
        && endTime.equals(ALL_DAY_END);
  }

  /**
   * Calculates the vertical pixel height used to display a timed event.
   * The height is proportional to the event's duration based on
   * {@code PIXELS_PER_HOUR}, with a minimum guaranteed height for
   * visibility.
   *
   * @param event the event whose display height should be calculated
   * @return the pixel height of this event’s UI widget
   */
  private int calculateEventHeight(InterfaceViewEvent event) {
    Duration duration = Duration.between(
        event.getStartDateTime(),
        event.getEndDateTime()
    );

    long minutes = duration.toMinutes();
    double hours = minutes / 60.0;

    int calculatedHeight = (int) (hours * PIXELS_PER_HOUR);

    return Math.max(calculatedHeight, MIN_EVENT_HEIGHT);
  }

  /**
   * Attaches mouse click behavior to an event widget. Left click triggers
   * the selection listener, and right click opens the edit-event dialog.
   *
   * @param widget the UI widget representing an event
   */
  private void attachEventClickListener(EventDayWidget widget) {
    widget.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // RIGHT CLICK → Edit event
        if (e.getButton() == MouseEvent.BUTTON3) {
          InterfaceViewEvent event = widget.getEvent();
          parentView.showEditEventDialog(event);
          return;
        }

        // LEFT CLICK → Select / show details
        if (e.getButton() == MouseEvent.BUTTON1) {
          if (eventSelectionListener != null) {
            eventSelectionListener.accept(widget.getEvent());
          }
        }
      }
    });
  }
}