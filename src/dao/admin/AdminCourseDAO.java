package dao.admin;

import util.DBConnection;
import model.Course;
import model.CourseLecturer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsible for Admin Course Management (Courses page CRUD & Lecturer Assignments).
 */
public class AdminCourseDAO {

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
        }
        return list;
    }

    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        }
        return null;
    }

    public List<Course> getCoursesByDepartment(int departmentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE department_id = ? ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCourse(rs));
                }
            }
        }
        return list;
    }

    public boolean createCourse(Course c) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, total_credits, ca_weight, final_weight, department_id, academic_year, semester, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getCourseCode());
            stmt.setString(2, c.getCourseName());
            stmt.setString(3, c.getDescription());
            stmt.setBigDecimal(4, c.getTheoryCredits());
            stmt.setBigDecimal(5, c.getPracticalCredits());
            stmt.setBigDecimal(6, c.getTotalCredits());
            stmt.setBigDecimal(7, c.getCaWeight());
            stmt.setBigDecimal(8, c.getFinalWeight());
            stmt.setInt(9, c.getDepartmentId());
            stmt.setString(10, c.getAcademicYear());
            stmt.setString(11, c.getSemester());
            stmt.setString(12, c.getStatus() != null ? c.getStatus() : "Active");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setCourseId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateCourse(Course c) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, theory_credits = ?, practical_credits = ?, total_credits = ?, ca_weight = ?, final_weight = ?, department_id = ?, academic_year = ?, semester = ?, status = ? WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getCourseCode());
            stmt.setString(2, c.getCourseName());
            stmt.setString(3, c.getDescription());
            stmt.setBigDecimal(4, c.getTheoryCredits());
            stmt.setBigDecimal(5, c.getPracticalCredits());
            stmt.setBigDecimal(6, c.getTotalCredits());
            stmt.setBigDecimal(7, c.getCaWeight());
            stmt.setBigDecimal(8, c.getFinalWeight());
            stmt.setInt(9, c.getDepartmentId());
            stmt.setString(10, c.getAcademicYear());
            stmt.setString(11, c.getSemester());
            stmt.setString(12, c.getStatus());
            stmt.setInt(13, c.getCourseId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean assignLecturerToCourse(int courseId, long lecturerId, String responsibility) throws SQLException {
        String sql = "INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE responsibility = VALUES(responsibility)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setLong(2, lecturerId);
            stmt.setString(3, responsibility != null ? responsibility : "In-Charge");
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeLecturerFromCourse(int courseId, long lecturerId) throws SQLException {
        String sql = "DELETE FROM course_lecturers WHERE course_id = ? AND lecturer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setLong(2, lecturerId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<CourseLecturer> getCourseLecturers(int courseId) throws SQLException {
        List<CourseLecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM course_lecturers WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseLecturer cl = new CourseLecturer();
                    cl.setCourseId(rs.getInt("course_id"));
                    cl.setLecturerId(rs.getLong("lecturer_id"));
                    cl.setResponsibility(rs.getString("responsibility"));
                    cl.setAssignedAt(rs.getTimestamp("assigned_at"));
                    list.add(cl);
                }
            }
        }
        return list;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
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
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
