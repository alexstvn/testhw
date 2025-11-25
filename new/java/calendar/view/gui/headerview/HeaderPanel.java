package calendar.view.gui.headerview;

import calendar.controller.guicontroller.Features;
import calendar.view.gui.InterfaceGuiView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Header panel that displays calendar information and navigation controls.
 * Emits high-level feature requests to controller via CalendarFeatures interface.
 * NO direct manipulation of model or business logic.
 */
public class HeaderPanel extends JPanel implements InterfaceHeaderPanel {
  // Calendar info - received from MainView
  private String calendarName;
  private String timeZone;

  InterfaceGuiView parentView;

  // UI components
  private JLabel calendarLabel;
  private JLabel monthLabel;
  private JButton prevButton;
  private JButton nextButton;
  private JButton editCalendarButton;
  private JButton switchOrAddCalendarButton;
  private JButton editEventsSameNameButton;

  // Current month/year for display
  private int currentYear;
  private int currentMonth;

  // Feature listeners (controller implements this)
  private List<Features> featureListeners;

  /**
   * Creates a new HeaderPanel with the specified calendar information.
   * Data is provided by MainView, which gets it from the controller.
   *
   * @param calendarName the active calendar name from MainView
   * @param timeZone     the active calendar timezone from MainView
   */
  public HeaderPanel(String calendarName, String timeZone, InterfaceGuiView parentView) {
    this.featureListeners = new ArrayList<>();
    this.parentView = parentView;
    this.timeZone = timeZone;
    this.calendarName = calendarName;
    LocalDate now = LocalDate.now();
    this.currentYear = now.getYear();
    this.currentMonth = now.getMonthValue();
    initializeComponents();
  }

  private void initializeComponents() {
    setLayout(new BorderLayout());
    Box vstack = Box.createVerticalBox();

    vstack.add(Box.createVerticalStrut(20));
    vstack.add(createCalendarNamePanel());
    vstack.add(createSwitchCalendarButton());
    vstack.add(Box.createVerticalStrut(10));
    vstack.add(createEditEventsSameNameButton());
    vstack.add(Box.createVerticalStrut(20));
    vstack.add(createMonthNavigationPanel());
    vstack.add(Box.createVerticalStrut(20));

    add(vstack);
  }

  /**
   * Adds a feature listener (typically the controller).
   * This is the ONLY way the panel communicates with the controller.
   */
  public void addFeaturesListener(Features featureListener) {
    featureListeners.add(featureListener);
    // Wire buttons only after we have a listener
    if (featureListeners.size() == 1) {
      wireButtons();
    }
  }

  /**
   * Wires all buttons to emit feature requests.
   * Uses high-level, application-specific callbacks instead of low-level ActionListener.
   */
  private void wireButtons() {
    // Switch calendar button
    switchOrAddCalendarButton.addActionListener(e -> {
      for (Features f : featureListeners) {
        parentView.showCalendarSelectorDialog(f.getAllCalendarNames());
      }
    });

    editCalendarButton.addActionListener(e -> {
      for (Features f : featureListeners) {
        parentView.showEditCalendarDialog(calendarName, TimeZone.getTimeZone(timeZone));
      }
    });

    editEventsSameNameButton.addActionListener(e -> {
      for (Features f : featureListeners) {
        parentView.showEditEventsSameNameDialog();
      }
    });

    prevButton.addActionListener(e -> {
      for (Features f : featureListeners) {
        f.previousMonth();
      }
    });

    // Next month button
    nextButton.addActionListener(e -> {
      for (Features f : featureListeners) {
        f.nextMonth();
      }
    });
  }

  private JPanel createCalendarNamePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    calendarLabel = new JLabel(formatCalendarLabel(calendarName, timeZone));
    calendarLabel.setFont(new Font("Arial", Font.BOLD, 24));
    calendarLabel.setForeground(new Color(34, 139, 34));
    panel.add(calendarLabel);

    editCalendarButton = new JButton("✎ edit calendar");
    editCalendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
    editCalendarButton.setBorderPainted(false);
    editCalendarButton.setFocusPainted(false);
    editCalendarButton.setContentAreaFilled(false);
    editCalendarButton.setForeground(Color.gray);
    panel.add(editCalendarButton);

    return panel;
  }

  private JButton createSwitchCalendarButton() {
    switchOrAddCalendarButton = new JButton("Switch or Add Calendar");
    switchOrAddCalendarButton.setMargin(new Insets(10, 20, 10, 20));
    switchOrAddCalendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
    switchOrAddCalendarButton.setFocusPainted(false);
    return switchOrAddCalendarButton;
  }

  private JButton createEditEventsSameNameButton() {
    editEventsSameNameButton = new JButton("Edit Events with Same Name");
    editEventsSameNameButton.setMargin(new Insets(10, 20, 10, 20));
    editEventsSameNameButton.setFont(new Font("Arial", Font.PLAIN, 14));
    editEventsSameNameButton.setFocusPainted(false);
    return editEventsSameNameButton;
  }

  private JPanel createMonthNavigationPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    prevButton = new JButton("<");
    prevButton.setFont(new Font("Arial", Font.PLAIN, 12));
    prevButton.setFocusPainted(false);
    panel.add(prevButton);

    monthLabel = new JLabel(formatMonthYearLabel(currentMonth, currentYear));
    monthLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    panel.add(monthLabel);

    nextButton = new JButton(">");
    nextButton.setFont(new Font("Arial", Font.PLAIN, 12));
    nextButton.setFocusPainted(false);
    panel.add(nextButton);

    return panel;
  }

  /**
   * Updates the calendar information displayed in the header.
   * This method is called by MainView when calendar data changes.
   *
   * @param calendarName the new calendar name from MainView
   * @param timeZone     the new timezone from MainView
   */
  @Override
  public void updateCalendarInfo(String calendarName, String timeZone) {
    this.calendarName = calendarName;
    this.timeZone = timeZone;
    updateCalendarLabel();
  }

  @Override
  public void setMonthYear(int month, int year) {
    this.currentMonth = month;
    this.currentYear = year;
    updateMonthLabel();
  }

  private void updateCalendarLabel() {
    calendarLabel.setText(formatCalendarLabel(calendarName, timeZone));
  }

  private void updateMonthLabel() {
    monthLabel.setText(formatMonthYearLabel(currentMonth, currentYear));
  }

  private String formatCalendarLabel(String name, String timezone) {
    return "Calendar: " + name + " (" + timezone + ")";
  }

  private String formatMonthYearLabel(int month, int year) {
    String monthName = Month.of(month).name();
    return "<html><b>" + monthName + "</b> " + year + "</html>";
  }
}