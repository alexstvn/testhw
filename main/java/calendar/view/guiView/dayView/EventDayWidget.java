package calendar.view.guiView.dayView;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import calendar.view.guiView.adapter.IViewEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

/**
 * Widget representing a single event in the day view.
 * Displays event information and handles click events.
 *
 * Mouse event handling pattern:
 * - Mouse events are separated into individual handler methods (handleClick, handleMouseEntered, handleMouseExited)
 * - Click events delegate to an EventClickListener callback
 * - Hover effects managed internally
 */
public class EventDayWidget extends JComponent {
  private static final int DEFAULT_WIDTH = 600;
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);

  private String eventName;
  private String timeRange;
  private String location;
  private boolean isAllDay;
  private int widgetHeight;
  private IViewEvent event;
  private Color originalBackgroundColor;
  private EventClickListener clickListener;
  private Consumer<IViewEvent> leftClickListener;
  private Consumer<IViewEvent> rightClickListener;

  public EventDayWidget(IViewEvent event, Color backgroundColor, int height) {
    this.event = event;
    this.originalBackgroundColor = backgroundColor;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");

    this.eventName = event.getSubject();
    this.location = event.getLocation();
    this.widgetHeight = height;

    // Check if all-day event
    LocalTime start = event.getStartDateTime().toLocalTime();
    LocalTime end = event.getEndDateTime().toLocalTime();
    boolean sameDay = event.getStartDateTime().toLocalDate()
        .equals(event.getEndDateTime().toLocalDate());

    this.isAllDay = sameDay && start.equals(ALL_DAY_START) && end.equals(ALL_DAY_END);

    if (isAllDay) {
      this.timeRange = "All Day";
    } else {
      this.timeRange = event.getStartDateTime().format(dtf) + " - "
          + event.getEndDateTime().format(dtf);
    }

    setBackground(backgroundColor);
    setOpaque(true);
    setPreferredSize(new Dimension(DEFAULT_WIDTH, height));
    setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    setMinimumSize(new Dimension(200, height));

    // Setup mouse interactions
    setupMouseListeners();
  }

  /**
   * Sets up mouse listeners for click and hover effects.
   */
  private void setupMouseListeners() {
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        handleClick(e);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        handleMouseEntered();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        handleMouseExited();
      }
    });

  }

  private void handleClick(MouseEvent e) {
    if (isRightMouseButton(e)) {
      if (rightClickListener != null) {
        rightClickListener.accept(event);
      }
    } else if (isLeftMouseButton(e)) {
      if (leftClickListener != null) {
        leftClickListener.accept(event);
      }
    }

    // Keep backward compatibility with old clickListener
    if (clickListener != null) {
      clickListener.onEventClicked(event);
    }
  }
  public IViewEvent getEvent() {
    return event;
  }


  private void handleMouseEntered() {
    setBackground(originalBackgroundColor.darker());
    repaint();
  }

  private void handleMouseExited() {
    setBackground(originalBackgroundColor);
    repaint();
  }

  /**
   * Sets the listener for event click events.
   *
   * @param listener the click listener
   */
  public void setEventClickListener(EventClickListener listener) {
    this.clickListener = listener;
  }

  public void setLeftClickListener(Consumer<IViewEvent> l) {
    this.leftClickListener = l;
  }

  public void setRightClickListener(Consumer<IViewEvent> r) {
    this.rightClickListener = r;
  }
  /**
   * Listener interface for event click events.
   */
  public interface EventClickListener {
    void onEventClicked(IViewEvent event);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw background
    int padding = 4;
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight() - padding);

    // Draw border
    g.setColor(new Color(100, 150, 255));
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

    // Determine font sizes based on height
    int subjectFontSize = widgetHeight < 50 ? 12 : 14;
    int detailFontSize = widgetHeight < 50 ? 10 : 12;

    // Draw subject
    g.setFont(new Font("Arial", Font.BOLD, subjectFontSize));
    g.setColor(Color.BLACK);
    g.drawString(truncateText(eventName, 50), 10, 20);

    // Draw time range (if there's room)
    if (widgetHeight >= 40) {
      g.setFont(new Font("Arial", Font.PLAIN, detailFontSize));
      g.setColor(new Color(80, 80, 80));
      g.drawString(timeRange, 10, 38);
    }

    // Draw location (if there's room and location exists)
    if (widgetHeight >= 60 && !location.isEmpty()) {
      g.setColor(new Color(120, 120, 120));
      g.drawString(truncateText("@ " + location, 40), 10, 55);
    }
  }

  /**
   * Truncates text to fit within a maximum length.
   */
  private String truncateText(String text, int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }
    return text.substring(0, maxLength - 3) + "...";
  }
}