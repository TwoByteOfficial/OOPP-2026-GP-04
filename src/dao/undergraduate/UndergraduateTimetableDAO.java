
package dao.undergraduate;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object for Weekly Academic Lecture and Practical Timetable.
 *
 * Undergraduate permissions:
 * - View enrolled course timetables.
 * - View lecturer and venue information.
 * - Calculate weekly timetable statistics.
 */
public class UndergraduateTimetableDAO {

    // ============================================================
    // 1. TIMETABLE SLOT DTO
    // ============================================================

    public static class TimetableSlotDTO {

        public long timetableId;
        public String dayOfWeek;
        public String timeRange;
        public String courseCode;
        public String courseName;
        public String component;
        public String venue;
        public String lecturerName;

        // Actual session duration in minutes
        public int durationMinutes;
    }


    // ============================================================
    // 2. TIMETABLE SUMMARY DTO
    // ============================================================

    public static class TimetableSummaryDTO {

        public int weeklyWorkloadHours = 0;
        public int activeModulesCount = 0;
        public int practicalLabsCount = 0;
        public int activeDaysCount = 0;
    }


    // ============================================================
    // 3. GET STUDENT WEEKLY TIMETABLE
    // ============================================================

    public static List<TimetableSlotDTO> getStudentWeeklyTimetable(
            long studentId) {

        List<TimetableSlotDTO> list = new ArrayList<>();

        String sql =
                "SELECT t.timetable_id, t.day_of_week, " +

                        "DATE_FORMAT(t.start_time, '%H:%i') AS start_t, " +
                        "DATE_FORMAT(t.end_time, '%H:%i') AS end_t, " +

                        "TIMESTAMPDIFF(MINUTE, t.start_time, t.end_time) " +
                        "AS duration_minutes, " +

                        "c.course_code, c.course_name, " +
                        "t.component, t.venue, " +

                        "COALESCE(u.full_name, 'Academic Staff') " +
                        "AS lecturer_name " +

                        "FROM timetable_entries t " +

                        "INNER JOIN courses c " +
                        "ON t.course_id = c.course_id " +

                        "LEFT JOIN users u " +
                        "ON t.lecturer_id = u.user_id " +

                        "WHERE EXISTS (" +
                        "SELECT 1 FROM enrollments e " +
                        "WHERE e.course_id = t.course_id " +
                        "AND e.student_id = ? " +
                        "AND e.enrollment_status = 'Enrolled'" +
                        ") " +

                        "ORDER BY FIELD(t.day_of_week, " +
                        "'Monday', 'Tuesday', 'Wednesday', " +
                        "'Thursday', 'Friday', 'Saturday', 'Sunday'), " +
                        "t.start_time ASC";

        try (Connection conn = DBConnection.UG_CON;
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    TimetableSlotDTO dto = new TimetableSlotDTO();

                    dto.timetableId = rs.getLong("timetable_id");
                    dto.dayOfWeek = rs.getString("day_of_week");

                    dto.timeRange =
                            rs.getString("start_t") +
                                    " - " +
                                    rs.getString("end_t");

                    dto.durationMinutes =
                            rs.getInt("duration_minutes");

                    dto.courseCode = rs.getString("course_code");
                    dto.courseName = rs.getString("course_name");

                    dto.component = rs.getString("component");
                    dto.venue = rs.getString("venue");

                    dto.lecturerName =
                            rs.getString("lecturer_name");

                    list.add(dto);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "[UndergraduateTimetableDAO] Timetable retrieval failed: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        return list;
    }


    // ============================================================
    // 4. GET TIMETABLE SUMMARY
    // ============================================================

    public static TimetableSummaryDTO getTimetableSummary(
            long studentId) {

        TimetableSummaryDTO summary = new TimetableSummaryDTO();

        String sql =
                "SELECT " +

                        "COUNT(DISTINCT c.course_id) AS module_count, " +

                        "COUNT(DISTINCT t.day_of_week) AS active_days, " +

                        "SUM(CASE WHEN t.component = 'Practical' " +
                        "THEN 1 ELSE 0 END) AS practical_count, " +

                        "COALESCE(SUM(TIMESTAMPDIFF(" +
                        "MINUTE, t.start_time, t.end_time)), 0) " +
                        "AS total_minutes " +

                        "FROM timetable_entries t " +

                        "INNER JOIN courses c " +
                        "ON t.course_id = c.course_id " +

                        "WHERE EXISTS (" +
                        "SELECT 1 FROM enrollments e " +
                        "WHERE e.course_id = t.course_id " +
                        "AND e.student_id = ? " +
                        "AND e.enrollment_status = 'Enrolled'" +
                        ")";

        try (Connection conn = DBConnection.UG_CON;
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    summary.activeModulesCount =
                            rs.getInt("module_count");

                    summary.activeDaysCount =
                            rs.getInt("active_days");

                    summary.practicalLabsCount =
                            rs.getInt("practical_count");

                    int totalMinutes =
                            rs.getInt("total_minutes");

                    // Convert actual minutes into rounded hours.
                    summary.weeklyWorkloadHours =
                            (int) Math.round(totalMinutes / 60.0);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "[UndergraduateTimetableDAO] Summary retrieval failed: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        return summary;
    }
}
