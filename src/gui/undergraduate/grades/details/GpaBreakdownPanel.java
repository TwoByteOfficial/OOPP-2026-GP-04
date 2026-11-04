package gui.undergraduate.grades.details;

import dao.undergraduate.UndergraduateGradesDAO.StudentCourseGradeDTO;
import dao.undergraduate.UndergraduateGradesDAO.TranscriptSummaryDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed breakdown panel illustrating degree class honours benchmark and selected course performance.
 * Features 4px tightly bound progress bars, sleek status badge, and calibrated vertical spacing.
 * Displays selected module metrics (Grade, GPV, Credits) and evaluates overall CGPA against university honours cutoffs.
 */
public class GpaBreakdownPanel extends JPanel {

    // Card styling constants for rounded geometry and subtle border stroke
    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    // Selected course module header labels and dynamic grade badge
    private final JLabel courseTitleLabel;
    private final JLabel subDetailsLabel;
    private final JLabel gradeBadge;

    // Honours benchmark tracking components (First Class, Second Upper, Second Lower)
    private final CustomProgressBar firstClassBar;
    private final JLabel firstClassStatusLabel;
    private final CustomProgressBar secondUpperBar;
    private final JLabel secondUpperStatusLabel;
    private final CustomProgressBar secondLowerBar;
    private final JLabel secondLowerStatusLabel;

    // Metric pill labels for course-specific evaluation
    private final JLabel gpvValueLabel;
    private final JLabel creditsEarnedLabel;

