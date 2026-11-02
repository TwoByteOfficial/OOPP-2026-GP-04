package dao.Lecturer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // READ: Get courses assigned to a lecturer.
    public List<Object[]> getCoursesByLecturer(long lecturerUserId)
            throws SQLException {

        List<Object[]> courses = new ArrayList<>();

        String sql = "SELECT c.course_id, c.course_code, c.course_name, " +
                "c.total_credits, c.theory_credits, c.practical_credits, " +
                "c.ca_weight, COUNT(DISTINCT e.student_id) AS student_count " +
                "FROM courses c " +
                "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                "LEFT JOIN enrollments e ON c.course_id = e.course_id " +
                "AND c.academic_year = e.academic_year " +
                "AND c.semester = e.semester " +
                "AND e.enrollment_status = 'Enrolled' " +
                "WHERE cl.lecturer_id = ? AND c.status = 'Active' " +
                "GROUP BY c.course_id, c.course_code, c.course_name, " +
                "c.total_credits, c.theory_credits, c.practical_credits, " +
                "c.ca_weight ORDER BY c.course_code";

        try (PreparedStatement ps = DBConnection.LEC_CON.prepareStatement(sql)) {
            ps.setLong(1, lecturerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] course = {
                            rs.getInt("course_id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getBigDecimal("total_credits"),
                            rs.getBigDecimal("theory_credits"),
                            rs.getBigDecimal("practical_credits"),
                            rs.getBigDecimal("ca_weight"),
                            rs.getInt("student_count")
                    };
                    courses.add(course);
                }
            }
        }

        return courses;
    }

    // READ: Get enrolled students for a selected course.
    public List<Object[]> getEnrolledStudentsByCourse(int courseId)
            throws SQLException {

        List<Object[]> students = new ArrayList<>();

        String sql = "SELECT u.registration_no, usr.full_name, " +
                "u.student_type, att.adjusted_attendance_percent, " +
                "elig.ca_mark " +
                "FROM enrollments e " +
                "JOIN undergraduates u ON e.student_id = u.user_id " +
                "JOIN users usr ON u.user_id = usr.user_id " +
                "LEFT JOIN v_attendance_summary att " +
                "ON att.student_id = e.student_id " +
                "AND att.course_id = e.course_id " +
                "LEFT JOIN v_eligibility elig " +
                "ON elig.student_id = e.student_id " +
                "AND elig.course_id = e.course_id " +
                "WHERE e.course_id = ? " +
                "AND e.enrollment_status = 'Enrolled' " +
                "ORDER BY u.registration_no";

        try (PreparedStatement ps = DBConnection.LEC_CON.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    BigDecimal attendance =
                            rs.getBigDecimal("adjusted_attendance_percent");

                    BigDecimal caMark = rs.getBigDecimal("ca_mark");

                    String attendanceText;

                    if (attendance == null) {
                        attendanceText = "N/A";
                    } else {
                        attendanceText =
                                attendance.stripTrailingZeros()
                                        .toPlainString() + "%";
                    }

                    String caEligibility;

                    if (caMark == null) {
                        caEligibility = "Pending";
                    } else if (caMark.compareTo(
                            new BigDecimal("40")) >= 0) {
                        caEligibility = "Eligible CA";
                    } else {
                        caEligibility = "CA < 40";
                    }

                    Object[] student = {
                            rs.getString("registration_no"),
                            rs.getString("full_name"),
                            rs.getString("student_type"),
                            attendanceText,
                            caEligibility
                    };

                    students.add(student);
                }
            }
        }

        return students;
    }
}