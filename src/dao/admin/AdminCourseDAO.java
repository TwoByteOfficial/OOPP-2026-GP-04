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
        String sql = "INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, " +
                "quiz_weight, assignment_weight, mid_theory_weight, mid_practical_weight, ca_weight, " +
                "end_theory_weight, end_practical_weight, department_id, academic_year, semester, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");
            pstmt.setBigDecimal(4, c.getTheoryCredits() != null ? c.getTheoryCredits() : BigDecimal.ZERO);
            pstmt.setBigDecimal(5, c.getPracticalCredits() != null ? c.getPracticalCredits() : BigDecimal.ZERO);

            pstmt.setBigDecimal(6, c.getQuizWeight() != null ? c.getQuizWeight() : BigDecimal.ZERO);
            pstmt.setBigDecimal(7, c.getAssignmentWeight() != null ? c.getAssignmentWeight() : BigDecimal.ZERO);
            pstmt.setBigDecimal(8, c.getMidTheoryWeight() != null ? c.getMidTheoryWeight() : BigDecimal.ZERO);
            pstmt.setBigDecimal(9, c.getMidPracticalWeight() != null ? c.getMidPracticalWeight() : BigDecimal.ZERO);
            pstmt.setBigDecimal(10, c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("30.00"));

            pstmt.setBigDecimal(11, c.getEndTheoryWeight() != null ? c.getEndTheoryWeight() : BigDecimal.ZERO);
            pstmt.setBigDecimal(12, c.getEndPracticalWeight() != null ? c.getEndPracticalWeight() : BigDecimal.ZERO);

            pstmt.setInt(13, c.getDepartmentId() > 0 ? c.getDepartmentId() : 1);
            pstmt.setString(14, c.getAcademicYear() != null ? c.getAcademicYear() : "2024/2025");
            pstmt.setString(15, c.getSemester() != null ? c.getSemester() : "Semester I");
            pstmt.setString(16, c.getStatus() != null ? c.getStatus() : "Active");

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
        }
        return false;
    }

    private void createDefaultAssessmentsForCourse(Connection con, Course c) {
        if (c.getCourseId() <= 0) return;

        String sql = "INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark) " +
                "VALUES (?, ?, ?, ?, ?, ?, 100.00)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            // Provision Quiz 1, Quiz 2, and Quiz 3 if Quiz Weight > 0
            if (c.getQuizWeight() != null && c.getQuizWeight().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal quizEach = c.getQuizWeight().divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP);
                insertAssessmentBatch(pstmt, c.getCourseId(), "Quiz 1", "CA", "Quiz1", "Theory", quizEach);
                insertAssessmentBatch(pstmt, c.getCourseId(), "Quiz 2", "CA", "Quiz2", "Theory", quizEach);
                insertAssessmentBatch(pstmt, c.getCourseId(), "Quiz 3", "CA", "Quiz3", "Theory", quizEach);
            }
            // Provision Assignment if Assignment Weight > 0
            if (c.getAssignmentWeight() != null && c.getAssignmentWeight().compareTo(BigDecimal.ZERO) > 0) {
                insertAssessmentBatch(pstmt, c.getCourseId(), "Assignment 1", "CA", "Assignment", "Theory", c.getAssignmentWeight());
            }
            // Provision Mid Exam Theory if Weight > 0
            if (c.getMidTheoryWeight() != null && c.getMidTheoryWeight().compareTo(BigDecimal.ZERO) > 0) {
                insertAssessmentBatch(pstmt, c.getCourseId(), "Mid Exam Theory", "CA", "Mid Exam Theory", "Theory", c.getMidTheoryWeight());
            }
            // Provision Mid Exam Practical if Weight > 0
            if (c.getMidPracticalWeight() != null && c.getMidPracticalWeight().compareTo(BigDecimal.ZERO) > 0) {
                insertAssessmentBatch(pstmt, c.getCourseId(), "Mid Exam Practical", "CA", "Mid Exam Practical", "Practical", c.getMidPracticalWeight());
            }
            // Provision End Exam Theory if Weight > 0
            if (c.getEndTheoryWeight() != null && c.getEndTheoryWeight().compareTo(BigDecimal.ZERO) > 0) {
                insertAssessmentBatch(pstmt, c.getCourseId(), "End Semester Theory Exam", "FINAL", "End Exam Theory", "Theory", c.getEndTheoryWeight());
            }
            // Provision End Exam Practical if Weight > 0
            if (c.getEndPracticalWeight() != null && c.getEndPracticalWeight().compareTo(BigDecimal.ZERO) > 0) {
                insertAssessmentBatch(pstmt, c.getCourseId(), "End Semester Practical Exam", "FINAL", "End Exam Practical", "Practical", c.getEndPracticalWeight());
            }
        } catch (Exception ignored) {
        }
    }

    private void insertAssessmentBatch(PreparedStatement pstmt, int courseId, String name, String category, String type, String component, BigDecimal weight) throws SQLException {
        pstmt.setInt(1, courseId);
        pstmt.setString(2, name);
        pstmt.setString(3, category);
        pstmt.setString(4, type);
        pstmt.setString(5, component);
        pstmt.setBigDecimal(6, weight);
        pstmt.executeUpdate();
    }

    public boolean updateCourse(Course c) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, theory_credits = ?, practical_credits = ?, " +
                "quiz_weight = ?, assignment_weight = ?, mid_theory_weight = ?, mid_practical_weight = ?, ca_weight = ?, " +
                "end_theory_weight = ?, end_practical_weight = ?, department_id = ?, academic_year = ?, semester = ?, status = ? " +
                "WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setString(3, c.getDescription());
            pstmt.setBigDecimal(4, c.getTheoryCredits());
            pstmt.setBigDecimal(5, c.getPracticalCredits());

            pstmt.setBigDecimal(6, c.getQuizWeight());
            pstmt.setBigDecimal(7, c.getAssignmentWeight());
            pstmt.setBigDecimal(8, c.getMidTheoryWeight());
            pstmt.setBigDecimal(9, c.getMidPracticalWeight());
            pstmt.setBigDecimal(10, c.getCaWeight());

            pstmt.setBigDecimal(11, c.getEndTheoryWeight());
            pstmt.setBigDecimal(12, c.getEndPracticalWeight());

            pstmt.setInt(13, c.getDepartmentId());
            pstmt.setString(14, c.getAcademicYear());
            pstmt.setString(15, c.getSemester());
            pstmt.setString(16, c.getStatus());
            pstmt.setInt(17, c.getCourseId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
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

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean removeLecturerFromCourse(int courseId, long lecturerId) throws SQLException {
        String sql = "DELETE FROM course_lecturers WHERE course_id = ? AND lecturer_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setLong(2, lecturerId);

            return pstmt.executeUpdate() > 0;
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

        c.setQuizWeight(rs.getBigDecimal("quiz_weight"));
        c.setAssignmentWeight(rs.getBigDecimal("assignment_weight"));
        c.setMidTheoryWeight(rs.getBigDecimal("mid_theory_weight"));
        c.setMidPracticalWeight(rs.getBigDecimal("mid_practical_weight"));
        c.setCaWeight(rs.getBigDecimal("ca_weight"));

        c.setEndTheoryWeight(rs.getBigDecimal("end_theory_weight"));
        c.setEndPracticalWeight(rs.getBigDecimal("end_practical_weight"));
        c.setFinalWeight(rs.getBigDecimal("final_weight"));

        c.setDepartmentId(rs.getInt("department_id"));
        c.setAcademicYear(rs.getString("academic_year"));
        c.setSemester(rs.getString("semester"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}