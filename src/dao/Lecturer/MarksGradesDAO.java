package dao.Lecturer;

import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksGradesDAO {

    // Load courses assigned to this lecturer.
    public List<Object[]> getAssignedCourses(long lecturerUserId)
            throws SQLException {

        List<Object[]> courses = new ArrayList<>();

        String sql =
                "SELECT c.course_id, c.course_code, c.course_name " +
                        "FROM courses c " +
                        "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                        "WHERE cl.lecturer_id = ? " +
                        "AND c.status = 'Active' " +
                        "ORDER BY c.course_code";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, lecturerUserId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    courses.add(new Object[]{
                            result.getInt("course_id"),
                            result.getString("course_code"),
                            result.getString("course_name")
                    });
                }
            }
        }

        return courses;
    }

    // Load assessments only for courses assigned to this lecturer.
    public List<Object[]> getAssessments(
            long lecturerUserId,
            int courseId) throws SQLException {

        List<Object[]> assessments = new ArrayList<>();

        String sql =
                "SELECT a.assessment_id, a.assessment_name, " +
                        "a.assessment_category, a.assessment_type, " +
                        "a.max_mark " +
                        "FROM assessments a " +
                        "WHERE a.course_id = ? " +
                        "AND EXISTS (" +
                        "    SELECT 1 FROM course_lecturers cl " +
                        "    WHERE cl.course_id = a.course_id " +
                        "    AND cl.lecturer_id = ?" +
                        ") " +
                        "ORDER BY a.assessment_date, a.assessment_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.setLong(2, lecturerUserId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    assessments.add(new Object[]{
                            result.getLong("assessment_id"),
                            result.getString("assessment_name"),
                            result.getString("assessment_category"),
                            result.getString("assessment_type"),
                            result.getBigDecimal("max_mark")
                    });
                }
            }
        }

        return assessments;
    }

    // Load enrolled students and existing marks.
    public List<Object[]> getStudentsForAssessment(
            long lecturerUserId,
            int courseId,
            long assessmentId) throws SQLException {

        List<Object[]> students = new ArrayList<>();

        String sql =
                "SELECT u.user_id, ug.registration_no, u.full_name, " +
                        "m.mark " +
                        "FROM enrollments e " +
                        "JOIN undergraduates ug ON e.student_id = ug.user_id " +
                        "JOIN users u ON ug.user_id = u.user_id " +
                        "JOIN assessments a ON a.course_id = e.course_id " +
                        "LEFT JOIN marks m " +
                        "ON m.assessment_id = a.assessment_id " +
                        "AND m.student_id = e.student_id " +
                        "WHERE e.course_id = ? " +
                        "AND a.assessment_id = ? " +
                        "AND e.enrollment_status = 'Enrolled' " +
                        "AND EXISTS (" +
                        "    SELECT 1 FROM course_lecturers cl " +
                        "    WHERE cl.course_id = e.course_id " +
                        "    AND cl.lecturer_id = ?" +
                        ") " +
                        "ORDER BY ug.registration_no";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.setLong(2, assessmentId);
            statement.setLong(3, lecturerUserId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {

                    Object mark = result.getObject("mark");

                    students.add(new Object[]{
                            result.getLong("user_id"),
                            result.getString("registration_no"),
                            result.getString("full_name"),
                            mark,
                            mark == null ? "Not entered" : "Saved"
                    });
                }
            }
        }

        return students;
    }

    // Save a new mark or update an existing mark.
    public void saveMark(
            long lecturerUserId,
            int courseId,
            long assessmentId,
            long studentId,
            BigDecimal mark) throws SQLException {

        if (mark == null) {
            throw new IllegalArgumentException(
                    "Please enter a mark before saving."
            );
        }

        if (mark.compareTo(BigDecimal.ZERO) < 0 ||
                mark.compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "Mark must be between 0 and 100."
            );
        }

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Verify lecturer assignment and obtain the assessment maximum.
                String assessmentSql =
                        "SELECT a.max_mark " +
                                "FROM assessments a " +
                                "WHERE a.assessment_id = ? " +
                                "AND a.course_id = ? " +
                                "AND EXISTS (" +
                                "    SELECT 1 FROM course_lecturers cl " +
                                "    WHERE cl.course_id = a.course_id " +
                                "    AND cl.lecturer_id = ?" +
                                ") " +
                                "FOR UPDATE";

                BigDecimal maxMark;

                try (PreparedStatement statement =
                             connection.prepareStatement(assessmentSql)) {

                    statement.setLong(1, assessmentId);
                    statement.setInt(2, courseId);
                    statement.setLong(3, lecturerUserId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException(
                                    "Assessment not found or you are not " +
                                            "assigned to this course."
                            );
                        }

                        maxMark = result.getBigDecimal("max_mark");
                    }
                }

                if (maxMark == null ||
                        maxMark.compareTo(BigDecimal.ZERO) <= 0) {

                    throw new SQLException(
                            "The assessment maximum mark is invalid."
                    );
                }

                if (mark.compareTo(maxMark) > 0) {
                    throw new IllegalArgumentException(
                            "Mark cannot exceed the assessment maximum of " +
                                    maxMark.toPlainString() + "."
                    );
                }

                // Verify that the student is actively enrolled.
                String enrollmentSql =
                        "SELECT 1 " +
                                "FROM enrollments " +
                                "WHERE course_id = ? " +
                                "AND student_id = ? " +
                                "AND enrollment_status = 'Enrolled' " +
                                "LIMIT 1";

                try (PreparedStatement statement =
                             connection.prepareStatement(enrollmentSql)) {

                    statement.setInt(1, courseId);
                    statement.setLong(2, studentId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException(
                                    "The student is not actively enrolled " +
                                            "in this course."
                            );
                        }
                    }
                }

                // Insert the mark, or update it if it already exists.
                String saveSql =
                        "INSERT INTO marks " +
                                "(assessment_id, student_id, mark, entered_by) " +
                                "VALUES (?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "mark = VALUES(mark), " +
                                "entered_by = VALUES(entered_by), " +
                                "updated_at = CURRENT_TIMESTAMP";

                try (PreparedStatement statement =
                             connection.prepareStatement(saveSql)) {

                    statement.setLong(1, assessmentId);
                    statement.setLong(2, studentId);
                    statement.setBigDecimal(3, mark);
                    statement.setLong(4, lecturerUserId);

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
