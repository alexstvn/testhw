package calendar.view.gui.monthview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Custom JLabel that draws a circle background for today's date.
 */
class CircleLabel extends JLabel {
  private static final int CIRCLE_SIZE = 30;
  private static final int CIRCLE_PADDING = 4;

  private final boolean isToday;
  private Color originalForeground;

  public CircleLabel(String text, boolean isToday) {
    super(text);
    this.isToday = isToday;
    this.originalForeground = getForeground();
    setHorizontalAlignment(SwingConstants.CENTER);
    setVerticalAlignment(SwingConstants.CENTER);
    setPreferredSize(new Dimension(CIRCLE_SIZE, CIRCLE_SIZE));
  }

  @Override
  public void setForeground(Color fg) {
    super.setForeground(fg);
    this.originalForeground = fg;
    if (isToday) {
      super.setForeground(Color.WHITE);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (isToday) {
      // Draw a red circle for today's date
      g2d.setColor(Color.RED);
      int size = Math.min(getWidth(), getHeight()) - CIRCLE_PADDING;
      int x = (getWidth() - size) / 2;
      int y = (getHeight() - size) / 2;
      g2d.fillOval(x, y, size, size);
    }

    g2d.dispose();
    super.paintComponent(g);
  }
}
