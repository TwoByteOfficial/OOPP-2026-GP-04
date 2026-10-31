package gui.undergraduate.grades.details;

import dao.undergraduate.UndergraduateGradesDAO.StudentCourseGradeDTO;
import dao.undergraduate.UndergraduateGradesDAO.TranscriptSummaryDTO;
import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed breakdown panel illustrating degree class honours benchmark and selected course performance.
 */
public class GpaBreakdownPanel extends JPanel {

    private static final int CORNER_RADIUS = 16;
    private static final Color CARD_BORDER_COLOR = new Color(0xE0, 0xE6, 0xED);

    private final JLabel courseTitleLabel;
    private final JLabel subDetailsLabel;
    private final JLabel gradeBadge;

    private final CustomProgressBar firstClassBar;
    private final JLabel firstClassStatusLabel;
    private final CustomProgressBar secondUpperBar;
    private final JLabel secondUpperStatusLabel;
    private final CustomProgressBar secondLowerBar;
    private final JLabel secondLowerStatusLabel;

    private final JLabel gpvValueLabel;
    private final JLabel creditsEarnedLabel;

    public GpaBreakdownPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));

        courseTitleLabel = new JLabel("Select Course Module");
        courseTitleLabel.setFont(AdminTheme.font(Font.BOLD, 16));
        courseTitleLabel.setForeground(AdminTheme.FOREST_GREEN);

        subDetailsLabel = new JLabel("Academic Performance & Honours Benchmark");
        subDetailsLabel.setFont(AdminTheme.font(Font.PLAIN, 13));
        subDetailsLabel.setForeground(AdminTheme.MUTED);

        titleGroup.add(courseTitleLabel);
        titleGroup.add(Box.createVerticalStrut(3));
        titleGroup.add(subDetailsLabel);

        gradeBadge = new JLabel("Grade: -");
        gradeBadge.setFont(AdminTheme.font(Font.BOLD, 12));
        gradeBadge.setForeground(AdminTheme.FOREST_GREEN);
        gradeBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.FOREST_GREEN, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        headerRow.add(titleGroup, BorderLayout.WEST);
        headerRow.add(gradeBadge, BorderLayout.EAST);
        add(headerRow);

        add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xEAEAEA));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(14));

        JLabel benchHeading = new JLabel("Degree Classification Benchmarks");
        benchHeading.setFont(AdminTheme.font(Font.BOLD, 14));
        benchHeading.setForeground(AdminTheme.DEEP_GREEN);
        benchHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(benchHeading);
        add(Box.createVerticalStrut(14));

        firstClassStatusLabel = new JLabel("Current: 0.00");
        firstClassBar = new CustomProgressBar(0, AdminTheme.FOREST_GREEN);
        add(createBenchmarkRow("First Class Honours (CGPA >= 3.70)", firstClassStatusLabel, firstClassBar));
        add(Box.createVerticalStrut(12));

        secondUpperStatusLabel = new JLabel("Pending");
        secondUpperBar = new CustomProgressBar(0, AdminTheme.MEDIUM_GREEN);
        add(createBenchmarkRow("Second Class Upper (CGPA >= 3.30)", secondUpperStatusLabel, secondUpperBar));
        add(Box.createVerticalStrut(12));

        secondLowerStatusLabel = new JLabel("Pending");
        secondLowerBar = new CustomProgressBar(0, AdminTheme.MEDIUM_GREEN);
        add(createBenchmarkRow("Second Class Lower (CGPA >= 3.00)", secondLowerStatusLabel, secondLowerBar));
        add(Box.createVerticalStrut(16));

        JPanel pillsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        pillsRow.setOpaque(false);
        pillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pillsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        gpvValueLabel = new JLabel("Grade Point (GPV): 0.00");
        creditsEarnedLabel = new JLabel("Credits Counted: 0");
        pillsRow.add(createStatPill(gpvValueLabel));
        pillsRow.add(createStatPill(creditsEarnedLabel));
        add(pillsRow);

        add(Box.createVerticalStrut(12));

        JPanel noticeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        noticeCard.setOpaque(true);
        noticeCard.setBackground(AdminTheme.MINT);
        noticeCard.setBorder(BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1, true));
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noticeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel infoText = new JLabel("Senate Regulation: Minimum CGPA of 2.00 required for award of degree.");
        infoText.setFont(AdminTheme.font(Font.PLAIN, 12));
        infoText.setForeground(AdminTheme.DEEP_GREEN);
        noticeCard.add(infoText);
        add(noticeCard);
    }

    public void updateCourseSelection(StudentCourseGradeDTO data) {
        if (data == null) return;

        courseTitleLabel.setText(data.courseName + " (" + data.courseCode + ")");
        gradeBadge.setText("Grade: " + data.grade);
        gpvValueLabel.setText(String.format("Grade Point (GPV): %.2f", data.gpv));
        creditsEarnedLabel.setText("Credits Counted: " + data.credits);

        if (data.grade.startsWith("A")) {
            gradeBadge.setForeground(AdminTheme.FOREST_GREEN);
        } else if (data.grade.startsWith("B")) {
            gradeBadge.setForeground(AdminTheme.MEDIUM_GREEN);
        } else if (data.grade.startsWith("C")) {
            gradeBadge.setForeground(AdminTheme.WARNING);
        } else {
            gradeBadge.setForeground(AdminTheme.DANGER);
        }
    }

    public void updateHonoursBenchmarks(TranscriptSummaryDTO summary) {
        if (summary == null) return;
        double cgpa = summary.cgpa;

        firstClassStatusLabel.setText(String.format("Current: %.2f", cgpa));
        int firstClassProgress = (int) Math.min(100, Math.round((cgpa / 4.0) * 100.0));
        firstClassBar.setProgress(firstClassProgress);

        if (cgpa >= 3.70) {
            firstClassStatusLabel.setText("Qualified (First Class)");
            firstClassStatusLabel.setForeground(AdminTheme.FOREST_GREEN);
        } else {
            firstClassStatusLabel.setForeground(AdminTheme.MUTED);
        }

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

    private JPanel createBenchmarkRow(String name, JLabel valLbl, CustomProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(name);
        title.setFont(AdminTheme.font(Font.PLAIN, 13));
        title.setForeground(new Color(0x33, 0x33, 0x33));

        valLbl.setFont(AdminTheme.font(Font.BOLD, 12));

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
        private final Color fillColor;

        CustomProgressBar(int progress, Color fillColor) {
            this.progress = progress;
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(0, 8));
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

            g2.setColor(AdminTheme.MINT);
            g2.fillRoundRect(0, 0, width, height, height, height);

            int fillWidth = (int) (width * (progress / 100.0));
            if (fillWidth > 0) {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, fillWidth, height, height, height);
            }

            g2.dispose();
        }
    }
}