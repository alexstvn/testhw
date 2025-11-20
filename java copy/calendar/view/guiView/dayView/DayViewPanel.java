package calendar.view.guiView.dayView;

import calendar.view.guiView.adapter.IViewEvent;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class DayViewPanel extends JPanel {
  private List<IViewEvent> eventList;
  private JPanel eventContainer;
  private JLabel dateLabel;
  private JButton backToMonth;
  private LocalDate currentDate;

  public DayViewPanel() {
    eventList = new ArrayList<>();
    this.currentDate = LocalDate.now();

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(Color.WHITE);
    setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding around edges

    // TODO: Delete later.
    backToMonth = new JButton("← Back to Month");
    backToMonth.setFont(new Font("Arial", Font.PLAIN, 14));
    backToMonth.setAlignmentX(LEFT_ALIGNMENT);
    backToMonth.setFocusPainted(false);

    dateLabel = new JLabel();
    dateLabel.setFont(new Font("Arial", Font.BOLD, 24));
    dateLabel.setAlignmentX(LEFT_ALIGNMENT);
    updateDateLabel();

    eventContainer = new JPanel();
    eventContainer.setLayout(new BoxLayout(eventContainer, BoxLayout.Y_AXIS));
    eventContainer.setBackground(Color.WHITE);

    JScrollPane scrollPane = new JScrollPane(eventContainer);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setAlignmentX(LEFT_ALIGNMENT);

    add(backToMonth);
    add(Box.createVerticalStrut(15));
    add(dateLabel);
    add(Box.createVerticalStrut(20));
    add(scrollPane);
  }

  public JButton getBackToMonth() {
    return backToMonth;
  }

  public void setDate(LocalDate date) {
    this.currentDate = date;
    updateDateLabel();
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

  private void updateDateLabel() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    dateLabel.setText(currentDate.format(dtf));
  }

  private void redrawEvents() {
    eventContainer.removeAll(); // Only clear event container

    if (eventList.isEmpty()) {
      JLabel noEvents = new JLabel("No events scheduled for this day");
      noEvents.setFont(new Font("Arial", Font.ITALIC, 14));
      noEvents.setForeground(Color.GRAY);
      noEvents.setAlignmentX(LEFT_ALIGNMENT);
      eventContainer.add(noEvents);
    } else {
      Collections.sort(eventList);

      for (IViewEvent event : eventList) {
        // TODO: Change to be color of calendar later.
        EventDayWidget eventWidget = new EventDayWidget(event, Color.CYAN);
        eventWidget.setAlignmentX(LEFT_ALIGNMENT);
        eventContainer.add(eventWidget);
        eventContainer.add(Box.createVerticalStrut(10)); // Spacing between events
      }
    }

    eventContainer.revalidate();
    eventContainer.repaint();
  }
}