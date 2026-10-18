package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;
import java.util.Map; // Added Import

public class AttendanceSummary extends JPanel {

    private final Color WHITE = Color.WHITE;
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED = new Color(0x4F, 0x6B, 0x52);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    // Modified Constructor Signature to receive data variables
    public AttendanceSummary(Map<String, Integer> metrics) {
        setLayout(null);
        setBounds(0, 0, 770, 260);
        setOpaque(false);

        JPanel container = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(0x1B, 0x5E, 0x20, 35));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };

        container.setBounds(0, 0, 770, 260);
        container.setOpaque(false);

        JLabel title = new JLabel("Administrative Quick Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(FOREST_GREEN);
        title.setBounds(25, 20, 450, 30);
        container.add(title);

        addStatusRow(
                container,
                "Pending Medical Submissions",
                "2 Pending",
                70,
                new Color(0xF0, 0xAD, 0x4E)
        );

        addStatusRow(
                container,
                "Undergraduates Below 40% CA",
                "5 Students",
                130,
                new Color(0xDC, 0x35, 0x45)
        );

        // Bound to real live dataset counter retrieved from 'timetable_entries' table
        int activeTimetables = metrics.getOrDefault("timetables", 0);
        addStatusRow(
                container,
                "Active Timetable Entries",
                activeTimetables + " Entries",
                190,
                FOREST_GREEN
        );

        add(container);
    }

    private void addStatusRow(JPanel panel, String title, String value, int y, Color indicatorColor) {
        JPanel indicator = new JPanel();
        indicator.setBackground(indicatorColor);
        indicator.setBounds(25, y + 3, 8, 18);
        panel.add(indicator);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(MUTED);
        titleLabel.setBounds(45, y, 390, 24);
        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(435, y, 310, 24);
        panel.add(valueLabel);

        JSeparator separator = new JSeparator();
        separator.setForeground(PASTEL_GREEN);
        separator.setBounds(25, y + 32, 720, 1);
        panel.add(separator);
    }
}
