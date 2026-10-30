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
        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(userSql)) {

            while (rs.next()) {
                String role = rs.getString("role");
                int total = rs.getInt("total");
                if ("Admin".equalsIgnoreCase(role)) {
                    counts.put("admins", total);
                } else if ("Lecturer".equalsIgnoreCase(role)) {
                    counts.put("lecturers", total);
                } else if ("Technical Officer".equalsIgnoreCase(role)) {
                    counts.put("technical_officers", total);
                } else if ("Undergraduate".equalsIgnoreCase(role)) {
                    counts.put("undergraduates", total);
                }
            }
        }

        String courseSql = "SELECT COUNT(*) FROM courses WHERE status = 'Active'";
        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(courseSql)) {
            if (rs.next()) {
                counts.put("courses", rs.getInt(1));
            }
        }

        String noticeSql = "SELECT COUNT(*) FROM notices WHERE status = 'Published'";
        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(noticeSql)) {
            if (rs.next()) {
                counts.put("notices", rs.getInt(1));
            }
        }

        String timetableSql = "SELECT COUNT(*) FROM timetable_entries";
        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(timetableSql)) {
            if (rs.next()) {
                counts.put("timetables", rs.getInt(1));
            }
        }

        return counts;
    }

    public List<Map<String, Object>> getRecentActivities(int limit) throws SQLException {
        List<Map<String, Object>> activities = new ArrayList<>();

        String sql = "SELECT 'Notice' AS type, title AS description, published_at AS activity_time FROM notices " +
                     "UNION ALL " +
                     "SELECT 'User Registration' AS type, CONCAT(full_name, ' (', role, ')') AS description, created_at AS activity_time FROM users " +
                     "ORDER BY activity_time DESC LIMIT ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

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
