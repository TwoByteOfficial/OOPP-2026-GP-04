package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;
import util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StatusSummary extends JPanel {

    public StatusSummary() {
        setLayout(null);
        setBounds(0, 0, 790, 260);
        setOpaque(false);

        Map<String, Object> stats = fetchLiveAttendanceData();

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

        JLabel title = new JLabel("Faculty Attendance Distribution");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AdminTheme.FOREST_GREEN);
        title.setBounds(30, 18, 600, 30);
        container.add(title);

        int totalStudents = (int) stats.getOrDefault("total_students", 0);
        JLabel subtitle = new JLabel("Total enrolled undergraduates: " + totalStudents);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setBounds(30, 48, 400, 22);
        container.add(subtitle);

        int aboveCount = (int) stats.getOrDefault("above_80_count", 0);
        double abovePct = (double) stats.getOrDefault("above_80_pct", 0.0);

        int exactCount = (int) stats.getOrDefault("exact_80_count", 0);
        double exactPct = (double) stats.getOrDefault("exact_80_pct", 0.0);

        int belowCount = (int) stats.getOrDefault("below_80_count", 0);
        double belowPct = (double) stats.getOrDefault("below_80_pct", 0.0);

        String aboveText = String.format("%d students (%.1f%%)", aboveCount, abovePct);
        String exactText = String.format("%d students (%.1f%%)", exactCount, exactPct);
        String belowText = String.format("%d students (%.1f%%)", belowCount, belowPct);

        addAttendanceRow(container, "Above 80% Attendance", aboveText, abovePct, 82, AdminTheme.FOREST_GREEN);
        addAttendanceRow(container, "Exactly 80% Attendance", exactText, exactPct, 142, AdminTheme.WARNING);
        addAttendanceRow(container, "Below 80% Attendance", belowText, belowPct, 202, AdminTheme.DANGER);

        add(container);
    }

    private Map<String, Object> fetchLiveAttendanceData() {
        Map<String, Object> stats = new HashMap<>();

        int totalStudents = 0;
        int above80 = 0;
        int exact80 = 0;
        int below80 = 0;

        String sql = "SELECT u.user_id, " +
                "COUNT(ar.attendance_record_id) AS total_sessions, " +
                "SUM(CASE WHEN ar.attendance_status IN ('PRESENT', 'LATE', 'MEDICAL') THEN 1 ELSE 0 END) AS present_sessions " +
                "FROM users u " +
                "LEFT JOIN attendance_records ar ON u.user_id = ar.student_id " +
                "WHERE u.role = 'Undergraduate' " +
                "GROUP BY u.user_id";

        try (Connection con = getDbConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                totalStudents++;
                int total = rs.getInt("total_sessions");
                int present = rs.getInt("present_sessions");

                double pct = (total > 0) ? ((double) present / total) * 100.0 : 100.0;

                if (pct > 80.0) {
                    above80++;
                } else if (Math.abs(pct - 80.0) < 0.01) {
                    exact80++;
                } else {
                    below80++;
                }
            }
        } catch (Exception e) {
            try (Connection con = getDbConnection();
                 Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'Undergraduate'")) {
                if (rs.next()) {
                    totalStudents = rs.getInt(1);
                    above80 = totalStudents;
                }
            } catch (Exception ignored) {}
        }

        double above80Pct = totalStudents > 0 ? (above80 * 100.0 / totalStudents) : 0.0;
        double exact80Pct = totalStudents > 0 ? (exact80 * 100.0 / totalStudents) : 0.0;
        double below80Pct = totalStudents > 0 ? (below80 * 100.0 / totalStudents) : 0.0;

        stats.put("total_students", totalStudents);
        stats.put("above_80_count", above80);
        stats.put("above_80_pct", above80Pct);
        stats.put("exact_80_count", exact80);
        stats.put("exact_80_pct", exact80Pct);
        stats.put("below_80_count", below80);
        stats.put("below_80_pct", below80Pct);

        return stats;
    }

    private Connection getDbConnection() throws SQLException {
        try {
            return DBConnection.con();
        } catch (Throwable t) {
            return DBConnection.getConnection();
        }
    }

    private void addAttendanceRow(JPanel panel, String title, String value, double percentage, int y, Color barColor) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(AdminTheme.MUTED);
        titleLabel.setBounds(30, y, 350, 24);
        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(AdminTheme.DEEP_GREEN);
        valueLabel.setBounds(360, y, 400, 24);
        panel.add(valueLabel);

        JPanel progressBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AdminTheme.PASTEL_GREEN);
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