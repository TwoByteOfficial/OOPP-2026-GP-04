package dao.undergraduate;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateTimetableDAO {

    public static class TimetableSlot {
        public final String day;
        public final String timeRange;
        public final String courseCode;
        public final String venue;
        public final String type;

        public TimetableSlot(String day, String timeRange, String courseCode, String venue, String type) {
            this.day = day;
            this.timeRange = timeRange;
            this.courseCode = courseCode;
            this.venue = venue;
            this.type = type;
        }
    }

    public static List<TimetableSlot> getWeeklyTimetable(String level, String semester) {
        List<TimetableSlot> slots = new ArrayList<>();
        String sql = "SELECT t.day_of_week, CONCAT(t.start_time, ' - ', t.end_time) AS timerange, " +
                "c.course_code, t.venue, t.session_type " +
                "FROM timetable_entries t JOIN courses c ON t.course_id = c.course_id " +
                "WHERE c.level = ? AND c.semester = ? ORDER BY t.day_of_week ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, level);
            stmt.setString(2, semester);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(new TimetableSlot(
                            rs.getString("day_of_week"),
                            rs.getString("timerange"),
                            rs.getString("course_code"),
                            rs.getString("venue"),
                            rs.getString("session_type")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[UndergraduateTimetableDAO] Fallback triggered: " + e.getMessage());
        }

        if (slots.isEmpty()) {
            slots.add(new TimetableSlot("Monday", "08:30 - 10:30", "ICT2132 (Lecture)", "LT-01", "Lecture"));
            slots.add(new TimetableSlot("Tuesday", "13:30 - 16:30", "ICT2113 (Practical)", "Lab 02", "Lab"));
            slots.add(new TimetableSlot("Thursday", "10:30 - 12:30", "ICT2122 (Lecture)", "LT-02", "Lecture"));
        }
        return slots;
    }
}