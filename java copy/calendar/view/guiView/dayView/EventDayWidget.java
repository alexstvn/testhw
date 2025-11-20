package calendar.view.guiView.dayView;

import calendar.view.guiView.adapter.IViewEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.time.format.DateTimeFormatter;
import javax.swing.JComponent;

public class EventDayWidget extends JComponent {
  private String eventName;
  private String start;
  private String end;
  private String location;

  public EventDayWidget(IViewEvent event, Color backgroundColor) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");

    this.eventName = event.getSubject();
    this.start = event.getStartDateTime().format(dtf);
    this.end = event.getEndDateTime().format(dtf);
    this.location = event.getLocation();

    setBackground(backgroundColor);
    setPreferredSize(new Dimension(300, 70));
    setMaximumSize(new Dimension(400, 100));
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setFont(new Font("arial", Font.BOLD, 14));

    g.setColor(Color.blue);
    g.drawString(eventName, 10, 20); // in relation to where in widget

    g.setFont(new Font("arial", Font.PLAIN, 12));

    g.setColor(Color.DARK_GRAY);
    g.drawString(start, 10, 40);

    // TODO: Find way to put in end time.

    g.setColor(Color.DARK_GRAY);
    g.drawString(location, 10, 55);
  }
}
