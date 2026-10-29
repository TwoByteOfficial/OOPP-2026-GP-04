package dao.admin;

import util.DBConnection;
import model.Course;
import model.CourseLecturer;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return createCourse(c, null, null, null, null, null, null);
    }

    public boolean createCourse(Course c, BigDecimal quizWeight, BigDecimal assignWeight,
                                BigDecimal midTheoryWeight, BigDecimal midPracticalWeight,
                                BigDecimal endTheoryWeight, BigDecimal endPracticalWeight) throws SQLException {
        String sql1 = "INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, ca_weight, department_id, academic_year, semester, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");
            pstmt.setBigDecimal(4, c.getTheoryCredits() != null ? c.getTheoryCredits() : BigDecimal.ZERO);
            pstmt.setBigDecimal(5, c.getPracticalCredits() != null ? c.getPracticalCredits() : BigDecimal.ZERO);
            pstmt.setBigDecimal(6, c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("40.00"));
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
                saveAssessmentWeights(con, c.getCourseId(), quizWeight, assignWeight, midTheoryWeight, midPracticalWeight, endTheoryWeight, endPracticalWeight);
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
                pstmt.setBigDecimal(7, c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("40.00"));
                BigDecimal finalW = (c.getFinalWeight() != null) ? c.getFinalWeight() : (new BigDecimal("100.00").subtract(c.getCaWeight() != null ? c.getCaWeight() : new BigDecimal("40.00")));
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
                    saveAssessmentWeights(con, c.getCourseId(), quizWeight, assignWeight, midTheoryWeight, midPracticalWeight, endTheoryWeight, endPracticalWeight);
                    return true;
                }
            } catch (SQLException ex2) {
                throw ex;
            }
        }
        return false;
    }

    public boolean updateCourse(Course c) throws SQLException {
        return updateCourse(c, null, null, null, null, null, null);
    }

    public boolean updateCourse(Course c, BigDecimal quizWeight, BigDecimal assignWeight,
                                BigDecimal midTheoryWeight, BigDecimal midPracticalWeight,
                                BigDecimal endTheoryWeight, BigDecimal endPracticalWeight) throws SQLException {
        String sql1 = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, theory_credits = ?, " +
                "practical_credits = ?, ca_weight = ?, department_id = ?, academic_year = ?, semester = ?, status = ? " +
                "WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql1)) {

            pstmt.setString(1, c.getCourseCode());
            pstmt.setString(2, c.getCourseName());
            pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");
            pstmt.setBigDecimal(4, c.getTheoryCredits());
            pstmt.setBigDecimal(5, c.getPracticalCredits());
            pstmt.setBigDecimal(6, c.getCaWeight());
            pstmt.setInt(7, c.getDepartmentId());
            pstmt.setString(8, c.getAcademicYear());
            pstmt.setString(9, c.getSemester());
            pstmt.setString(10, c.getStatus());
            pstmt.setInt(11, c.getCourseId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                saveAssessmentWeights(con, c.getCourseId(), quizWeight, assignWeight, midTheoryWeight, midPracticalWeight, endTheoryWeight, endPracticalWeight);
                return true;
            }
        } catch (SQLException ex) {
            String sql2 = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, theory_credits = ?, " +
                    "practical_credits = ?, total_credits = ?, ca_weight = ?, final_weight = ?, department_id = ?, academic_year = ?, semester = ?, status = ? " +
                    "WHERE course_id = ?";

            try (Connection con = DBConnection.con();
                 PreparedStatement pstmt = con.prepareStatement(sql2)) {

                pstmt.setString(1, c.getCourseCode());
                pstmt.setString(2, c.getCourseName());
                pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");
                pstmt.setBigDecimal(4, c.getTheoryCredits());
                pstmt.setBigDecimal(5, c.getPracticalCredits());
                BigDecimal total = (c.getTotalCredits() != null) ? c.getTotalCredits() : (c.getTheoryCredits().add(c.getPracticalCredits()));
                pstmt.setBigDecimal(6, total);
                pstmt.setBigDecimal(7, c.getCaWeight());
                pstmt.setBigDecimal(8, c.getFinalWeight());
                pstmt.setInt(9, c.getDepartmentId());
                pstmt.setString(10, c.getAcademicYear());
                pstmt.setString(11, c.getSemester());
                pstmt.setString(12, c.getStatus());
                pstmt.setInt(13, c.getCourseId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    saveAssessmentWeights(con, c.getCourseId(), quizWeight, assignWeight, midTheoryWeight, midPracticalWeight, endTheoryWeight, endPracticalWeight);
                    return true;
                }
            } catch (SQLException ex2) {
                throw ex;
            }
        }
        return false;
    }

    private void saveAssessmentWeights(Connection con, int courseId, BigDecimal quizWeight, BigDecimal assignWeight,
                                       BigDecimal midTheoryWeight, BigDecimal midPracticalWeight,
                                       BigDecimal endTheoryWeight, BigDecimal endPracticalWeight) {
        if (courseId <= 0) return;

        // Default values if any weight parameter is null
        BigDecimal qW = (quizWeight != null) ? quizWeight : new BigDecimal("10.00");
        BigDecimal aW = (assignWeight != null) ? assignWeight : new BigDecimal("10.00");
        BigDecimal mtW = (midTheoryWeight != null) ? midTheoryWeight : new BigDecimal("10.00");
        BigDecimal mpW = (midPracticalWeight != null) ? midPracticalWeight : new BigDecimal("10.00");
        BigDecimal etW = (endTheoryWeight != null) ? endTheoryWeight : new BigDecimal("40.00");
        BigDecimal epW = (endPracticalWeight != null) ? endPracticalWeight : new BigDecimal("20.00");

        try {
            // Clear existing assessment records for this course to ensure clean update
            String delSql = "DELETE FROM assessments WHERE course_id = ?";
            try (PreparedStatement delPstmt = con.prepareStatement(delSql)) {
                delPstmt.setInt(1, courseId);
                delPstmt.executeUpdate();
            }

            String insSql = "INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark) VALUES " +
                    "(?, 'Highest 2 Quizzes', 'CA', 'Quiz1', 'Theory', ?, 100.00), " +
                    "(?, 'Assignment', 'CA', 'Assignment', 'Theory', ?, 100.00), " +
                    "(?, 'Mid Exam Theory', 'CA', 'Mid Exam Theory', 'Theory', ?, 100.00), " +
                    "(?, 'Mid Exam Practical', 'CA', 'Mid Exam Practical', 'Practical', ?, 100.00), " +
                    "(?, 'End Exam Theory', 'FINAL', 'End Exam Theory', 'Theory', ?, 100.00), " +
                    "(?, 'End Exam Practical', 'FINAL', 'End Exam Practical', 'Practical', ?, 100.00)";

            try (PreparedStatement insPstmt = con.prepareStatement(insSql)) {
                insPstmt.setInt(1, courseId); insPstmt.setBigDecimal(2, qW);
                insPstmt.setInt(3, courseId); insPstmt.setBigDecimal(4, aW);
                insPstmt.setInt(5, courseId); insPstmt.setBigDecimal(6, mtW);
                insPstmt.setInt(7, courseId); insPstmt.setBigDecimal(8, mpW);
                insPstmt.setInt(9, courseId); insPstmt.setBigDecimal(10, etW);
                insPstmt.setInt(11, courseId); insPstmt.setBigDecimal(12, epW);

                insPstmt.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("[AdminCourseDAO] Note on saving assessment weights: " + e.getMessage());
        }
    }

    public Map<String, BigDecimal> getCourseAssessmentWeights(int courseId) {
        Map<String, BigDecimal> weights = new HashMap<>();

        // Defaults
        weights.put("quiz", new BigDecimal("10.00"));
        weights.put("assignment", new BigDecimal("10.00"));
        weights.put("midTheory", new BigDecimal("10.00"));
        weights.put("midPractical", new BigDecimal("10.00"));
        weights.put("endTheory", new BigDecimal("40.00"));
        weights.put("endPractical", new BigDecimal("20.00"));

        String sql = "SELECT assessment_name, assessment_type, weight_percent FROM assessments WHERE course_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("assessment_name") != null ? rs.getString("assessment_name").toLowerCase() : "";
                    String type = rs.getString("assessment_type") != null ? rs.getString("assessment_type").toLowerCase() : "";
                    BigDecimal weight = rs.getBigDecimal("weight_percent");

                    if (weight == null) continue;

                    if (type.contains("quiz") || name.contains("quiz")) {
                        weights.put("quiz", weight);
                    } else if (type.contains("assignment") || name.contains("assignment")) {
                        weights.put("assignment", weight);
                    } else if (type.contains("mid exam theory") || name.contains("mid exam theory")) {
                        weights.put("midTheory", weight);
                    } else if (type.contains("mid exam practical") || name.contains("mid exam practical")) {
                        weights.put("midPractical", weight);
                    } else if (type.contains("end exam theory") || name.contains("end exam theory")) {
                        weights.put("endTheory", weight);
                    } else if (type.contains("end exam practical") || name.contains("end exam practical")) {
                        weights.put("endPractical", weight);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AdminCourseDAO] Note on fetching assessment weights: " + e.getMessage());
        }

        return weights;
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