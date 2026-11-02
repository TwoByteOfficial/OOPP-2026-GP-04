package dao.admin;

import util.DBConnection;
import model.EnrollmentRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminEnrollDAO - Data Access Object for handling course enrollment requests
 * and automatic synchronization with the enrollments table upon admin approval.
 * Follows identical architecture and transaction patterns as AdminCourseDAO and AdminUserDAO.
 */
public class AdminEnrollDAO {

    // =========================================================
    // GET ALL ENROLLMENT REQUESTS
    // =========================================================
    public List<EnrollmentRequest> getAllEnrollmentRequests() throws SQLException {
        List<EnrollmentRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM v_enrollment_requests ORDER BY requested_at DESC";

        try (Statement stmt = DBConnection.ADMIN_CON.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToEnrollmentRequest(rs));
            }
        }

        return list;
    }

    // =========================================================
    // GET ENROLLMENT REQUEST BY ID
    // =========================================================
    public EnrollmentRequest getEnrollmentRequestById(long requestId) throws SQLException {
        String sql = "SELECT * FROM v_enrollment_requests WHERE request_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setLong(1, requestId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnrollmentRequest(rs);
                }
            }
        }

        return null;
    }

    // =========================================================
    // GET ENROLLMENT REQUESTS BY STATUS
    // =========================================================
    public List<EnrollmentRequest> getEnrollmentRequestsByStatus(String status) throws SQLException {
        List<EnrollmentRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM v_enrollment_requests WHERE status = ? ORDER BY requested_at DESC";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEnrollmentRequest(rs));
                }
            }
        }

        return list;
    }

    // =========================================================
    // APPROVE ENROLLMENT REQUEST
    // Updates status to 'Approved' and inserts record into enrollments table
    // in a single atomic database transaction.
    // =========================================================
    public boolean approveEnrollmentRequest(long requestId, long adminUserId, String decisionNote) throws SQLException {
        String updateReqSql = "UPDATE enrollment_requests SET status = 'Approved', decided_by = ?, decided_at = NOW(), decision_note = ? " +
                              "WHERE request_id = ? AND status = 'Pending'";

        String fetchReqSql = "SELECT student_id, course_id, academic_year, semester, enrollment_type FROM enrollment_requests WHERE request_id = ?";

        String checkEnrollSql = "SELECT 1 FROM enrollments WHERE student_id = ? AND course_id = ? AND academic_year = ? AND semester = ?";

        String insertEnrollSql = "INSERT INTO enrollments (student_id, course_id, academic_year, semester, enrollment_type, enrollment_status, enrolled_at) " +
                                 "VALUES (?, ?, ?, ?, ?, 'Enrolled', NOW())";

        DBConnection.ADMIN_CON.setAutoCommit(false);

        try {
            // 1. Update Enrollment Request Status
            int rowsUpdated;
            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(updateReqSql)) {
                pstmt.setLong(1, adminUserId);
                pstmt.setString(2, decisionNote != null ? decisionNote : "Approved by administrator");
                pstmt.setLong(3, requestId);
                rowsUpdated = pstmt.executeUpdate();
            }

            if (rowsUpdated == 0) {
                DBConnection.ADMIN_CON.rollback();
                return false;
            }

            // 2. Fetch Request Details
            long studentId = 0;
            int courseId = 0;
            String academicYear = "2026";
            String semester = "Semester I";
            String enrollmentType = "Normal";

            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(fetchReqSql)) {
                pstmt.setLong(1, requestId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getLong("student_id");
                        courseId = rs.getInt("course_id");
                        academicYear = rs.getString("academic_year");
                        semester = rs.getString("semester");
                        enrollmentType = rs.getString("enrollment_type");
                    } else {
                        DBConnection.ADMIN_CON.rollback();
                        return false;
                    }
                }
            }

            // 3. Check if already enrolled in enrollments table
            boolean alreadyEnrolled = false;
            try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(checkEnrollSql)) {
                pstmt.setLong(1, studentId);
                pstmt.setInt(2, courseId);
                pstmt.setString(3, academicYear);
                pstmt.setString(4, semester);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        alreadyEnrolled = true;
                    }
                }
            }

            // 4. Insert into enrollments table if not existing
            if (!alreadyEnrolled) {
                try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(insertEnrollSql)) {
                    pstmt.setLong(1, studentId);
                    pstmt.setInt(2, courseId);
                    pstmt.setString(3, academicYear);
                    pstmt.setString(4, semester);
                    pstmt.setString(5, enrollmentType);
                    pstmt.executeUpdate();
                }
            }

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
    // DECLINE ENROLLMENT REQUEST
    // =========================================================
    public boolean declineEnrollmentRequest(long requestId, long adminUserId, String decisionNote) throws SQLException {
        String sql = "UPDATE enrollment_requests SET status = 'Declined', decided_by = ?, decided_at = NOW(), decision_note = ? " +
                     "WHERE request_id = ? AND status = 'Pending'";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setLong(1, adminUserId);
            pstmt.setString(2, decisionNote != null ? decisionNote : "Declined by administrator");
            pstmt.setLong(3, requestId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    // =========================================================
    // CREATE ENROLLMENT REQUEST
    // =========================================================
    public boolean createEnrollmentRequest(long studentId, int courseId, String academicYear, String semester,
                                            String enrollmentType, String requestNote) throws SQLException {
        String sql = "INSERT INTO enrollment_requests (student_id, course_id, academic_year, semester, enrollment_type, request_note, status, requested_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'Pending', NOW())";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, academicYear != null ? academicYear : "2026");
            pstmt.setString(4, semester != null ? semester : "Semester I");
            pstmt.setString(5, enrollmentType != null ? enrollmentType : "Normal");
            pstmt.setString(6, requestNote);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    // =========================================================
    // DELETE ENROLLMENT REQUEST
    // =========================================================
    public boolean deleteEnrollmentRequest(long requestId) throws SQLException {
        String sql = "DELETE FROM enrollment_requests WHERE request_id = ?";

        try (PreparedStatement pstmt = DBConnection.ADMIN_CON.prepareStatement(sql)) {
            pstmt.setLong(1, requestId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================
    // HELPER: MAP RESULTSET TO MODEL
    // =========================================================
    private EnrollmentRequest mapResultSetToEnrollmentRequest(ResultSet rs) throws SQLException {
        EnrollmentRequest req = new EnrollmentRequest();

        req.setRequestId(rs.getLong("request_id"));
        req.setStudentId(rs.getLong("student_id"));
        req.setRegistrationNo(rs.getString("registration_no"));
        req.setStudentName(rs.getString("student_name"));
        req.setStudentType(rs.getString("student_type"));

        req.setCourseId(rs.getInt("course_id"));
        req.setCourseCode(rs.getString("course_code"));
        req.setCourseName(rs.getString("course_name"));
        req.setTotalCredits(rs.getBigDecimal("total_credits"));

        req.setAcademicYear(rs.getString("academic_year"));
        req.setSemester(rs.getString("semester"));
        req.setEnrollmentType(rs.getString("enrollment_type"));
        req.setRequestNote(rs.getString("request_note"));
        req.setStatus(rs.getString("status"));
        req.setRequestedAt(rs.getTimestamp("requested_at"));

        long decBy = rs.getLong("decided_by");
        if (!rs.wasNull()) {
            req.setDecidedBy(decBy);
        }
        req.setDecidedByName(rs.getString("decided_by_name"));
        req.setDecidedAt(rs.getTimestamp("decided_at"));
        req.setDecisionNote(rs.getString("decision_note"));

        req.setAlreadyEnrolled(rs.getInt("already_enrolled") == 1);

        return req;
    }
}
