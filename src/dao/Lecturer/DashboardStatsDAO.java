package dao.Lecturer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardStatsDAO {

    public int getUndergraduateCount(long lecturerId)
            throws SQLException {

        String sql =
                "SELECT COUNT(DISTINCT e.student_id) " +
                        "FROM courses c " +
                        "JOIN course_lecturers cl " +
                        "ON c.course_id = cl.course_id " +
                        "JOIN enrollments e " +
                        "ON c.course_id = e.course_id " +
                        "AND c.academic_year = e.academic_year " +
                        "AND c.semester = e.semester " +
                        "WHERE cl.lecturer_id = ? " +
                        "AND c.status = 'Active' " +
                        "AND e.enrollment_status = 'Enrolled'";

        try (PreparedStatement ps = DBConnection.LEC_CON.prepareStatement(sql)) {

            ps.setLong(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public int getPendingMarksCount(long lecturerId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM courses c " +
                        "JOIN course_lecturers cl " +
                        "ON c.course_id = cl.course_id " +
                        "JOIN assessments a " +
                        "ON c.course_id = a.course_id " +
                        "JOIN enrollments e " +
                        "ON c.course_id = e.course_id " +
                        "AND c.academic_year = e.academic_year " +
                        "AND c.semester = e.semester " +
                        "LEFT JOIN marks m " +
                        "ON a.assessment_id = m.assessment_id " +
                        "AND e.student_id = m.student_id " +
                        "WHERE cl.lecturer_id = ? " +
                        "AND c.status = 'Active' " +
                        "AND e.enrollment_status = 'Enrolled' " +
                        "AND m.mark_id IS NULL";

        try (PreparedStatement ps = DBConnection.LEC_CON.prepareStatement(sql)) {

            ps.setLong(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Count published, non-expired notices visible to this lecturer.
     */
    public int getNewNoticesCount(long lecturerId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM notices n " +
                        "WHERE n.status = 'Published' " +
                        "AND n.target_role IN ('All', 'Lecturer') " +
                        "AND (n.expires_at IS NULL " +
                        "OR n.expires_at > CURRENT_TIMESTAMP) " +
                        "AND (n.department_id IS NULL " +
                        "OR n.department_id = " +
                        "(SELECT department_id FROM users WHERE user_id = ?))";

        try (PreparedStatement ps = DBConnection.LEC_CON.prepareStatement(sql)) {

            ps.setLong(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
}