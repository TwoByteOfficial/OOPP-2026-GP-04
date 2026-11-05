package gui.undergraduate.attendance.details;

import dao.undergraduate.UndergraduateAttendanceDAO.AttendanceRecordDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Breakdown panel rendering module eligibility progress bars and criteria info.
 * Displays overall, theory, and practical attendance percentages with visual progress bars.
 */
public class AttendanceEligibilityPanel extends JPanel {

    // Card styling constants
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // UI Labels and Custom Components
    private final JLabel titleLabel;
    private final JLabel subLabel;
    private final JLabel statusBadge;
    private final JLabel overallValLabel;
    private final CustomProgressBar overallBar;
    private final JLabel theoryValLabel;
    private final CustomProgressBar theoryBar;
    private final JLabel practicalValLabel;
    private final CustomProgressBar practicalBar;
    private final JLabel theoryPillLabel;
    private final JLabel practicalPillLabel;
    private final JLabel footerNoteLabel;

    public AttendanceEligibilityPanel() {
        // Transparent background with vertical box layout and outer margins
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // -------------------------------------------------------------
        // 1. Header Row (Module Title, Sub-heading, and Status Badge)
        // -------------------------------------------------------------
        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Left title group
        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Module Breakdown");
        titleLabel.setFont(AdminTheme.font(Font.BOLD, 15));
        titleLabel.setForeground(AdminTheme.DEEP_GREEN);

        subLabel = new JLabel("Theory & Practical Attendance Breakdown");
        subLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        subLabel.setForeground(AdminTheme.MUTED);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(3));
        titleGroup.add(subLabel);

        // Right eligibility status badge
        statusBadge = new JLabel("Eligible for Exam", SwingConstants.CENTER);
        statusBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        statusBadge.setForeground(AdminTheme.FOREST_GREEN);
        statusBadge.setBackground(AdminTheme.MINT);
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(statusBadge);

        headerRow.add(titleGroup, BorderLayout.CENTER);
        headerRow.add(badgeWrapper, BorderLayout.EAST);
        add(headerRow);

