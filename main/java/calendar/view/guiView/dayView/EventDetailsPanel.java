package calendar.view.guiView.dayView;

import calendar.view.guiView.adapter.IViewEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class EventDetailsPanel extends JPanel {

  private JLabel titleLabel;
  private JPanel contentPanel;
  private JScrollPane scrollPane;
  private JButton closeButton;

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a");

  public EventDetailsPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setPreferredSize(new Dimension(350, 0)); // Width 350, height flexible
    setMinimumSize(new Dimension(350, 400));
    setBackground(new Color(250, 250, 250));
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)),
        new EmptyBorder(20, 20, 20, 20)
    ));

    // Header section
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
    headerPanel.setBackground(new Color(250, 250, 250));
    headerPanel.setAlignmentX(LEFT_ALIGNMENT);
    headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    titleLabel = new JLabel("Event Details");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setForeground(new Color(50, 50, 50));

    closeButton = new JButton("✕");
    closeButton.setFont(new Font("Arial", Font.BOLD, 16));
    closeButton.setForeground(new Color(100, 100, 100));
    closeButton.setBackground(new Color(250, 250, 250));
    closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    closeButton.setFocusPainted(false);
    closeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    closeButton.addActionListener(e -> setVisible(false));

    headerPanel.add(titleLabel);
    headerPanel.add(Box.createHorizontalGlue());
    headerPanel.add(closeButton);

    // Content panel with scroll
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

    scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(null);
    scrollPane.setAlignmentX(LEFT_ALIGNMENT);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    add(headerPanel);
    add(Box.createVerticalStrut(15));
    add(scrollPane);
  }

  public void showEventDetails(IViewEvent event) {
    contentPanel.removeAll();

    // Subject
    addFieldLabel("Subject");
    addFieldValue(event.getSubject());
    addSpacer(15);

    // Start time
    addFieldLabel("Starts");
    addFieldValue(event.getStartDateTime().format(TIME_FORMATTER));
    addSpacer(15);

    // End time
    addFieldLabel("Ends");
    addFieldValue(event.getEndDateTime().format(TIME_FORMATTER));
    addSpacer(15);

    // Location (only if not empty)
    if (event.getLocation() != null && !event.getLocation().trim().isEmpty()) {
      addFieldLabel("Location");
      addFieldValue(event.getLocation());
      addSpacer(15);
    }

    // Description (only if not empty)
    if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
      addFieldLabel("Description");
      addDescriptionField(event.getDescription());
      addSpacer(15);
    }

    // Privacy status
    addFieldLabel("Privacy");
    addFieldValue(event.isPrivate() ? "Private" : "Public");
    addSpacer(15);

    // Repeating status
    addFieldLabel("Repeating");
    addFieldValue(event.isRepeating() ? "Yes" : "No");

    contentPanel.revalidate();
    contentPanel.repaint();
  }

  private void addFieldLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", Font.BOLD, 11));
    label.setForeground(new Color(100, 100, 100));
    label.setAlignmentX(LEFT_ALIGNMENT);
    contentPanel.add(label);
    contentPanel.add(Box.createVerticalStrut(5));
  }

  private void addFieldValue(String text) {
    JLabel label = new JLabel("<html>" + text + "</html>");
    label.setFont(new Font("Arial", Font.PLAIN, 14));
    label.setForeground(new Color(50, 50, 50));
    label.setAlignmentX(LEFT_ALIGNMENT);
    contentPanel.add(label);
  }

  private void addDescriptionField(String text) {
    JTextArea textArea = new JTextArea(text);
    textArea.setFont(new Font("Arial", Font.PLAIN, 14));
    textArea.setForeground(new Color(50, 50, 50));
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);
    textArea.setOpaque(false);
    textArea.setAlignmentX(LEFT_ALIGNMENT);
    contentPanel.add(textArea);
  }

  private void addSpacer(int height) {
    contentPanel.add(Box.createVerticalStrut(height));
  }
}