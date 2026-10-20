package gui.undergraduate.attendance.details;

import gui.admin.AdminTheme;
import gui.undergraduate.attendance.model.CourseAttendanceData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed panel displaying theory and practical attendance breakdown, threshold indicator, and exam pass status.
 */
public class AttendanceEligibilityPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel courseTitleLabel;
    private final JLabel subDetailsLabel;
    private final JLabel eligibilityBadge;

    private final CustomProgressBar overallBar;
    private final JLabel overallValLabel;
    private final CustomProgressBar theoryBar;
    private final JLabel theoryValLabel;
    private final CustomProgressBar practicalBar;
    private final JLabel practicalValLabel;

    private final JLabel theorySessionsLabel;
    private final JLabel practicalSessionsLabel;
    private final JLabel medicalNoticeLabel;

    public AttendanceEligibilityPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // 1. Header Row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        courseTitleLabel = new JLabel("Module Name");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 16));
        courseTitleLabel.setForeground(AdminTheme.FOREST_GREEN);

        subDetailsLabel = new JLabel("Theory & Practical Attendance Breakdown");
        subDetailsLabel.setFont(AdminTheme.font(Font.PLAIN, 13));
        subDetailsLabel.setForeground(AdminTheme.MUTED);

        titleGroup.add(courseTitleLabel);
        titleGroup.add(Box.createVerticalStrut(3));
        titleGroup.add(subDetailsLabel);

        eligibilityBadge = new JLabel("Eligible");
        eligibilityBadge.setFont(AdminTheme.font(Font.BOLD, 12));
        eligibilityBadge.setForeground(AdminTheme.FOREST_GREEN);
        eligibilityBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.FOREST_GREEN, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        headerRow.add(titleGroup, BorderLayout.WEST);
        headerRow.add(eligibilityBadge, BorderLayout.EAST);
        add(headerRow);

        add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(14));

        // 2. Section Heading
        JLabel evalHeading = new JLabel("Eligibility Status & Progress");
        evalHeading.setFont(AdminTheme.font(Font.BOLD, 14));
        evalHeading.setForeground(AdminTheme.DEEP_GREEN);
        evalHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(evalHeading);
        add(Box.createVerticalStrut(14));

        // 3. Overall Attendance Bar (Primary metric)
        overallValLabel = new JLabel("0.0%");
        overallBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN, true);
        add(createProgressRow("Overall Attendance (80% Exam Entry Threshold)", overallValLabel, overallBar));
        add(Box.createVerticalStrut(12));

        // 4. Theory & Practical Bars
        theoryValLabel = new JLabel("0.0%");
        theoryBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN, false);
        add(createProgressRow("Theory Lecture Attendance", theoryValLabel, theoryBar));
        add(Box.createVerticalStrut(12));

        practicalValLabel = new JLabel("0.0%");
        practicalBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN, false);
        add(createProgressRow("Practical / Lab Session Attendance", practicalValLabel, practicalBar));
        add(Box.createVerticalStrut(16));

        // 5. Stat Pills Row
        JPanel pillsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        pillsRow.setOpaque(false);
        pillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pillsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        theorySessionsLabel = new JLabel("Theory: 0 / 0 Hrs");
        practicalSessionsLabel = new JLabel("Lab: 0 / 0 Hrs");
        pillsRow.add(createStatPill(theorySessionsLabel));
        pillsRow.add(createStatPill(practicalSessionsLabel));
        add(pillsRow);

        add(Box.createVerticalStrut(12));

        // 6. Medical & Eligibility Notice Banner
        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        medicalNoticeLabel = new JLabel("Notice: Valid medical certificates have been accounted into calculations.");
        medicalNoticeLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        medicalNoticeLabel.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(medicalNoticeLabel);
        add(noticeCard);
    }

    public void updateData(CourseAttendanceData data) {
        if (data == null) return;

        courseTitleLabel.setText(data.getCourseName() + " (" + data.getCourseCode() + ")");

        int overall = (int) Math.round(data.getOverallPercentage());
        int theory = (int) Math.round(data.getTheoryPercentage());
        int practical = (int) Math.round(data.getPracticalPercentage());

        overallValLabel.setText(String.format("%.1f%%", data.getOverallPercentage()));
        overallBar.setProgress(overall);

        theoryValLabel.setText(String.format("%.1f%%", data.getTheoryPercentage()));
        theoryBar.setProgress(theory);

        practicalValLabel.setText(String.format("%.1f%%", data.getPracticalPercentage()));
        practicalBar.setProgress(practical);

        // Update pills
        int theoryAttended = data.getAttendedLectures() + data.getApprovedMedicals();
        theorySessionsLabel.setText("Theory: " + theoryAttended + " / " + data.getTotalLectures() + " Hrs");
        practicalSessionsLabel.setText("Practical: " + data.getAttendedPracticals() + " / " + data.getTotalPracticals() + " Hrs");

        // Update eligibility badge and bar colors
        if (data.isEligible()) {
            eligibilityBadge.setText("Eligible for Exam");
            eligibilityBadge.setForeground(AdminTheme.FOREST_GREEN);
            eligibilityBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.FOREST_GREEN, 1, true),
                    new EmptyBorder(4, 10, 4, 10)
            ));
            overallBar.setFillColor(AdminTheme.FOREST_GREEN);
            medicalNoticeLabel.setText("Eligible: Student meets 80% attendance requirement for final exam.");
        } else {
            eligibilityBadge.setText("At Risk (<80%)");
            eligibilityBadge.setForeground(AdminTheme.DANGER);
            eligibilityBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.DANGER, 1, true),
                    new EmptyBorder(4, 10, 4, 10)
            ));
            overallBar.setFillColor(AdminTheme.WARNING);
            medicalNoticeLabel.setText("Warning: Attendance below 80%. Submit pending medicals to dean's office.");
        }
    }

    private JPanel createProgressRow(String name, JLabel valLbl, CustomProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(name);
        title.setFont(AdminTheme.font(Font.PLAIN, 13));
        title.setForeground(new Color(0x33, 0x33, 0x33));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 13));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        header.add(title, BorderLayout.WEST);
        header.add(valLbl, BorderLayout.EAST);

        row.add(header, BorderLayout.NORTH);
        row.add(bar, BorderLayout.SOUTH);
        return row;
    }

    private JPanel createStatPill(JLabel label) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 6));
        pill.setOpaque(true);
        pill.setBackground(new Color(0xF4, 0xF9, 0xF5));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));

        label.setFont(AdminTheme.font(Font.BOLD, 12));
        label.setForeground(AdminTheme.DEEP_GREEN);
        pill.add(label);
        return pill;
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

    private static class CustomProgressBar extends JComponent {
        private int progress;
        private Color fillColor;
        private final boolean showThreshold;

        CustomProgressBar(int progress, Color fillColor, boolean showThreshold) {
            this.progress = progress;
            this.fillColor = fillColor;
            this.showThreshold = showThreshold;
            setPreferredSize(new Dimension(0, 8));
        }

        void setProgress(int progress) {
            this.progress = Math.min(100, Math.max(0, progress));
            repaint();
        }

        void setFillColor(Color color) {
            this.fillColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Track background
            g2.setColor(AdminTheme.MINT);
            g2.fillRoundRect(0, 0, width, height, height, height);

            // Progress bar
            int fillWidth = (int) (width * (progress / 100.0));
            if (fillWidth > 0) {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, fillWidth, height, height, height);
            }

            // Draw 80% threshold marker if required
            if (showThreshold) {
                int markerX = (int) (width * 0.80);
                g2.setColor(AdminTheme.DEEP_GREEN);
                g2.fillRect(markerX, 0, 2, height);
            }

            g2.dispose();
        }
    }
}