        // Subtle horizontal divider line
        add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(16));

        // -------------------------------------------------------------
        // 2. Section Heading
        // -------------------------------------------------------------
        JLabel criteriaHeading = new JLabel("Eligibility Status & Progress");
        criteriaHeading.setFont(AdminTheme.font(Font.BOLD, 13));
        criteriaHeading.setForeground(AdminTheme.DEEP_GREEN);
        criteriaHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        criteriaHeading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        add(criteriaHeading);
        add(Box.createVerticalStrut(18));

        // -------------------------------------------------------------
        // 3. Progress Bars (Overall, Theory, and Practical Attendance)
        // -------------------------------------------------------------
        // Overall attendance bar (80% minimum regulation threshold)
        overallValLabel = new JLabel("0.0%");
        overallBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createProgressRow("Overall Attendance (80% Exam Entry Threshold)", overallValLabel, overallBar));
        add(Box.createVerticalStrut(26));

        // Theory lectures attendance bar
        theoryValLabel = new JLabel("0%");
        theoryBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createProgressRow("Theory Lecture Attendance", theoryValLabel, theoryBar));
        add(Box.createVerticalStrut(26));

        // Practical lab sessions attendance bar
        practicalValLabel = new JLabel("0%");
        practicalBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createProgressRow("Practical / Lab Session Attendance", practicalValLabel, practicalBar));
        add(Box.createVerticalStrut(28));

        // -------------------------------------------------------------
        // 4. Pill Counters Row (Attended vs Total Hours)
        // -------------------------------------------------------------
        JPanel pillsRow = new JPanel(new GridLayout(1, 2, 12, 0));
        pillsRow.setOpaque(false);
        pillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pillsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        theoryPillLabel = new JLabel("Theory: 0 / 0 Hrs", SwingConstants.CENTER);
        practicalPillLabel = new JLabel("Lab: 0 / 0 Hrs", SwingConstants.CENTER);
        pillsRow.add(createPill(theoryPillLabel));
        pillsRow.add(createPill(practicalPillLabel));
        add(pillsRow);

        add(Box.createVerticalStrut(16));

        // -------------------------------------------------------------
        // 5. Footer Notice Banner (Dynamic eligibility status indicator)
        // -------------------------------------------------------------
        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 9));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        footerNoteLabel = new JLabel("Eligible: Student meets 80% attendance requirement for final exam.");
        footerNoteLabel.setFont(AdminTheme.font(Font.PLAIN, 11));
        footerNoteLabel.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(footerNoteLabel);
        add(noticeCard);
    }

    /**
     * Binds attendance database DTO values directly to UI components.
     * Computes percentages, updates progress bars, badges, and warning notes.
     */
    public void updateData(AttendanceRecordDTO dto) {
        if (dto == null) return;
        titleLabel.setText(dto.courseName + " (" + dto.courseCode + ")");

        // Check if student meets the minimum 80% threshold (including approved medicals)
        boolean eligible = dto.isEligible();
        statusBadge.setText(eligible ? "Eligible for Exam" : "Not Eligible");
        statusBadge.setForeground(eligible ? AdminTheme.FOREST_GREEN : AdminTheme.DANGER);
        statusBadge.setBackground(eligible ? AdminTheme.MINT : new Color(0xFD, 0xEA, 0xEA));
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(eligible ? AdminTheme.PASTEL_GREEN : new Color(0xEF, 0x9A, 0x9A), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        // Update Overall attendance progress bar
        int overall = (int) Math.round(dto.adjustedPercentage);
        overallValLabel.setText(String.format("%.1f%%", dto.adjustedPercentage));
        overallBar.setProgress(overall);
        overallBar.setFillColor(eligible ? AdminTheme.FOREST_GREEN : AdminTheme.DANGER);

        // Calculate and update Theory attendance percentage
        int theoryPct = (dto.totalTheoryHours > 0) ? (int) Math.round((dto.attendedTheoryHours * 100.0) / dto.totalTheoryHours) : 100;
        theoryValLabel.setText(theoryPct + "%");
        theoryBar.setProgress(theoryPct);

        // Calculate and update Practical attendance percentage
        int practicalPct = (dto.totalPracticalHours > 0) ? (int) Math.round((dto.attendedPracticalHours * 100.0) / dto.totalPracticalHours) : 100;
        practicalValLabel.setText(practicalPct + "%");
        practicalBar.setProgress(practicalPct);

        // Update attended vs total hours in pill badges
        theoryPillLabel.setText("Theory: " + dto.attendedTheoryHours + " / " + dto.totalTheoryHours + " Hrs");
        practicalPillLabel.setText("Lab: " + dto.attendedPracticalHours + " / " + dto.totalPracticalHours + " Hrs");

        // Toggle regulatory notice text based on eligibility
        if (eligible) {
            footerNoteLabel.setText("Eligible: Student meets 80% attendance requirement for final exam.");
            footerNoteLabel.setForeground(AdminTheme.DEEP_GREEN);
        } else {
            footerNoteLabel.setText("Warning: Attendance below 80%. Submit approved medicals to regain eligibility.");
            footerNoteLabel.setForeground(AdminTheme.DANGER);
        }
    }

    // Overloaded alias methods for polymorphic data passing
    public void updateEligibility(AttendanceRecordDTO dto) {
        updateData(dto);
    }

    public void updateDetails(AttendanceRecordDTO dto) {
        updateData(dto);
    }

    public void setData(AttendanceRecordDTO dto) {
        updateData(dto);
    }

    /**
     * Creates a styled rounded pill container for hours metrics.
     */
    private JPanel createPill(JLabel lbl) {
        JPanel pill = new JPanel(new BorderLayout());
        pill.setOpaque(true);
        pill.setBackground(new Color(0xF4, 0xF9, 0xF5));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        lbl.setFont(AdminTheme.font(Font.BOLD, 12));
        lbl.setForeground(AdminTheme.DEEP_GREEN);
        pill.add(lbl, BorderLayout.CENTER);
        return pill;
    }

    /**
     * Constructs a single progress bar row with label and percentage text.
     */
    private JPanel createProgressRow(String name, JLabel valLbl, CustomProgressBar bar) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

        JLabel title = new JLabel(name);
        title.setFont(AdminTheme.font(Font.PLAIN, 12));
        title.setForeground(new Color(0x33, 0x33, 0x33));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 12));
        valLbl.setForeground(AdminTheme.DEEP_GREEN);

        header.add(title, BorderLayout.WEST);
        header.add(valLbl, BorderLayout.EAST);

        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(header);
        row.add(Box.createVerticalStrut(4));
        row.add(bar);

        return row;
    }

    /**
     * Paints rounded white background and border conforming to card design standards.
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

    /**
     * Custom lightweight progress bar component rendering anti-aliased rounded progress bars.
     */
    private static class CustomProgressBar extends JComponent {
        private int progress;
        private Color fillColor;

        CustomProgressBar(int progress, Color fillColor) {
            this.progress = progress;
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(0, 7));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
        }

        void setProgress(int progress) {
            this.progress = progress;
            repaint();
        }

        void setFillColor(Color c) {
            this.fillColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Background track
            g2.setColor(AdminTheme.MINT);
            g2.fillRoundRect(0, 0, width, height, height, height);

            // Active progress fill
            int fillWidth = (int) (width * (progress / 100.0));
            if (fillWidth > 0) {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, fillWidth, height, height, height);
            }

            g2.dispose();
        }
    }
}