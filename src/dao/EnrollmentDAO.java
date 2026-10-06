package dao;

import util.DBConnection;
import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public List<Course> getCoursesByStudent(long studentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c JOIN enrollments e ON e.course_id = c.course_id " +
                     "WHERE e.student_id = ? AND e.enrollment_status = 'Enrolled' ORDER BY c.course_code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
}
