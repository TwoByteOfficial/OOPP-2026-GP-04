package gui.undergraduate.timetable.details;

import gui.admin.AdminTheme;
import gui.undergraduate.timetable.model.TimetableEntryData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed card displaying hall location, time slot, and assigned lecturer for the selected session.
 * Features tightened title-value pairs with expanded breathing space between logistics rows.
 * Encapsulates lecture session information, theory/practical category badging,
 * attendance scanning reminders, and graceful empty-state handling for free study days.
 */
public class UpcomingLectureCard extends JPanel {

    // Card styling constants for rounded geometry, outer stroke, and internal dividers
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);
    private static final Color DIVIDER_COLOR = new Color(0xEE, 0xF2, 0xF6);

    // Dynamic session metadata and logistics labels
    private final JLabel courseTitleLabel;
    private final JLabel courseCodeLabel;
    private final JLabel componentBadge;
    private final JLabel timeValueLabel;
    private final JLabel venueValueLabel;
    private final JLabel lecturerValueLabel;
    private final JLabel noticeInfoLabel;

    /**
     * Constructs the upcoming lecture card, sets up fixed proportional bounds,
     * initializes header elements, and arranges delivery logistics rows.
     */
    public UpcomingLectureCard() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(14, 16, 14, 16));

        // Proportional height accommodating expanded row gaps without trailing dead space
        setPreferredSize(new Dimension(340, 310));
        setMaximumSize(new Dimension(340, 310));
        setMinimumSize(new Dimension(340, 310));

        // -------------------------------------------------------------
        // 1. Header Row: Course Code on left, Badge on right
        // -------------------------------------------------------------
        JPanel topHeader = new JPanel(new BorderLayout(8, 0));
        topHeader.setOpaque(false);
        topHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        topHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        courseCodeLabel = new JLabel("ICT2112");
        courseCodeLabel.setFont(AdminTheme.font(Font.BOLD, 13));
        courseCodeLabel.setForeground(AdminTheme.MUTED);

        componentBadge = new JLabel("Theory", SwingConstants.CENTER);
        componentBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        componentBadge.setForeground(AdminTheme.FOREST_GREEN);
        componentBadge.setBackground(AdminTheme.MINT);
        componentBadge.setOpaque(true);
        componentBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(3, 10, 3, 10)
        ));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(componentBadge);

        topHeader.add(courseCodeLabel, BorderLayout.CENTER);
        topHeader.add(badgeWrapper, BorderLayout.EAST);
        add(topHeader);
        add(Box.createVerticalStrut(4));

        // Course Title with HTML word-wrapping
        courseTitleLabel = new JLabel("<html><body style='width: 250px;'>Data Structures & Algorithms</body></html>");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 14));
        courseTitleLabel.setForeground(AdminTheme.DEEP_GREEN);
        courseTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(courseTitleLabel);

        add(Box.createVerticalStrut(8));

        // Crisp visible divider line
        JPanel divider = new JPanel();
        divider.setOpaque(true);
        divider.setBackground(DIVIDER_COLOR);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(divider);

        add(Box.createVerticalStrut(10));

        // -------------------------------------------------------------
        // 2. Section Heading
        // -------------------------------------------------------------
        JLabel secHeading = new JLabel("Classroom & Delivery Logistics");
        secHeading.setFont(AdminTheme.font(Font.BOLD, 12));
        secHeading.setForeground(AdminTheme.DEEP_GREEN);
        secHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(secHeading);
        add(Box.createVerticalStrut(10));

        // -------------------------------------------------------------
        // 3. Detail Rows (Expanded 13px gap between sections, tight 1px gap inside each section)
        // -------------------------------------------------------------
        timeValueLabel = new JLabel("Monday • 08:00 - 10:00");
        venueValueLabel = new JLabel("Lecture Hall 1");
        lecturerValueLabel = new JLabel("Dr. N. Perera");

        add(createDetailRow("Scheduled Time Slot:", timeValueLabel));
        add(Box.createVerticalStrut(13));
        add(createDetailRow("Assigned Hall / Lab:", venueValueLabel));
        add(Box.createVerticalStrut(13));
        add(createDetailRow("Lecturer / Instructor:", lecturerValueLabel));
        add(Box.createVerticalStrut(14));

        // -------------------------------------------------------------
        // 4. Attendance Notice Banner
        // -------------------------------------------------------------
        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        noticeInfoLabel = new JLabel("<html><b>Note:</b> Student card scanning begins 10 mins before start.</html>");
        noticeInfoLabel.setFont(AdminTheme.font(Font.PLAIN, 11));
        noticeInfoLabel.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(noticeInfoLabel);
        add(noticeCard);
    }

    /**
     * Binds an active timetable entry model to the card labels and sets up component badging.
     *
     * @param data Selected session entity, or null if no session is active
     */
    public void updateSession(TimetableEntryData data) {
        if (data == null) {
            showEmptyState();
            return;
        }

        courseCodeLabel.setText(data.getCourseCode());

        String rawName = data.getCourseName();
        if (rawName == null) rawName = "";
        courseTitleLabel.setText("<html><body style='width: 250px;'>" + rawName + "</body></html>");

        String comp = data.getComponent();
        String displayComp = (comp != null && !comp.isEmpty()) ? comp : "Theory";
        componentBadge.setText(displayComp);

        // Apply distinct styling for Practical/Lab vs Theory sessions
        boolean isLab = "Lab".equalsIgnoreCase(displayComp) || "Practical".equalsIgnoreCase(displayComp);
        if (isLab) {
            componentBadge.setForeground(new Color(0x19, 0x76, 0xD2));
            componentBadge.setBackground(new Color(0xEB, 0xF5, 0xFB));
            componentBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xBB, 0xDE, 0xFB), 1, true),
                    new EmptyBorder(3, 10, 3, 10)
            ));
        } else {
            componentBadge.setForeground(AdminTheme.FOREST_GREEN);
            componentBadge.setBackground(AdminTheme.MINT);
            componentBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                    new EmptyBorder(3, 10, 3, 10)
            ));
        }

        timeValueLabel.setText(data.getDayOfWeek() + " • " + data.getTimeSlot());
        venueValueLabel.setText(data.getVenue());
        lecturerValueLabel.setText(data.getLecturerName());
        noticeInfoLabel.setText("<html><b>Note:</b> Student card scanning begins 10 mins before start.</html>");
    }

    /**
     * Configures the card into an empty-state display when no lectures are scheduled for the selected day.
     */
    public void showEmptyState() {
        courseCodeLabel.setText("No Session");
        componentBadge.setText("Free Day");
        componentBadge.setForeground(AdminTheme.MUTED);
        componentBadge.setBackground(new Color(0xF1, 0xF5, 0xF9));
        componentBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1, true),
                new EmptyBorder(3, 10, 3, 10)
        ));

        courseTitleLabel.setText("<html><body style='width: 250px; color: #888888;'>No Academic Sessions Scheduled</body></html>");
        timeValueLabel.setText("Free Study Day / Revision");
        venueValueLabel.setText("No Venue Assigned");
        lecturerValueLabel.setText("None");
        noticeInfoLabel.setText("<html><b>Info:</b> No timetabled lectures on this day.</html>");
    }

    /**
     * Helper factory generating a tightly positioned title-value detail row.
     *
     * @param labelText Caption describing the logistics attribute
     * @param valLbl    Dynamic value label
     * @return Configured vertical row container
     */
    private JPanel createDetailRow(String labelText, JLabel valLbl) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        lbl.setForeground(AdminTheme.MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        valLbl.setForeground(new Color(0x33, 0x33, 0x33));
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(lbl);
        // Tightly positioned value right below the title with 1px space
        row.add(Box.createVerticalStrut(1));
        row.add(valLbl);
        return row;
    }

    /**
     * Paints an anti-aliased rounded white card background and outer perimeter border.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AdminTheme.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.setColor(CARD_BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}