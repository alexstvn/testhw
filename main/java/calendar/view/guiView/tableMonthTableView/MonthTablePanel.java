package calendar.view.guiView.tableMonthTableView;

import calendar.controller.guicontroller.Features;
import calendar.view.guiView.adapter.IViewEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel that displays a month view calendar grid.
 * Handles date selection events and delegates to the controller.
 */
public class MonthTablePanel extends JPanel implements InterfaceMonthTablePanel {
  private static final String[] DAY_HEADERS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private static final int CALENDAR_ROWS = 7;
  private static final int CALENDAR_COLS = 7;
  private static final int CALENDAR_CELLS = 42; // 6 weeks * 7 days
  private static final int MAX_VISIBLE_EVENTS = 3;
  private static final int EVENT_TEXT_MAX_LENGTH = 12;
  private static final int EVENT_TEXT_TRUNCATE_LENGTH = 10;

  private static final Color CURRENT_MONTH_BG = Color.WHITE;
  private static final Color OTHER_MONTH_BG = new Color(245, 245, 245);
  private static final Color HOVER_BG = new Color(240, 240, 240);
  private static final Color CELL_BORDER = Color.LIGHT_GRAY;
  private static final Color OTHER_MONTH_TEXT = Color.GRAY;
  private static final Color EVENT_TEXT = new Color(0, 100, 0);
  private static final Color MORE_EVENTS_TEXT = Color.GRAY;

  private Map<LocalDate, List<IViewEvent>> eventsMap;
  private LocalDate selectedDate;
  private Map<LocalDate, JPanel> dateCellPanels;
  private int currentYear;
  private int currentMonth;
  private Features controller;

  /**
   * Sets the controller and wires up date selection events.
   *
   * @param controller the controller to handle date selection events
   */
  public void setController(Features controller) {
    this.controller = controller;
  }

  public MonthTablePanel() {
    this.eventsMap = new HashMap<>();
    this.selectedDate = null;
    this.dateCellPanels = new HashMap<>();

    LocalDate now = LocalDate.now();
    this.currentYear = now.getYear();
    this.currentMonth = now.getMonthValue();

    initializeComponents();
  }


  @Override
  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  @Override
  public void refresh() {
    removeAll();
    dateCellPanels.clear();
    initializeComponents();
    revalidate();
    repaint();
  }

  @Override
  public void setMonthYear(int year, int month) {
    this.currentYear = year;
    this.currentMonth = month;
    refresh();
  }

  @Override
  public void setEvents(Map<LocalDate, List<IViewEvent>> events) {
    this.eventsMap = events != null ? events : new HashMap<>();
    refresh();
  }

  @Override
  public void setSelectedDate(LocalDate date) {
    this.selectedDate = date;
  }

  private void initializeComponents() {
    setLayout(new GridLayout(CALENDAR_ROWS, CALENDAR_COLS, 1, 1));
    addDayHeaders();
    addDateCells();
  }

  private void addDayHeaders() {
    for (String day : DAY_HEADERS) {
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      dayLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
      add(dayLabel);
    }
  }

  private void addDateCells() {
    LocalDate today = LocalDate.now();
    LocalDate firstOfMonth = LocalDate.of(currentYear, currentMonth, 1);
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
    LocalDate startDate = firstOfMonth.minusDays(firstDayOfWeek);

    for (int i = 0; i < CALENDAR_CELLS; i++) {
      LocalDate date = startDate.plusDays(i);
      boolean isCurrentMonth = date.getMonthValue() == currentMonth;
      boolean isToday = date.equals(today);
      List<IViewEvent> dayEvents = eventsMap.getOrDefault(date, new ArrayList<>());

      JPanel cell = createDateCell(date, isToday, isCurrentMonth, dayEvents);
      dateCellPanels.put(date, cell);
      add(cell);
    }
  }

  private JPanel createDateCell(LocalDate date, boolean isToday,
                                 boolean isCurrentMonth, List<IViewEvent> events) {
    JPanel cellPanel = new JPanel(new BorderLayout());
    cellPanel.setBorder(BorderFactory.createLineBorder(CELL_BORDER));

    Color bgColor = isCurrentMonth ? CURRENT_MONTH_BG : OTHER_MONTH_BG;
    cellPanel.setBackground(bgColor);

    setupCellMouseListener(cellPanel, date, bgColor);
    addDateLabel(cellPanel, date, isToday, isCurrentMonth);
    addEventsList(cellPanel, events);

    return cellPanel;
  }

  private void setupCellMouseListener(JPanel cellPanel, LocalDate date, Color originalBg) {
    cellPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    cellPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        onDateClicked(date);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        cellPanel.setBackground(HOVER_BG);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        cellPanel.setBackground(originalBg);
      }
    });
  }

  private void addDateLabel(JPanel cellPanel, LocalDate date,
                            boolean isToday, boolean isCurrentMonth) {
    JPanel topPanel = new JPanel(new GridBagLayout());
    topPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHEAST;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.insets = new Insets(5, 5, 5, 5);

    CircleLabel dateLabel = new CircleLabel(String.valueOf(date.getDayOfMonth()), isToday);
    dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

    if (!isCurrentMonth) {
      dateLabel.setForeground(OTHER_MONTH_TEXT);
    }

    topPanel.add(dateLabel, gbc);
    cellPanel.add(topPanel, BorderLayout.NORTH);
  }

  private void addEventsList(JPanel cellPanel, List<IViewEvent> events) {
    if (events == null || events.isEmpty()) {
      return;
    }

    JPanel eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    eventsPanel.setOpaque(false);
    eventsPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));

    int visibleEvents = Math.min(events.size(), MAX_VISIBLE_EVENTS);
    for (int i = 0; i < visibleEvents; i++) {
      eventsPanel.add(createEventLabel(events.get(i)));
    }

    if (events.size() > MAX_VISIBLE_EVENTS) {
      eventsPanel.add(createMoreEventsLabel(events.size() - MAX_VISIBLE_EVENTS));
    }

    cellPanel.add(eventsPanel, BorderLayout.CENTER);
  }

  private JLabel createEventLabel(IViewEvent event) {
    String eventText = event.getSubject();
    if (eventText.length() > EVENT_TEXT_MAX_LENGTH) {
      eventText = eventText.substring(0, EVENT_TEXT_TRUNCATE_LENGTH) + "..";
    }

    JLabel eventLabel = new JLabel(eventText);
    eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
    eventLabel.setForeground(EVENT_TEXT);
    eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    return eventLabel;
  }

  private JLabel createMoreEventsLabel(int count) {
    JLabel moreLabel = new JLabel("+" + count + " more");
    moreLabel.setFont(new Font("Arial", Font.ITALIC, 9));
    moreLabel.setForeground(MORE_EVENTS_TEXT);
    moreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    return moreLabel;
  }

  private void onDateClicked(LocalDate date) {
    selectedDate = date;

    if (controller != null) {
      controller.selectDay(date);
    }

    System.out.println("Date clicked: " + date);
  }
}
