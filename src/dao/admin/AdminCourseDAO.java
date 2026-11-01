package dao.admin;

import util.DBConnection;
import model.Course;
import model.CourseLecturer;

import java.sql.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AdminCourseDAO {

    // =========================================================
    // GET ALL COURSES
    // =========================================================
    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";

        try (Statement stmt = DBConnection.ADMIN_CON.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
        }

        return list;
    }

    // =========================================================
    // GET COURSE BY ID
    // =========================================================
    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        }

        return null;
    }

    // =========================================================
    // GET COURSES BY DEPARTMENT
    // =========================================================
    public List<Course> getCoursesByDepartment(int departmentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE department_id = ? ORDER BY course_code";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCourse(rs));
                }
            }
        }

        return list;
    }

    // =========================================================
    // CREATE COURSE
    // Creates the course and its default assessment definitions
    // in one transaction.
    // =========================================================
    public boolean createCourse(Course c) throws SQLException {

        String sql = "INSERT INTO courses (" +
                "course_code, course_name, description, theory_credits, practical_credits, " +
                "quiz_weight, assignment_weight, mid_theory_weight, mid_practical_weight, ca_weight, " +
                "end_theory_weight, end_practical_weight, department_id, academic_year, semester, status" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DBConnection.ADMIN_CON.setAutoCommit(false);

        try {
            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, c.getCourseCode());
                pstmt.setString(2, c.getCourseName());
                pstmt.setString(3, c.getDescription() != null ? c.getDescription() : "");

                pstmt.setBigDecimal(4, valueOrZero(c.getTheoryCredits()));
                pstmt.setBigDecimal(5, valueOrZero(c.getPracticalCredits()));

                pstmt.setBigDecimal(6, valueOrZero(c.getQuizWeight()));
                pstmt.setBigDecimal(7, valueOrZero(c.getAssignmentWeight()));
                pstmt.setBigDecimal(8, valueOrZero(c.getMidTheoryWeight()));
                pstmt.setBigDecimal(9, valueOrZero(c.getMidPracticalWeight()));
                pstmt.setBigDecimal(10, valueOrZero(c.getCaWeight()));

                pstmt.setBigDecimal(11, valueOrZero(c.getEndTheoryWeight()));
                pstmt.setBigDecimal(12, valueOrZero(c.getEndPracticalWeight()));

                pstmt.setInt(13, c.getDepartmentId() > 0 ? c.getDepartmentId() : 1);
                pstmt.setString(14, c.getAcademicYear() != null
                        ? c.getAcademicYear() : "2024/2025");
                pstmt.setString(15, c.getSemester() != null
                        ? c.getSemester() : "Semester I");
                pstmt.setString(16, c.getStatus() != null
                        ? c.getStatus() : "Active");

                int rows = pstmt.executeUpdate();

                if (rows == 0) {
                    DBConnection.ADMIN_CON.rollback();
                    return false;
                }

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Course ID was not generated.");
                    }

                    c.setCourseId(rs.getInt(1));
                }
            }

            // Automatically create applicable assessment definitions.
            synchronizeAssessmentDefinitions(c);

            DBConnection.ADMIN_CON.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            DBConnection.ADMIN_CON.rollback();
            throw e;
        } finally {
            DBConnection.ADMIN_CON.setAutoCommit(true);
        }
    }

    // =========================================================
    // UPDATE COURSE
    // Updates course details and synchronizes assessment
    // definitions in the SAME transaction.
    // =========================================================
    public boolean updateCourse(Course c) throws SQLException {

        String sql = "UPDATE courses SET " +
                "course_code = ?, course_name = ?, description = ?, " +
                "theory_credits = ?, practical_credits = ?, " +
                "quiz_weight = ?, assignment_weight = ?, " +
                "mid_theory_weight = ?, mid_practical_weight = ?, ca_weight = ?, " +
                "end_theory_weight = ?, end_practical_weight = ?, " +
                "department_id = ?, academic_year = ?, semester = ?, status = ? " +
                "WHERE course_id = ?";

        DBConnection.ADMIN_CON.setAutoCommit(false);

        try {
            int rowsAffected;

            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

                pstmt.setString(1, c.getCourseCode());
                pstmt.setString(2, c.getCourseName());
                pstmt.setString(3, c.getDescription() != null
                        ? c.getDescription() : "");

                pstmt.setBigDecimal(4, valueOrZero(c.getTheoryCredits()));
                pstmt.setBigDecimal(5, valueOrZero(c.getPracticalCredits()));

                pstmt.setBigDecimal(6, valueOrZero(c.getQuizWeight()));
                pstmt.setBigDecimal(7, valueOrZero(c.getAssignmentWeight()));
                pstmt.setBigDecimal(8, valueOrZero(c.getMidTheoryWeight()));
                pstmt.setBigDecimal(9, valueOrZero(c.getMidPracticalWeight()));
                pstmt.setBigDecimal(10, valueOrZero(c.getCaWeight()));

                pstmt.setBigDecimal(11, valueOrZero(c.getEndTheoryWeight()));
                pstmt.setBigDecimal(12, valueOrZero(c.getEndPracticalWeight()));

                pstmt.setInt(13, c.getDepartmentId());
                pstmt.setString(14, c.getAcademicYear());
                pstmt.setString(15, c.getSemester());
                pstmt.setString(16, c.getStatus());
                pstmt.setInt(17, c.getCourseId());

                rowsAffected = pstmt.executeUpdate();
            }

            if (rowsAffected == 0) {
                // Check whether the course exists. MySQL can report
                // zero affected rows when values have not changed.
                if (!courseExists(c.getCourseId())) {
                    DBConnection.ADMIN_CON.rollback();
                    return false;
                }
            }

            // Synchronize assessment definitions with the new weights.
            synchronizeAssessmentDefinitions(c);

            DBConnection.ADMIN_CON.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            DBConnection.ADMIN_CON.rollback();
            throw e;
        } finally {
            DBConnection.ADMIN_CON.setAutoCommit(true);
        }
    }

    // =========================================================
    // CHECK COURSE EXISTS
    // =========================================================
    private boolean courseExists(int courseId) throws SQLException {

        String sql = "SELECT 1 FROM courses WHERE course_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =========================================================
    // SYNCHRONIZE ASSESSMENT DEFINITIONS
    //
    // Existing definitions:
    //   Update their category, component and configured weight.
    //
    // Missing definitions:
    //   Create them only when their component weight is positive.
    //
    // Zero-weight definitions:
    //   Retain existing records and set their weight to zero.
    //
    // Quiz 1/2/3:
    //   Always use weight_percent = 0.
    //   The best-two calculation uses courses.quiz_weight.
    // =========================================================
    private void synchronizeAssessmentDefinitions(Course c) throws SQLException {

        if (c.getCourseId() <= 0) {
            throw new SQLException("Invalid course ID for assessment synchronization.");
        }

        BigDecimal quizWeight = valueOrZero(c.getQuizWeight());
        BigDecimal assignmentWeight = valueOrZero(c.getAssignmentWeight());
        BigDecimal midTheoryWeight = valueOrZero(c.getMidTheoryWeight());
        BigDecimal midPracticalWeight = valueOrZero(c.getMidPracticalWeight());
        BigDecimal endTheoryWeight = valueOrZero(c.getEndTheoryWeight());
        BigDecimal endPracticalWeight = valueOrZero(c.getEndPracticalWeight());

        // Quiz group: create all three only if quiz weight is positive.
        synchronizeOneAssessment(
                c.getCourseId(),
                "Quiz 1", "CA", "Quiz1", "Theory",
                BigDecimal.ZERO, quizWeight.signum() > 0
        );

        synchronizeOneAssessment(
                c.getCourseId(),
                "Quiz 2", "CA", "Quiz2", "Theory",
                BigDecimal.ZERO, quizWeight.signum() > 0
        );

        synchronizeOneAssessment(
                c.getCourseId(),
                "Quiz 3", "CA", "Quiz3", "Theory",
                BigDecimal.ZERO, quizWeight.signum() > 0
        );

        // Assignment
        synchronizeOneAssessment(
                c.getCourseId(),
                "Assignment 1", "CA", "Assignment", "Theory",
                assignmentWeight, assignmentWeight.signum() > 0
        );

        // Mid Exam Theory
        synchronizeOneAssessment(
                c.getCourseId(),
                "Mid Exam Theory", "CA", "Mid Exam Theory", "Theory",
                midTheoryWeight, midTheoryWeight.signum() > 0
        );

        // Mid Exam Practical
        synchronizeOneAssessment(
                c.getCourseId(),
                "Mid Exam Practical", "CA", "Mid Exam Practical", "Practical",
                midPracticalWeight, midPracticalWeight.signum() > 0
        );

        // End Exam Theory
        synchronizeOneAssessment(
                c.getCourseId(),
                "End Semester Theory Exam", "FINAL",
                "End Exam Theory", "Theory",
                endTheoryWeight, endTheoryWeight.signum() > 0
        );

        // End Exam Practical
        synchronizeOneAssessment(
                c.getCourseId(),
                "End Semester Practical Exam", "FINAL",
                "End Exam Practical", "Practical",
                endPracticalWeight, endPracticalWeight.signum() > 0
        );
    }

    // =========================================================
    // SYNCHRONIZE ONE ASSESSMENT TYPE
    // =========================================================
    private void synchronizeOneAssessment(
            int courseId,
            String assessmentName,
            String category,
            String type,
            String component,
            BigDecimal weight,
            boolean createIfMissing) throws SQLException {

        String countSql =
                "SELECT COUNT(*) FROM assessments " +
                        "WHERE course_id = ? AND assessment_type = ?";

        int existingCount = 0;

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(countSql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    existingCount = rs.getInt(1);
                }
            }
        }

        if (existingCount > 0) {

            // Update all matching records without deleting any records.
            // Existing assessment names and student marks are preserved.
            String updateSql =
                    "UPDATE assessments SET " +
                            "assessment_category = ?, " +
                            "component = ?, " +
                            "weight_percent = ? " +
                            "WHERE course_id = ? AND assessment_type = ?";

            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(updateSql)) {
                pstmt.setString(1, category);
                pstmt.setString(2, component);
                pstmt.setBigDecimal(3, weight);
                pstmt.setInt(4, courseId);
                pstmt.setString(5, type);

                pstmt.executeUpdate();
            }

        } else if (createIfMissing) {

            // Create a new definition only when its component is enabled.
            String insertSql =
                    "INSERT INTO assessments (" +
                            "course_id, assessment_name, assessment_category, " +
                            "assessment_type, component, weight_percent, max_mark" +
                            ") VALUES (?, ?, ?, ?, ?, ?, 100.00)";

            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(insertSql)) {
                pstmt.setInt(1, courseId);
                pstmt.setString(2, assessmentName);
                pstmt.setString(3, category);
                pstmt.setString(4, type);
                pstmt.setString(5, component);
                pstmt.setBigDecimal(6, weight);

                pstmt.executeUpdate();
            }
        }
    }

    // =========================================================
    // DELETE COURSE
    // Existing method retained.
    // =========================================================
    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================
    // ASSIGN LECTURER TO COURSE
    // =========================================================
    public boolean assignLecturerToCourse(
            int courseId, long lecturerId, String responsibility) throws SQLException {

        String sql =
                "INSERT INTO course_lecturers " +
                        "(course_id, lecturer_id, responsibility) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE responsibility = VALUES(responsibility)";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setLong(2, lecturerId);
            pstmt.setString(3,
                    responsibility != null ? responsibility : "In-Charge");

            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================
    // REMOVE LECTURER FROM COURSE
    // =========================================================
    public boolean removeLecturerFromCourse(
            int courseId, long lecturerId) throws SQLException {

        String sql =
                "DELETE FROM course_lecturers " +
                        "WHERE course_id = ? AND lecturer_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setLong(2, lecturerId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================
    // GET COURSE LECTURERS
    // =========================================================
    public List<CourseLecturer> getCourseLecturers(int courseId) throws SQLException {

        List<CourseLecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM course_lecturers WHERE course_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {

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

    // =========================================================
    // MAP RESULT SET TO COURSE
    // =========================================================
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

    // =========================================================
    // NULL-SAFE BIG DECIMAL
    // =========================================================
    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}