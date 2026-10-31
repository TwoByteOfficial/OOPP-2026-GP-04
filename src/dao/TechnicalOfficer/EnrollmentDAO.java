package dao.TechnicalOfficer;

import util.DBConnection;
import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Undergraduate;

public class EnrollmentDAO {

    public List<Course> getCoursesByStudent(long studentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c JOIN enrollments e ON e.course_id = c.course_id " +
                "WHERE e.student_id = ? AND e.enrollment_status = 'Enrolled' ORDER BY c.course_code";
        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {
            stmt.setLong(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setCourseCode(rs.getString("course_code"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setDescription(rs.getString("description"));
                    c.setTheoryCredits(rs.getBigDecimal("theory_credits"));
                    c.setPracticalCredits(rs.getBigDecimal("practical_credits"));
                    c.setTotalCredits(rs.getBigDecimal("total_credits"));
                    c.setCaWeight(rs.getBigDecimal("ca_weight"));
                    c.setFinalWeight(rs.getBigDecimal("final_weight"));
                    c.setDepartmentId(rs.getInt("department_id"));
                    c.setAcademicYear(rs.getString("academic_year"));
                    c.setSemester(rs.getString("semester"));
                    c.setStatus(rs.getString("status"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    public List<Undergraduate> getEnrolledStudentsByCourse(
            int courseId) throws SQLException {

        List<Undergraduate> students = new ArrayList<>();

        String sql = "SELECT u.user_id, u.username, u.full_name, " +
                "u.email, u.contact_no, u.user_status, " +
                "ug.registration_no, ug.batch_year, ug.student_type " +
                "FROM enrollments e " +
                "JOIN undergraduates ug ON e.student_id = ug.user_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "WHERE e.course_id = ? " +
                "AND e.enrollment_status = 'Enrolled' " +
                "AND u.user_status = 'Active' " +
                "ORDER BY ug.registration_no";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Undergraduate student = new Undergraduate();

                    student.setUserId(rs.getLong("user_id"));
                    student.setUsername(rs.getString("username"));
                    student.setFullName(rs.getString("full_name"));
                    student.setEmail(rs.getString("email"));
                    student.setContactNo(rs.getString("contact_no"));
                    student.setUserStatus(rs.getString("user_status"));

                    student.setRegistrationNo(
                            rs.getString("registration_no"));
                    student.setBatchYear(rs.getInt("batch_year"));
                    student.setStudentType(rs.getString("student_type"));

                    students.add(student);
                }
            }
        }

        return students;
    }
}