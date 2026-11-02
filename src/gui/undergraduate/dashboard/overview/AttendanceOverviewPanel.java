package gui.undergraduate.dashboard.overview;

import gui.admin.AdminTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 1.1.4.1 - Undergraduate Attendance & Exam Eligibility Overview Card
 */
public class AttendanceOverviewPanel extends JPanel {

    public AttendanceOverviewPanel(double theoryPct, double practicalPct, double combinedPct) {
        setLayout(new BorderLayout(0, 14));
        setBackground(AdminTheme.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD2, 0xD7, 0xE1), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Header Title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Attendance & Exam Eligibility");
        title.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 17));
        title.setForeground(AdminTheme.DEEP_GREEN);

        JLabel statusBadge = new JLabel(combinedPct >= 80.0 ? "● Eligible for Exams" : "● Below Requirement (<80%)");
        statusBadge.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 13));
        statusBadge.setForeground(combinedPct >= 80.0 ? AdminTheme.FOREST_GREEN : AdminTheme.DANGER);

        header.add(title, BorderLayout.WEST);
        header.add(statusBadge, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Progress Bars Section
        JPanel barsPanel = new JPanel();
        barsPanel.setOpaque(false);
        barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.Y_AXIS));

        barsPanel.add(createProgressRow("Theory Sessions (Requirement: 80%)", theoryPct));
        barsPanel.add(Box.createVerticalStrut(12));
        barsPanel.add(createProgressRow("Practical Sessions (Requirement: 80%)", practicalPct));
        barsPanel.add(Box.createVerticalStrut(12));
        barsPanel.add(createProgressRow("Combined Overall Attendance", combinedPct));

        add(barsPanel, BorderLayout.CENTER);
    }

    private JPanel createProgressRow(String labelText, double percentage) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setOpaque(false);

        JPanel labelBox = new JPanel(new BorderLayout());
        labelBox.setOpaque(false);

        JLabel name = new JLabel(labelText);
        name.setFont(new Font(AdminTheme.FONT_FAMILY, Font.PLAIN, 14));
        name.setForeground(AdminTheme.DEEP_GREEN);

        JLabel value = new JLabel(String.format("%.1f%%", percentage));
        value.setFont(new Font(AdminTheme.FONT_FAMILY, Font.BOLD, 14));
        value.setForeground(percentage >= 80.0 ? AdminTheme.FOREST_GREEN : AdminTheme.DANGER);

        labelBox.add(name, BorderLayout.WEST);
        labelBox.add(value, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.round(percentage));
        bar.setPreferredSize(new Dimension(100, 10));
        bar.setForeground(percentage >= 80.0 ? AdminTheme.FOREST_GREEN : AdminTheme.DANGER);
        bar.setBackground(AdminTheme.PASTEL_GREEN);
        bar.setBorderPainted(false);

        row.add(labelBox, BorderLayout.NORTH);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }
}