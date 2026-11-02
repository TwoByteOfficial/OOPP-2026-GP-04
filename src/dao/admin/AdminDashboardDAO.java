package dao.admin;

import util.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardDAO {

    public Map<String, Integer> getDashboardSummaryCounts() throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("admins", 0);
        counts.put("lecturers", 0);
        counts.put("technical_officers", 0);
        counts.put("undergraduates", 0);

        String userSql = "SELECT role, COUNT(*) AS total FROM users GROUP BY role";
        try (Statement stmt = DBConnection.ADMIN_CON.createStatement(); ResultSet rs = stmt.executeQuery(userSql)) {
            while (rs.next()) {
                String role = rs.getString("role");
                int total = rs.getInt("total");
                if ("Admin".equalsIgnoreCase(role)) counts.put("admins", total);
                else if ("Lecturer".equalsIgnoreCase(role)) counts.put("lecturers", total);
                else if ("Technical Officer".equalsIgnoreCase(role)) counts.put("technical_officers", total);
                else if ("Undergraduate".equalsIgnoreCase(role)) counts.put("undergraduates", total);
            }
        }

        String courseSql = "SELECT COUNT(*) FROM courses WHERE status = 'Active'";
        try (Statement stmt = DBConnection.ADMIN_CON.createStatement(); ResultSet rs = stmt.executeQuery(courseSql)) {
            if (rs.next()) counts.put("courses", rs.getInt(1));
        }

        String noticeSql = "SELECT COUNT(*) FROM notices WHERE status = 'Published'";
        try (Statement stmt = DBConnection.ADMIN_CON.createStatement(); ResultSet rs = stmt.executeQuery(noticeSql)) {
            if (rs.next()) counts.put("notices", rs.getInt(1));
        }

        String timetableSql = "SELECT COUNT(*) FROM timetable_entries";
        try (Statement stmt = DBConnection.ADMIN_CON.createStatement(); ResultSet rs = stmt.executeQuery(timetableSql)) {
            if (rs.next()) counts.put("timetables", rs.getInt(1));
        }

        return counts;
    }

    // Get attendance distribution for one course.
    public Map<String, Object> getAttendanceDistribution(String courseCode) throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        String sql = """
            SELECT
                (SELECT COUNT(*) FROM users WHERE role = 'Undergraduate') AS total_students,
                COUNT(*) AS assessed_students,
                COALESCE(SUM(CASE WHEN raw_attendance_percent > 80 THEN 1 ELSE 0 END), 0) AS above_80_count,
                COALESCE(SUM(CASE WHEN raw_attendance_percent = 80 THEN 1 ELSE 0 END), 0) AS exact_80_count,
                COALESCE(SUM(CASE WHEN raw_attendance_percent < 80 THEN 1 ELSE 0 END), 0) AS below_80_count
            FROM v_attendance_summary
            WHERE course_code = ? AND total_sessions > 0
            """;

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_students");
                    int assessed = rs.getInt("assessed_students");
                    int above = rs.getInt("above_80_count");
                    int exact = rs.getInt("exact_80_count");
                    int below = rs.getInt("below_80_count");

                    stats.put("total_students", total);
                    stats.put("assessed_students", assessed);
                    stats.put("above_80_count", above);
                    stats.put("exact_80_count", exact);
                    stats.put("below_80_count", below);
                    stats.put("above_80_pct", calculatePercentage(above, assessed));
                    stats.put("exact_80_pct", calculatePercentage(exact, assessed));
                    stats.put("below_80_pct", calculatePercentage(below, assessed));
                }
            }
        }

        return stats;
    }

    private double calculatePercentage(int count, int total) {
        return total > 0 ? (count * 100.0) / total : 0.0;
    }

    public List<Map<String, Object>> getRecentActivities(int limit) throws SQLException {
        List<Map<String, Object>> activities = new ArrayList<>();

        String sql = "SELECT 'Notice' AS type, title AS description, published_at AS activity_time FROM notices " +
                "UNION ALL " +
                "SELECT 'User Registration' AS type, CONCAT(full_name, ' (', role, ')') AS description, created_at AS activity_time FROM users " +
                "ORDER BY activity_time DESC LIMIT ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("type", rs.getString("type"));
                    activity.put("description", rs.getString("description"));
                    activity.put("timestamp", rs.getTimestamp("activity_time"));
                    activities.add(activity);
                }
            }
        }

        return activities;
    }
}