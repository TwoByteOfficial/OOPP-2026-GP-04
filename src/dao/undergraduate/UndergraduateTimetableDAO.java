package dao.undergraduate;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Weekly Academic Lecture and Practical Timetable.
 */
public class UndergraduateTimetableDAO {

    public static class TimetableSlotDTO {
        public long timetableId;
        public String dayOfWeek;
        public String timeRange;
        public String courseCode;
        public String courseName;
        public String component; // 'Theory' or 'Practical'
        public String venue;
        public String lecturerName;
    }

    public static class TimetableSummaryDTO {
        public int weeklyWorkloadHours = 0;
        public int activeModulesCount = 0;
        public int practicalLabsCount = 0;
        public int activeDaysCount = 0;
    }

    public static List<TimetableSlotDTO> getStudentWeeklyTimetable(long studentId) {
        List<TimetableSlotDTO> list = new ArrayList<>();
        // Query timetable entries for courses the student is enrolled in
        String sql = "SELECT t.timetable_id, t.day_of_week, " +
                "DATE_FORMAT(t.start_time, '%H:%i') AS start_t, DATE_FORMAT(t.end_time, '%H:%i') AS end_t, " +
                "c.course_code, c.course_name, t.component, t.venue, " +
                "COALESCE(u.full_name, 'Academic Staff') AS lecturer_name " +
                "FROM timetable_entries t " +
                "JOIN courses c ON t.course_id = c.course_id " +
                "JOIN enrollments e ON c.course_id = e.course_id " +
                "LEFT JOIN users u ON t.lecturer_id = u.user_id " +
                "WHERE e.student_id = ? AND e.enrollment_status = 'Enrolled' " +
                "ORDER BY FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), t.start_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimetableSlotDTO dto = new TimetableSlotDTO();
                    dto.timetableId = rs.getLong("timetable_id");
                    dto.dayOfWeek = rs.getString("day_of_week");
                    dto.timeRange = rs.getString("start_t") + " - " + rs.getString("end_t");
                    dto.courseCode = rs.getString("course_code");
                    dto.courseName = rs.getString("course_name");
                    dto.component = rs.getString("component");
                    dto.venue = rs.getString("venue");
                    dto.lecturerName = rs.getString("lecturer_name");
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static TimetableSummaryDTO getTimetableSummary(long studentId) {
        TimetableSummaryDTO summary = new TimetableSummaryDTO();
        List<TimetableSlotDTO> slots = getStudentWeeklyTimetable(studentId);

        java.util.Set<String> distinctDays = new java.util.HashSet<>();
        java.util.Set<String> distinctCourses = new java.util.HashSet<>();

        for (TimetableSlotDTO s : slots) {
            distinctDays.add(s.dayOfWeek);
            distinctCourses.add(s.courseCode);
            summary.weeklyWorkloadHours += 2; // Default 2 hours per session slot
            if ("Practical".equalsIgnoreCase(s.component)) {
                summary.practicalLabsCount++;
            }
        }

        summary.activeDaysCount = distinctDays.size();
        summary.activeModulesCount = distinctCourses.size();
        return summary;
    }
}