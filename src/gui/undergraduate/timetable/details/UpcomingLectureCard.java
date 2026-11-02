package gui.undergraduate.timetable.details;

import gui.admin.AdminTheme;
import gui.undergraduate.timetable.model.TimetableEntryData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed card displaying hall location, time slot, and assigned lecturer for the selected session.
 * Supports HTML-based automatic wrapping to prevent course title truncation.
 */
public class UpcomingLectureCard extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel courseTitleLabel;
    private final JLabel courseCodeLabel;
    private final JLabel componentBadge;
    private final JLabel timeValueLabel;
    private final JLabel venueValueLabel;
    private final JLabel lecturerValueLabel;

    public UpcomingLectureCard() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // 1. Header Area: Code on left, Badge on right, Title below
        JPanel topHeader = new JPanel(new BorderLayout(8, 0));
        topHeader.setOpaque(false);
        topHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        courseCodeLabel = new JLabel("ICT2112");
        courseCodeLabel.setFont(AdminTheme.font(Font.BOLD, 13));
        courseCodeLabel.setForeground(AdminTheme.MUTED);

        componentBadge = new JLabel("Theory");
        componentBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        componentBadge.setForeground(AdminTheme.FOREST_GREEN);
        componentBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.FOREST_GREEN, 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));

        topHeader.add(courseCodeLabel, BorderLayout.WEST);
        topHeader.add(componentBadge, BorderLayout.EAST);
        add(topHeader);
        add(Box.createVerticalStrut(6));

        // Course title with HTML wrapping to prevent truncation of long course names
        courseTitleLabel = new JLabel("<html><body style='width: 250px;'>Data Structures & Algorithms</body></html>");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 15));
        courseTitleLabel.setForeground(AdminTheme.FOREST_GREEN);
        courseTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(courseTitleLabel);

        add(Box.createVerticalStrut(12));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(12));

        // 2. Section Heading
        JLabel secHeading = new JLabel("Classroom & Delivery Logistics");
        secHeading.setFont(AdminTheme.font(Font.BOLD, 13));
        secHeading.setForeground(AdminTheme.DEEP_GREEN);
        secHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(secHeading);
        add(Box.createVerticalStrut(12));

        // 3. Detail Rows
        timeValueLabel = new JLabel("Monday • 08:00 AM - 10:00 AM");
        venueValueLabel = new JLabel("Lecture Hall 01");
        lecturerValueLabel = new JLabel("Dr. N. Perera");

        add(createDetailRow("Scheduled Time Slot:", timeValueLabel));
        add(Box.createVerticalStrut(10));
        add(createDetailRow("Assigned Hall / Lab:", venueValueLabel));
        add(Box.createVerticalStrut(10));
        add(createDetailRow("Lecturer / Instructor:", lecturerValueLabel));
        add(Box.createVerticalStrut(14));

        // 4. Attendance Notice Banner
        JPanel noticeCard = new JPanel(new BorderLayout());
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JLabel infoText = new JLabel("<html><b>Note:</b> Student card scanning begins 10 mins before start.</html>");
        infoText.setFont(AdminTheme.font(Font.PLAIN, 11));
        infoText.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(infoText, BorderLayout.CENTER);
        add(noticeCard);
    }

    public void updateSession(TimetableEntryData data) {
        if (data == null) return;
        courseCodeLabel.setText(data.getCourseCode());

        // Multi-line HTML wrapped title eliminates ellipsis truncation
        String rawName = data.getCourseName();
        if (rawName == null) rawName = "";
        courseTitleLabel.setText("<html><body style='width: 250px;'>" + rawName + "</body></html>");

        String comp = data.getComponent();
        componentBadge.setText(comp != null && !comp.isEmpty() ? comp : "Theory");
        timeValueLabel.setText(data.getDayOfWeek() + " • " + data.getTimeSlot());
        venueValueLabel.setText(data.getVenue());
        lecturerValueLabel.setText(data.getLecturerName());
    }

    private JPanel createDetailRow(String labelText, JLabel valLbl) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AdminTheme.font(Font.PLAIN, 11));
        lbl.setForeground(AdminTheme.MUTED);

        valLbl.setFont(AdminTheme.font(Font.BOLD, 13));
        valLbl.setForeground(new Color(0x33, 0x33, 0x33));

        row.add(lbl, BorderLayout.NORTH);
        row.add(valLbl, BorderLayout.SOUTH);
        return row;
    }

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