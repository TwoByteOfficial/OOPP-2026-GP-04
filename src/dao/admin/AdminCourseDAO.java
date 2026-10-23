package dao.admin;

import util.DBConnection;
import model.Course;
import model.CourseLecturer;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminCourseDAO {

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";

        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
        }
        return list;
    }

    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
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

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCourse(rs));
                }
            }
        }
        return list;
    }

    public boolean createCourse(Course c) throws SQLException {
        String sql1 = "INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, ca_weight, department_id, academic_year, semester, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");
            pstmt.setBigDecimal(4, c.getTheoryCredits() != null ? c.getTheoryCredits() : BigDecimal.ZERO);
            pstmt.setBigDecimal(5, c.getPracticalCredits() != null ? c.getPracticalCredits() : BigDecimal.ZERO);
            pstmt.setBigDecimal(6, c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("30.00"));
            pstmt.setInt(7, c.getDepartmentId() > 0 ? c.getDepartmentId() : 1);
            pstmt.setString(8, c.getAcademicYear() != null ? c.getAcademicYear() : "2024/2025");
            pstmt.setString(9, c.getSemester() != null ? c.getSemester() : "Semester I");
            pstmt.setString(10, c.getStatus() != null ? c.getStatus() : "Active");

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setCourseId(rs.getInt(1));
                    }
                }
                createDefaultAssessmentsForCourse(con, c);
                return true;
            }
        } catch (SQLException ex) {
            String sql2 = "INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, total_credits, ca_weight, final_weight, department_id, academic_year, semester, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnection.con();
                 PreparedStatement pstmt = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, c.getCourseCode());
                pstmt.setString(2, c.getCourseName());
                pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");
                pstmt.setBigDecimal(4, c.getTheoryCredits() != null ? c.getTheoryCredits() : BigDecimal.ZERO);
                pstmt.setBigDecimal(5, c.getPracticalCredits() != null ? c.getPracticalCredits() : BigDecimal.ZERO);
                BigDecimal total = (c.getTotalCredits() != null) ? c.getTotalCredits() : (c.getTheoryCredits().add(c.getPracticalCredits()));
                pstmt.setBigDecimal(6, total);
                pstmt.setBigDecimal(7, c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("30.00"));
                BigDecimal finalW = (c.getFinalWeight() != null) ? c.getFinalWeight() : (new BigDecimal("100.00").subtract(c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("30.00")));
                pstmt.setBigDecimal(8, finalW);
                pstmt.setInt(9, c.getDepartmentId() > 0 ? c.getDepartmentId() : 1);
                pstmt.setString(10, c.getAcademicYear() != null ? c.getAcademicYear() : "2024/2025");
                pstmt.setString(11, c.getSemester() != null ? c.getSemester() : "Semester I");
                pstmt.setString(12, c.getStatus() != null ? c.getStatus() : "Active");

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            c.setCourseId(rs.getInt(1));
                        }
                    }
                    createDefaultAssessmentsForCourse(con, c);
                    return true;
                }
            } catch (SQLException ex2) {
                throw ex;
            }
        }
        return false;
    }

    private void createDefaultAssessmentsForCourse(Connection con, Course c) {
        if (c.getCourseId() <= 0) return;
        String sql = "INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark) " +
                     "VALUES (?, 'Quiz 1', 'CA', 'Quiz', 'Theory', ?, 100.00), " +
                     "(?, 'Quiz 2', 'CA', 'Quiz', 'Theory', ?, 100.00), " +
                     "(?, 'Quiz 3', 'CA', 'Quiz', 'Theory', ?, 100.00), " +
                     "(?, 'Final Examination', 'FINAL', 'Final Examination', 'Theory', ?, 100.00)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            BigDecimal caWeight = c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("30.00");
            BigDecimal quizWeight = caWeight.divide(new BigDecimal("3"), 2, java.math.RoundingMode.HALF_UP);
            BigDecimal finalWeight = c.getFinalWeight() != null ? c.getFinalWeight() : new BigDecimal("70.00");

            pstmt.setInt(1, c.getCourseId());
            pstmt.setBigDecimal(2, quizWeight);
            pstmt.setInt(3, c.getCourseId());
            pstmt.setBigDecimal(4, quizWeight);
            pstmt.setInt(5, c.getCourseId());
            pstmt.setBigDecimal(6, quizWeight);
            pstmt.setInt(7, c.getCourseId());
            pstmt.setBigDecimal(8, finalWeight);

            pstmt.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    public boolean updateCourse(Course c) throws SQLException {
        String sql1 = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, theory_credits = ?, practical_credits = ?, ca_weight = ?, department_id = ?, academic_year = ?, semester = ?, status = ? WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql1)) {

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setString(3, c.getDescription());
            pstmt.setBigDecimal(4, c.getTheoryCredits());
            pstmt.setBigDecimal(5, c.getPracticalCredits());
            pstmt.setBigDecimal(6, c.getCaWeight());
            pstmt.setInt(7, c.getDepartmentId());
            pstmt.setString(8, c.getAcademicYear());
            pstmt.setString(9, c.getSemester());
            pstmt.setString(10, c.getStatus());
            pstmt.setInt(11, c.getCourseId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            String sql2 = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, theory_credits = ?, practical_credits = ?, total_credits = ?, ca_weight = ?, final_weight = ?, department_id = ?, academic_year = ?, semester = ?, status = ? WHERE course_id = ?";

            try (Connection con = DBConnection.con();
                 PreparedStatement pstmt = con.prepareStatement(sql2)) {

                pstmt.setString(1, c.getCourseCode());
                pstmt.setString(2, c.getCourseName());
                pstmt.setString(3, c.getDescription());
                pstmt.setBigDecimal(4, c.getTheoryCredits());
                pstmt.setBigDecimal(5, c.getPracticalCredits());
                pstmt.setBigDecimal(6, c.getTotalCredits());
                pstmt.setBigDecimal(7, c.getCaWeight());
                pstmt.setBigDecimal(8, c.getFinalWeight());
                pstmt.setInt(9, c.getDepartmentId());
                pstmt.setString(10, c.getAcademicYear());
                pstmt.setString(11, c.getSemester());
                pstmt.setString(12, c.getStatus());
                pstmt.setInt(13, c.getCourseId());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException ex2) {
                throw ex;
            }
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean assignLecturerToCourse(int courseId, long lecturerId, String responsibility) throws SQLException {
        String sql = "INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE responsibility = VALUES(responsibility)";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setLong(2, lecturerId);
            pstmt.setString(3, responsibility != null ? responsibility : "In-Charge");

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean removeLecturerFromCourse(int courseId, long lecturerId) throws SQLException {
        String sql = "DELETE FROM course_lecturers WHERE course_id = ? AND lecturer_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setLong(2, lecturerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<CourseLecturer> getCourseLecturers(int courseId) throws SQLException {
        List<CourseLecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM course_lecturers WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
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
