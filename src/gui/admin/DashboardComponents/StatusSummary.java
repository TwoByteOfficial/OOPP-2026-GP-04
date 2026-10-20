package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class StatusSummary extends JPanel {

    private final Color WHITE        = Color.WHITE;
    private final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private final Color DEEP_GREEN   = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED        = new Color(0x4F, 0x6B, 0x52);
    private final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    public StatusSummary() {
        setLayout(null);
        setBounds(0, 0, 790, 260);
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

        container.setBounds(0, 0, 790, 260);
        container.setOpaque(false);

        JLabel title = new JLabel("Faculty Attendance Distribution");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(FOREST_GREEN);
        title.setBounds(30, 20, 600, 30);
        container.add(title);

        JLabel subtitle = new JLabel("Total enrolled undergraduates: 160");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);
        subtitle.setBounds(30, 48, 400, 20);
        container.add(subtitle);

        addAttendanceRow(container, "Above 80% Attendance", "148 students (92.5%)", 92.5, 80, FOREST_GREEN);
        addAttendanceRow(container, "Exactly 80% Attendance", "8 students (5.0%)", 5.0, 140, new Color(0xF0, 0xAD, 0x4E));
        addAttendanceRow(container, "Below 80% Attendance", "4 students (2.5%)", 2.5, 200, new Color(0xDC, 0x35, 0x45));

        add(container);
    }

    private void addAttendanceRow(JPanel panel, String title, String value, double percentage, int y, Color barColor) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        titleLabel.setForeground(MUTED);
        titleLabel.setBounds(30, y, 350, 22);
        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(360, y, 400, 22);
        panel.add(valueLabel);

        JPanel progressBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PASTEL_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                int fillWidth = (int) (getWidth() * Math.max(0, Math.min(100, percentage)) / 100.0);
                if (fillWidth > 0) {
                    g2.setColor(barColor);
                    g2.fillRoundRect(0, 0, fillWidth, getHeight(), 10, 10);
                }
                g2.dispose();
            }
        };

        progressBar.setBounds(30, y + 26, 730, 10);
        progressBar.setOpaque(false);
        panel.add(progressBar);
    }
}
