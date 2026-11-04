package dao.undergraduate;

import util.DBConnection;
import gui.undergraduate.enrollment.model.EnrollmentCourseData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object managing course enrollment queries and request submissions.
 * Directly integrates with UniMis database schema using DBConnection.UG_CON.
 *
 * Responsibilities:
 * - Resolves student identity mapping from the logged-in user account.
 * - Retrieves curriculum courses with calculated live enrollment states (ENROLLED, PENDING, AVAILABLE).
 * - Submits student enrollment requests for administrative review, featuring schema fallback resilience.
 */
public class UndergraduateEnrollmentDAO {

    /**
     * Resolves the student ID directly from the authenticated user ID.
     * In the UniMis identity model, student accounts share their identifier with the primary user record.
     *
     * @param userId The unique user ID of the logged-in student
     * @return The resolved student primary key identifier
     */
    public long resolveStudentId(long userId) {
        return userId;
    }

    /**
     * Fetches all department courses along with student-specific status: ENROLLED, PENDING, or AVAILABLE.
     * Evaluates active status via SQL CASE logic by joining against existing confirmed enrollments
     * and pending enrollment requests.
     *
     * @param userId The unique user identifier of the undergraduate student
     * @return List of {@link EnrollmentCourseData} items populated with module credits, weights, and status
     */
    public List<EnrollmentCourseData> getAvailableCoursesForStudent(long userId) {
        List<EnrollmentCourseData> list = new ArrayList<>();

        // SQL query evaluating course availability and enrollment condition for the specified student
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.total_credits, " +
                "       c.ca_weight, c.final_weight, " +
                "       CASE " +
                "           WHEN e.course_id IS NOT NULL THEN 'ENROLLED' " +
                "           WHEN er.course_id IS NOT NULL THEN 'PENDING' " +
                "           ELSE 'AVAILABLE' " +
                "       END AS enrollment_status " +
                "FROM courses c " +
                "LEFT JOIN enrollments e ON c.course_id = e.course_id AND e.student_id = ? " +
                "LEFT JOIN enrollment_requests er ON c.course_id = er.course_id AND er.student_id = ? AND er.status = 'Pending' " +
                "ORDER BY c.course_code ASC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            // Bind student ID to both LEFT JOIN conditions
            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
                    String courseCode = rs.getString("course_code");
                    String courseName = rs.getString("course_name");
                    double totalCredits = rs.getDouble("total_credits");
                    double caWeight = rs.getDouble("ca_weight");
                    double finalWeight = rs.getDouble("final_weight");
                    String status = rs.getString("enrollment_status");

                    // Instantiate data model item with academic semester metadata and weight specifications
                    list.add(new EnrollmentCourseData(
                            courseId,
                            courseCode,
                            courseName,
                            "",
                            totalCredits,
                            totalCredits,
                            0.0,
                            true,
                            caWeight,
                            finalWeight,
                            "Semester I",
                            "2026/2027",
                            status,
                            ""
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts a new pending enrollment request for admin review.
     * Attempts the full schema insertion with academic year and semester details first.
     * If the target database lacks extended columns, automatically falls back to basic insertion.
     *
     * @param userId   The authenticated undergraduate's user ID
     * @param courseId The primary key ID of the course requested
     * @param semester Academic semester label (e.g., "Semester I")
     * @param note     Optional remarks or special student justification
     * @return true if the enrollment request record was successfully inserted, false otherwise
     */
    public boolean requestEnrollment(long userId, int courseId, String semester, String note) {
        // Primary SQL insertion matching full schema definition
        String sql = "INSERT INTO enrollment_requests (student_id, course_id, academic_year, semester, enrollment_type, request_note, status) " +
                "VALUES (?, ?, '2026/2027', ?, 'Normal', ?, 'Pending')";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, semester);
            ps.setString(4, note);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Fallback insertion for simplified or legacy database table structures
            String fallbackSql = "INSERT INTO enrollment_requests (student_id, course_id, status) VALUES (?, ?, 'Pending')";
            try (PreparedStatement ps2 = DBConnection.UG_CON.prepareStatement(fallbackSql)) {
                ps2.setLong(1, userId);
                ps2.setInt(2, courseId);
                return ps2.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}