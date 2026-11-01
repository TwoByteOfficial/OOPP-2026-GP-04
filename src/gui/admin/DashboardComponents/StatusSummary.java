
package gui.admin.DashboardComponents;

import dao.admin.AdminDashboardDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatusSummary extends JPanel {

    private static final Color WHITE = Color.WHITE;
    private static final Color FOREST_GREEN = new Color(0x38, 0x8E, 0x3C);
    private static final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private static final Color MUTED = new Color(0x4F, 0x6B, 0x52);
    private static final Color PASTEL_GREEN = new Color(0xC8, 0xE6, 0xC9);

    private static final String COURSE_CODE = "ICT2132";

    private final AdminDashboardDAO dashboardDAO = new AdminDashboardDAO();

    public StatusSummary() {
        setLayout(null);
        setBounds(0, 0, 790, 260);
        setOpaque(false);
        loadAttendanceData();
    }

    // Load attendance data through DAO.
    public void loadAttendanceData() {
        try {
            Map<String, Object> stats = dashboardDAO.getAttendanceDistribution(COURSE_CODE);
            displayAttendanceData(stats);
        } catch (SQLException e) {
            e.printStackTrace();
            displayAttendanceData(new HashMap<>());
        }
    }

    // Display attendance information.
    private void displayAttendanceData(Map<String, Object> stats) {
        removeAll();

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

        JLabel title = new JLabel("Course Attendance Distribution - " + COURSE_CODE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(FOREST_GREEN);
        title.setBounds(30, 18, 700, 30);
        container.add(title);

        int totalStudents = getInt(stats, "total_students");
        int assessedStudents = getInt(stats, "assessed_students");

        JLabel subtitle = new JLabel("Total undergraduates: " + totalStudents + " | Attendance recorded: " + assessedStudents);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setBounds(30, 48, 700, 22);
        container.add(subtitle);

        int aboveCount = getInt(stats, "above_80_count");
        int exactCount = getInt(stats, "exact_80_count");
        int belowCount = getInt(stats, "below_80_count");

        double abovePct = getDouble(stats, "above_80_pct");
        double exactPct = getDouble(stats, "exact_80_pct");
        double belowPct = getDouble(stats, "below_80_pct");

        addAttendanceRow(container, "Above 80% Attendance", aboveCount, abovePct, 82, FOREST_GREEN);
        addAttendanceRow(container, "Exactly 80% Attendance", exactCount, exactPct, 142, new Color(0xF0, 0xAD, 0x4E));
        addAttendanceRow(container, "Below 80% Attendance", belowCount, belowPct, 202, new Color(0xDC, 0x35, 0x45));

        add(container);
        revalidate();
        repaint();
    }

    private int getInt(Map<String, Object> stats, String key) {
        Object value = stats.get(key);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    private double getDouble(Map<String, Object> stats, String key) {
        Object value = stats.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    private void addAttendanceRow(JPanel panel, String title, int count, double percentage, int y, Color barColor) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(MUTED);
        titleLabel.setBounds(30, y, 350, 24);
        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(String.format("%d students (%.1f%%)", count, percentage), SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(360, y, 400, 24);
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
