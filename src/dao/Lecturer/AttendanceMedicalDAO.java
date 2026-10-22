package dao.Lecturer;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceMedicalDAO {

    // Retrieve attendance summary for a course taught by lecturer, filtered by component (Theory, Practical, or Combined)
    public List<Object[]> getAttendanceSummary(long lecturerUserId, int courseId, String componentFilter)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT e.student_id, ug.registration_no, usr.full_name, " +
                "COUNT(ar.attendance_record_id) AS total_sessions, " +
                "SUM(CASE WHEN ar.attendance_status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) AS attended_sessions, " +
                "SUM(CASE WHEN ar.attendance_status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_sessions, " +
                "SUM(CASE WHEN ar.attendance_status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_sessions, " +
                "(SUM(CASE WHEN ar.attendance_status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(ar.attendance_record_id), 0)) AS raw_percent, " +
                "((SUM(CASE WHEN ar.attendance_status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) + " +
                "  SUM(CASE WHEN ar.attendance_status = 'MEDICAL' AND EXISTS (" +
                "      SELECT 1 FROM medicals m WHERE m.attendance_record_id = ar.attendance_record_id AND m.approval_status = 'Approved'" +
                "  ) THEN 1 ELSE 0 END)) * 100.0 / NULLIF(COUNT(ar.attendance_record_id), 0)) AS adjusted_percent " +
                "FROM enrollments e " +
                "JOIN undergraduates ug ON e.student_id = ug.user_id " +
                "JOIN users usr ON ug.user_id = usr.user_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                "LEFT JOIN attendance_sessions ats ON ats.course_id = c.course_id " +
                "LEFT JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id AND ar.student_id = e.student_id " +
                "WHERE cl.lecturer_id = ? " +
                "AND c.course_id = ? " +
                "AND e.enrollment_status = 'Enrolled' "
        );

        if (componentFilter != null && !componentFilter.equalsIgnoreCase("Combined") && !componentFilter.equalsIgnoreCase("All")) {
            sql.append("AND ats.component = ? ");
        }

        sql.append("GROUP BY e.student_id, ug.registration_no, usr.full_name ");
        sql.append("ORDER BY ug.registration_no");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            statement.setLong(1, lecturerUserId);
            statement.setInt(2, courseId);

            if (componentFilter != null && !componentFilter.equalsIgnoreCase("Combined") && !componentFilter.equalsIgnoreCase("All")) {
                statement.setString(3, componentFilter);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    java.math.BigDecimal adjusted = rs.getBigDecimal("adjusted_percent");
                    java.math.BigDecimal raw = rs.getBigDecimal("raw_percent");

                    String rawStr = raw == null ? "N/A" : raw.setScale(1, java.math.RoundingMode.HALF_UP) + "%";
                    String adjStr = adjusted == null ? "N/A" : adjusted.setScale(1, java.math.RoundingMode.HALF_UP) + "%";

                    String status = "N/A";
                    if (adjusted != null) {
                        status = adjusted.compareTo(new java.math.BigDecimal("80")) >= 0 ? "Eligible (>=80%)" : "Ineligible (<80%)";
                    }

                    rows.add(new Object[]{
                            rs.getString("registration_no"),
                            rs.getString("full_name"),
                            rs.getInt("total_sessions"),
                            rs.getInt("attended_sessions"),
                            rs.getInt("medical_sessions"),
                            rs.getInt("absent_sessions"),
                            rawStr,
                            adjStr,
                            status
                    });
                }
            }
        }

        return rows;
    }

    // Retrieve medical certificate submissions for students in lecturer's courses
    public List<Object[]> getMedicalsByLecturer(long lecturerUserId, Integer courseIdFilter)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT m.medical_id, ug.registration_no, usr.full_name, c.course_code, " +
                "m.medical_date, m.reason, m.approval_status, m.review_comment, m.submitted_at " +
                "FROM medicals m " +
                "JOIN undergraduates ug ON m.student_id = ug.user_id " +
                "JOIN users usr ON ug.user_id = usr.user_id " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                "WHERE cl.lecturer_id = ? "
        );

        if (courseIdFilter != null && courseIdFilter > 0) {
            sql.append("AND c.course_id = ? ");
        }

        sql.append("ORDER BY m.submitted_at DESC");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            statement.setLong(1, lecturerUserId);
            if (courseIdFilter != null && courseIdFilter > 0) {
                statement.setInt(2, courseIdFilter);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getLong("medical_id"),
                            rs.getString("registration_no"),
                            rs.getString("full_name"),
                            rs.getString("course_code"),
                            rs.getDate("medical_date"),
                            rs.getString("reason"),
                            rs.getString("approval_status"),
                            rs.getString("review_comment") == null ? "N/A" : rs.getString("review_comment")
                    });
                }
            }
        }

        return rows;
    }
}
