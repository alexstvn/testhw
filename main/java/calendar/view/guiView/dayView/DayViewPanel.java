package calendar.view.guiView.dayView;

import calendar.controller.guicontroller.Features;
import calendar.view.guiView.adapter.IViewEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
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

public class DayViewPanel extends JPanel {
  private static final String ACTION_CREATE_EVENT = "CREATE_EVENT";
  private static final String ACTION_BACK_TO_MONTH = "BACK_TO_MONTH";

  // Constants for sizing
  private static final int PIXELS_PER_HOUR = 60; // Height per hour
  private static final int MIN_EVENT_HEIGHT = 40; // Minimum height for readability
  private static final int ALL_DAY_EVENT_HEIGHT = 70; // Fixed height for all-day events
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);

  private List<IViewEvent> eventList;
  private JPanel eventContainer;
  private JPanel headerPanel;
  private JLabel dateLabel;
  private JLabel calendarNameLabel;
  private JButton backToMonth;
  private JButton createEventButton;
  private LocalDate currentDate;
  private String calendarName;
  private Features controller;
  private Runnable backToMonthCallback;

  private EventDayWidget selectedWidget = null;
  private Consumer<IViewEvent> eventSelectionListener;

  public DayViewPanel() {
    eventList = new ArrayList<>();
    this.currentDate = LocalDate.now();
    this.calendarName = "";

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(Color.WHITE);

    // Create header panel (buttons and date)
    headerPanel = new JPanel();
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

    buttonPanel.add(backToMonth);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(createEventButton);
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

  public void setEventSelectionListener(Consumer<IViewEvent> listener) {
    this.eventSelectionListener = listener;
  }

  public void setController(Features controller) {
    this.controller = controller;
    wireButtons();
  }

  public void setBackToMonthCallback(Runnable callback) {
    this.backToMonthCallback = callback;
  }

  private void wireButtons() {
    createEventButton.setActionCommand(ACTION_CREATE_EVENT);
    createEventButton.addActionListener(this::actionPerformed);

    backToMonth.setActionCommand(ACTION_BACK_TO_MONTH);
    backToMonth.addActionListener(this::actionPerformed);
  }

  private void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    switch (command) {
      case ACTION_CREATE_EVENT:
        handleCreateEvent();
        break;
      case ACTION_BACK_TO_MONTH:
        handleBackToMonth();
        break;
      default:
        break;
    }
  }

  private void handleCreateEvent() {
    if (controller != null) {
      controller.handleAddSingleEvent();
    }
  }

  private void handleBackToMonth() {
    if (backToMonthCallback != null) {
      backToMonthCallback.run();
    }
  }

  public void setDate(LocalDate date) {
    this.currentDate = date;
    updateDateLabel();
  }

  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
    updateCalendarNameLabel();
  }

  public void setEventList(List<IViewEvent> eventList) {
    this.eventList.clear();
    this.eventList.addAll(eventList);
    redrawEvents();
  }

  public void clearEvents() {
    this.eventList.clear();
    redrawEvents();
  }

  public void refresh() {
    redrawEvents();
  }

  private void updateDateLabel() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    dateLabel.setText(currentDate.format(dtf));
  }

  private void updateCalendarNameLabel() {
    if (calendarName != null && !calendarName.isEmpty()) {
      calendarNameLabel.setText("Calendar: " + calendarName);
      calendarNameLabel.setVisible(true);
    } else {
      calendarNameLabel.setVisible(false);
    }
  }

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
      List<IViewEvent> allDayEvents = new ArrayList<>();
      List<IViewEvent> timedEvents = new ArrayList<>();

      for (IViewEvent event : eventList) {
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

        for (IViewEvent event : allDayEvents) {
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

        for (IViewEvent event : timedEvents) {
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

  private boolean isAllDayEvent(IViewEvent event) {
    LocalDate startDate = event.getStartDateTime().toLocalDate();
    LocalDate endDate = event.getEndDateTime().toLocalDate();
    LocalTime startTime = event.getStartDateTime().toLocalTime();
    LocalTime endTime = event.getEndDateTime().toLocalTime();

    return startDate.equals(endDate)
        && startTime.equals(ALL_DAY_START)
        && endTime.equals(ALL_DAY_END);
  }

  private int calculateEventHeight(IViewEvent event) {
    Duration duration = Duration.between(
        event.getStartDateTime(),
        event.getEndDateTime()
    );

    long minutes = duration.toMinutes();
    double hours = minutes / 60.0;

    int calculatedHeight = (int) (hours * PIXELS_PER_HOUR);

    return Math.max(calculatedHeight, MIN_EVENT_HEIGHT);
  }

  private void attachEventClickListener(EventDayWidget widget) {
    widget.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        // RIGHT CLICK → Edit event
        if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
          if (controller != null) {
            IViewEvent event = widget.getEvent();
            controller.handleEditEvent(event);
          }
          return;
        }

        // LEFT CLICK → Select / show details
        if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
          if (eventSelectionListener != null) {
            eventSelectionListener.accept(widget.getEvent());
          }
        }
      }
    });
  }
}