package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AttendanceSummary extends JPanel {

    public AttendanceSummary(Map<String, Integer> metrics) {
        setLayout(null);
        setBounds(0, 0, 790, 260);
        setOpaque(false);

        JPanel container = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(AdminTheme.CARD_SHADOW);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        container.setBounds(0, 0, 790, 260);
        container.setOpaque(false);

        JLabel title = new JLabel("Administrative Quick Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AdminTheme.FOREST_GREEN);
        title.setBounds(25, 18, 450, 30);
        container.add(title);

        JLabel subtitle = new JLabel("System alert counters and active operational entries");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setBounds(25, 48, 500, 22);
        container.add(subtitle);

        addStatusRow(container, "Pending Medical Submissions", "2 Pending", 82, AdminTheme.WARNING);
        addStatusRow(container, "Undergraduates Below 40% CA", "5 Students", 142, AdminTheme.DANGER);

        int activeTimetables = metrics.getOrDefault("timetables", 0);
        addStatusRow(container, "Active Timetable Entries", activeTimetables + " Entries", 202, AdminTheme.FOREST_GREEN);

        add(container);
    }

    private void addStatusRow(JPanel panel, String title, String value, int y, Color indicatorColor) {
        JPanel indicator = new JPanel();
        indicator.setBackground(indicatorColor);
        indicator.setBounds(25, y + 3, 8, 20);
        panel.add(indicator);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(AdminTheme.MUTED);
        titleLabel.setBounds(45, y, 420, 26);
        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(AdminTheme.DEEP_GREEN);
        valueLabel.setBounds(480, y, 285, 26);
        panel.add(valueLabel);

        JSeparator separator = new JSeparator();
        separator.setForeground(AdminTheme.PASTEL_GREEN);
        separator.setBounds(25, y + 32, 740, 1);
        panel.add(separator);
    }
}