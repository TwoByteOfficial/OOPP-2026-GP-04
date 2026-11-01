
package gui.admin.DashboardComponents;

import util.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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

        Map<String, Object> stats = fetchLiveAttendanceData();

        JPanel container = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(WHITE);
                g2.fillRoundRect(
                        0, 0,
                        getWidth(), getHeight(),
                        12, 12
                );

                g2.setColor(new Color(0x1B, 0x5E, 0x20, 35));

                g2.drawRoundRect(
                        0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        12, 12
                );

                g2.dispose();
            }
        };

        container.setBounds(0, 0, 790, 260);
        container.setOpaque(false);

        // Title
        JLabel title = new JLabel("Faculty Attendance Distribution");

        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(FOREST_GREEN);
        title.setBounds(30, 18, 600, 30);

        container.add(title);

        // Statistics
        int totalStudents = getInt(stats, "total_students");
        int assessedStudents = getInt(stats, "assessed_students");

        JLabel subtitle = new JLabel(
                "Total undergraduates: " + totalStudents
                        + " | Attendance recorded: " + assessedStudents
        );

        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setBounds(30, 48, 600, 22);

        container.add(subtitle);

        // Above 80%
        int aboveCount = getInt(stats, "above_80_count");
        double abovePct = getDouble(stats, "above_80_pct");

        // Exactly 80%
        int exactCount = getInt(stats, "exact_80_count");
        double exactPct = getDouble(stats, "exact_80_pct");

        // Below 80%
        int belowCount = getInt(stats, "below_80_count");
        double belowPct = getDouble(stats, "below_80_pct");

        String aboveText = String.format(
                "%d students (%.1f%%)",
                aboveCount, abovePct
        );

        String exactText = String.format(
                "%d students (%.1f%%)",
                exactCount, exactPct
        );

        String belowText = String.format(
                "%d students (%.1f%%)",
                belowCount, belowPct
        );

        // Attendance rows
        addAttendanceRow(
                container,
                "Above 80% Attendance",
                aboveText,
                abovePct,
                82,
                FOREST_GREEN
        );

        addAttendanceRow(
                container,
                "Exactly 80% Attendance",
                exactText,
                exactPct,
                142,
                new Color(0xF0, 0xAD, 0x4E)
        );

        addAttendanceRow(
                container,
                "Below 80% Attendance",
                belowText,
                belowPct,
                202,
                new Color(0xDC, 0x35, 0x45)
        );

        add(container);
    }

    /*
     * Fetch attendance distribution from the database.
     */
    private Map<String, Object> fetchLiveAttendanceData() {

        Map<String, Object> stats = new HashMap<>();

        // Default values
        stats.put("total_students", 0);
        stats.put("assessed_students", 0);

        stats.put("above_80_count", 0);
        stats.put("above_80_pct", 0.0);

        stats.put("exact_80_count", 0);
        stats.put("exact_80_pct", 0.0);

        stats.put("below_80_count", 0);
        stats.put("below_80_pct", 0.0);

        String sql = """
                SELECT
                    COUNT(*) AS total_students,

                    SUM(
                        CASE
                            WHEN attendance_data.total_sessions > 0
                            THEN 1 ELSE 0
                        END
                    ) AS assessed_students,

                    SUM(
                        CASE
                            WHEN attendance_data.total_sessions > 0
                            AND attendance_data.present_sessions * 5
                                > attendance_data.total_sessions * 4
                            THEN 1 ELSE 0
                        END
                    ) AS above_80_count,

                    SUM(
                        CASE
                            WHEN attendance_data.total_sessions > 0
                            AND attendance_data.present_sessions * 5
                                = attendance_data.total_sessions * 4
                            THEN 1 ELSE 0
                        END
                    ) AS exact_80_count,

                    SUM(
                        CASE
                            WHEN attendance_data.total_sessions > 0
                            AND attendance_data.present_sessions * 5
                                < attendance_data.total_sessions * 4
                            THEN 1 ELSE 0
                        END
                    ) AS below_80_count

                FROM (
                    SELECT
                        u.user_id,

                        COUNT(ar.attendance_record_id) AS total_sessions,

                        SUM(
                            CASE
                                WHEN ar.attendance_status IN (
                                    'PRESENT',
                                    'LATE',
                                    'MEDICAL'
                                )
                                THEN 1 ELSE 0
                            END
                        ) AS present_sessions

                    FROM users u

                    LEFT JOIN attendance_records ar
                        ON u.user_id = ar.student_id

                    WHERE u.role = 'Undergraduate'

                    GROUP BY u.user_id

                ) AS attendance_data
                """;

        try {

            Connection con = DBConnection.ADMIN_CON;

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int totalStudents = rs.getInt("total_students");
                    int assessedStudents = rs.getInt("assessed_students");

                    int above80 = rs.getInt("above_80_count");
                    int exact80 = rs.getInt("exact_80_count");
                    int below80 = rs.getInt("below_80_count");

                    // Calculate distribution percentages
                    double abovePct = calculatePercentage(
                            above80,
                            assessedStudents
                    );

                    double exactPct = calculatePercentage(
                            exact80,
                            assessedStudents
                    );

                    double belowPct = calculatePercentage(
                            below80,
                            assessedStudents
                    );

                    // Store statistics
                    stats.put("total_students", totalStudents);
                    stats.put("assessed_students", assessedStudents);

                    stats.put("above_80_count", above80);
                    stats.put("above_80_pct", abovePct);

                    stats.put("exact_80_count", exact80);
                    stats.put("exact_80_pct", exactPct);

                    stats.put("below_80_count", below80);
                    stats.put("below_80_pct", belowPct);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "StatusSummary Database Error: " + e.getMessage()
            );
        }

        return stats;
    }

    /*
     * Calculate percentage safely.
     */
    private double calculatePercentage(int count, int total) {

        if (total <= 0) {
            return 0.0;
        }

        return (count * 100.0) / total;
    }

    /*
     * Safely retrieve integer values.
     */
    private int getInt(Map<String, Object> stats, String key) {

        Object value = stats.get(key);

        return value instanceof Number
                ? ((Number) value).intValue()
                : 0;
    }

    /*
     * Safely retrieve decimal values.
     */
    private double getDouble(Map<String, Object> stats, String key) {

        Object value = stats.get(key);

        return value instanceof Number
                ? ((Number) value).doubleValue()
                : 0.0;
    }

    /*
     * Add attendance distribution row.
     */
    private void addAttendanceRow(
            JPanel panel,
            String title,
            String value,
            double percentage,
            int y,
            Color barColor
    ) {

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(MUTED);
        titleLabel.setBounds(30, y, 350, 24);

        panel.add(titleLabel);

        JLabel valueLabel = new JLabel(
                value,
                SwingConstants.RIGHT
        );

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(DEEP_GREEN);
        valueLabel.setBounds(360, y, 400, 24);

        panel.add(valueLabel);

        JPanel progressBar = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Background
                g2.setColor(PASTEL_GREEN);

                g2.fillRoundRect(
                        0, 0,
                        getWidth(), getHeight(),
                        10, 10
                );

                // Progress
                int fillWidth = (int) (
                        getWidth()
                                * Math.max(0, Math.min(100, percentage))
                                / 100.0
                );

                if (fillWidth > 0) {

                    g2.setColor(barColor);

                    g2.fillRoundRect(
                            0, 0,
                            fillWidth, getHeight(),
                            10, 10
                    );
                }

                g2.dispose();
            }
        };

        progressBar.setBounds(30, y + 26, 730, 10);
        progressBar.setOpaque(false);

        panel.add(progressBar);
    }
}