    public GpaBreakdownPanel() {
        // Base container layout configuration and matched outer padding
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Matched outer padding with Attendance and Directory tables
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // -------------------------------------------------------------
        // 1. Header Row (Module Title, Subtitle, and Dynamic Grade Badge)
        // -------------------------------------------------------------
        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        courseTitleLabel = new JLabel("Select Course Module");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 15));
        courseTitleLabel.setForeground(AdminTheme.DEEP_GREEN);

        subDetailsLabel = new JLabel("Academic Performance & Honours Benchmark");
        subDetailsLabel.setFont(AdminTheme.font(Font.PLAIN, 12));
        subDetailsLabel.setForeground(AdminTheme.MUTED);

        titleGroup.add(courseTitleLabel);
        titleGroup.add(Box.createVerticalStrut(3));
        titleGroup.add(subDetailsLabel);

        // Sleek Badge matched with Attendance status badge style
        gradeBadge = new JLabel("Grade: -", SwingConstants.CENTER);
        gradeBadge.setFont(AdminTheme.font(Font.BOLD, 11));
        gradeBadge.setForeground(AdminTheme.FOREST_GREEN);
        gradeBadge.setBackground(AdminTheme.MINT);
        gradeBadge.setOpaque(true);
        gradeBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(gradeBadge);

        headerRow.add(titleGroup, BorderLayout.CENTER);
        headerRow.add(badgeWrapper, BorderLayout.EAST);
        add(headerRow);

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
        JLabel benchHeading = new JLabel("Degree Classification Benchmarks");
        benchHeading.setFont(AdminTheme.font(Font.BOLD, 13));
        benchHeading.setForeground(AdminTheme.DEEP_GREEN);
        benchHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        benchHeading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        add(benchHeading);
        add(Box.createVerticalStrut(18));

        // -------------------------------------------------------------
        // 3. Progress Bars (Evenly spaced across card with tight 4px inner gap)
        // -------------------------------------------------------------
        // First Class Honours Benchmark (CGPA >= 3.70)
        firstClassStatusLabel = new JLabel("Current: 0.00");
        firstClassBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createBenchmarkRow("First Class Honours (CGPA >= 3.70)", firstClassStatusLabel, firstClassBar));
        add(Box.createVerticalStrut(26));

        // Second Class Upper Division Benchmark (CGPA >= 3.30)
        secondUpperStatusLabel = new JLabel("Pending");
        secondUpperBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createBenchmarkRow("Second Class Upper (CGPA >= 3.30)", secondUpperStatusLabel, secondUpperBar));
        add(Box.createVerticalStrut(26));

        // Second Class Lower Division Benchmark (CGPA >= 3.00)
        secondLowerStatusLabel = new JLabel("Pending");
        secondLowerBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createBenchmarkRow("Second Class Lower (CGPA >= 3.00)", secondLowerStatusLabel, secondLowerBar));
        add(Box.createVerticalStrut(28));

        // -------------------------------------------------------------
        // 4. Pill Counters Row (Course GPV & Counted Credits)
        // -------------------------------------------------------------
        JPanel pillsRow = new JPanel(new GridLayout(1, 2, 12, 0));
        pillsRow.setOpaque(false);
        pillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pillsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        gpvValueLabel = new JLabel("Grade Point (GPV): 0.00", SwingConstants.CENTER);
        creditsEarnedLabel = new JLabel("Credits Counted: 0", SwingConstants.CENTER);
        pillsRow.add(createStatPill(gpvValueLabel));
        pillsRow.add(createStatPill(creditsEarnedLabel));
        add(pillsRow);

        add(Box.createVerticalStrut(16));

        // -------------------------------------------------------------
        // 5. Footer Notice Banner (Academic Regulation Advisory)
        // -------------------------------------------------------------
        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 9));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel infoText = new JLabel("Senate Regulation: Minimum CGPA of 2.00 required for award of degree.");
        infoText.setFont(AdminTheme.font(Font.PLAIN, 11));
        infoText.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(infoText);
        add(noticeCard);
    }

    /**
     * Binds selected course grade details, updates labels, and color-codes the grade status badge.
     */
    public void updateCourseSelection(StudentCourseGradeDTO data) {
        if (data == null) return;

        courseTitleLabel.setText(data.courseName + " (" + data.courseCode + ")");
        gradeBadge.setText("Grade: " + data.grade);
        gpvValueLabel.setText(String.format("Grade Point (GPV): %.2f", data.gpv));
        creditsEarnedLabel.setText("Credits Counted: " + data.credits);

        // Dynamic badge color coding based on letter grade tier
        if (data.grade.startsWith("A")) {
            gradeBadge.setForeground(AdminTheme.FOREST_GREEN);
            gradeBadge.setBackground(AdminTheme.MINT);
            gradeBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        } else if (data.grade.startsWith("B")) {
            gradeBadge.setForeground(new Color(0x15, 0x80, 0x3D));
            gradeBadge.setBackground(AdminTheme.MINT);
            gradeBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        } else if (data.grade.startsWith("C")) {
            gradeBadge.setForeground(AdminTheme.WARNING);
            gradeBadge.setBackground(new Color(0xFF, 0xFB, 0xEB));
            gradeBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xFC, 0xE7, 0x88), 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        } else {
            gradeBadge.setForeground(AdminTheme.DANGER);
            gradeBadge.setBackground(new Color(0xFD, 0xEA, 0xEA));
            gradeBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xEF, 0x9A, 0x9A), 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        }
    }

    /**
     * Evaluates current cumulative GPA (CGPA) against degree classification thresholds
     * and updates progress bars and status labels.
     */
    public void updateHonoursBenchmarks(TranscriptSummaryDTO summary) {
        if (summary == null) return;
        double cgpa = summary.cgpa;

        // 1. Evaluate First Class Honours
        firstClassStatusLabel.setText(String.format("Current: %.2f", cgpa));
        int firstClassProgress = (int) Math.min(100, Math.round((cgpa / 4.0) * 100.0));
        firstClassBar.setProgress(firstClassProgress);

        if (cgpa >= 3.70) {
            firstClassStatusLabel.setText("Qualified (First Class)");
            firstClassStatusLabel.setForeground(AdminTheme.FOREST_GREEN);
        } else {
            firstClassStatusLabel.setForeground(AdminTheme.MUTED);
        }

        // 2. Evaluate Second Class Upper Division
        if (cgpa >= 3.30) {
            secondUpperStatusLabel.setText("Qualified");
            secondUpperStatusLabel.setForeground(AdminTheme.FOREST_GREEN);
            secondUpperBar.setProgress(100);
        } else {
            int p = (int) Math.min(100, Math.round((cgpa / 3.30) * 100.0));
            secondUpperStatusLabel.setText(String.format("%.2f / 3.30", cgpa));
            secondUpperStatusLabel.setForeground(AdminTheme.MUTED);
            secondUpperBar.setProgress(p);
        }

        // 3. Evaluate Second Class Lower Division
        if (cgpa >= 3.00) {
            secondLowerStatusLabel.setText("Qualified");
            secondLowerStatusLabel.setForeground(AdminTheme.FOREST_GREEN);
            secondLowerBar.setProgress(100);
        } else {
            int p = (int) Math.min(100, Math.round((cgpa / 3.00) * 100.0));
            secondLowerStatusLabel.setText(String.format("%.2f / 3.00", cgpa));
            secondLowerStatusLabel.setForeground(AdminTheme.MUTED);
            secondLowerBar.setProgress(p);
        }
    }

    /**
     * Factory method creating a benchmark row featuring title label, status text, and slender progress bar.
     */
    private JPanel createBenchmarkRow(String name, JLabel valLbl, CustomProgressBar bar) {
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
        row.add(Box.createVerticalStrut(4)); // 4px tight binding between title and bar
        row.add(bar);

        return row;
    }

    /**
     * Factory method creating an enclosed statistical pill badge with subtle borders and padding.
     */
    private JPanel createStatPill(JLabel label) {
        JPanel pill = new JPanel(new BorderLayout());
        pill.setOpaque(true);
        pill.setBackground(new Color(0xF4, 0xF9, 0xF5));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        label.setFont(AdminTheme.font(Font.BOLD, 12));
        label.setForeground(AdminTheme.DEEP_GREEN);
        pill.add(label, BorderLayout.CENTER);
        return pill;
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

    /**
     * Custom lightweight slender 7px progress bar with rounded ends and theme-colored fill.
     */
    private static class CustomProgressBar extends JComponent {
        private int progress;
        private final Color fillColor;

        CustomProgressBar(int progress, Color fillColor) {
            this.progress = progress;
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(0, 7));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
        }

        void setProgress(int p) {
            this.progress = Math.min(100, Math.max(0, p));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Track background fill
            g2.setColor(AdminTheme.MINT);
            g2.fillRoundRect(0, 0, width, height, height, height);

            // Dynamic progress fill
            int fillWidth = (int) (width * (progress / 100.0));
            if (fillWidth > 0) {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, fillWidth, height, height, height);
            }

            g2.dispose();
        }
    }
}