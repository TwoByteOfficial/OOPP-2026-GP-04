package dao.TechnicalOfficer;

import model.AttendanceSession;
import model.AttendanceRecord;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing attendance operations for Technical Officers.
 * Handles saving batch attendance, retrieving course sessions, loading recorded attendance,
 * and processing medical approval/rejection updates with database transactions.
 */
public class AttendanceDAO {

    /**
     * Saves or updates a list of student attendance records in a single database batch.
     * Uses a transaction to make sure either all records save successfully or none do.
     * If an attendance record already exists for that session and student, it updates
     * the attendance status and remarks instead of failing.
     *
     * @param records List of AttendanceRecord objects to save
     * @return true if the batch executed and committed successfully
     * @throws SQLException if a database error occurs during the operation
     */
    public boolean recordAttendanceBatch(List<AttendanceRecord> records) throws SQLException {
        String sql = "INSERT INTO attendance_records (attendance_session_id, student_id, attendance_status, remarks, recorded_by) " +
                "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE attendance_status = VALUES(attendance_status), remarks = VALUES(remarks)";

        // Disable auto-commit to begin manual transaction
        DBConnection.TO_CON.setAutoCommit(false);

        try {
            try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {
                // Add each student record to the SQL batch
                for (AttendanceRecord r : records) {
                    stmt.setLong(1, r.getAttendanceSessionId());
                    stmt.setLong(2, r.getStudentId());
                    stmt.setString(3, r.getAttendanceStatus());
                    stmt.setString(4, r.getRemarks());
                    // Record which Technical Officer saved this attendance
                    if (r.getRecordedBy() != null) {
                        stmt.setLong(5, r.getRecordedBy());
                    } else {
                        stmt.setNull(5, Types.BIGINT);
                    }
                    stmt.addBatch();
                }
                // Execute all batched statements in one roundtrip
                stmt.executeBatch();
            }

            // Commit transaction after successful execution
            DBConnection.TO_CON.commit();
            return true;
        } catch (SQLException | RuntimeException e) {
            // Roll back any changes if an error occurs to prevent partial data writes
            DBConnection.TO_CON.rollback();
            throw e;
        } finally {
            // Restore default auto-commit mode
            DBConnection.TO_CON.setAutoCommit(true);
        }
    }

    /**
     * Retrieves all attendance sessions scheduled for a given course and component.
     * For example, gets all Lab/Practical sessions or Lecture/Theory sessions.
     *
     * @param courseId  The course ID
     * @param component The session component (e.g., "Theory" or "Practical")
     * @return List of AttendanceSession objects sorted by session number
     * @throws SQLException if a query execution error occurs
     */
    public List<AttendanceSession> getSessionsByCourse(
            int courseId, String component) throws SQLException {

        List<AttendanceSession> sessions = new ArrayList<>();

        String sql = "SELECT * FROM attendance_sessions " +
                "WHERE course_id = ? AND component = ? " +
                "ORDER BY session_no";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, component);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceSession session = new AttendanceSession();

                    session.setAttendanceSessionId(rs.getLong("attendance_session_id"));
                    session.setCourseId(rs.getInt("course_id"));
                    session.setComponent(rs.getString("component"));
                    session.setSessionNo(rs.getInt("session_no"));
                    session.setSessionDate(rs.getDate("session_date"));
                    session.setStartTime(rs.getTime("start_time"));
                    session.setEndTime(rs.getTime("end_time"));
                    session.setDurationHours(rs.getBigDecimal("duration_hours"));

                    long conductedBy = rs.getLong("conducted_by");
                    session.setConductedBy(rs.wasNull() ? null : conductedBy);

                    session.setTopic(rs.getString("topic"));

                    sessions.add(session);
                }
            }
        }

        return sessions;
    }

    /**
     * Retrieves existing attendance records already saved for a specific session.
     * Used by the attendance table to display currently recorded statuses (Present/Absent/Excused).
     *
     * @param sessionId The attendance session ID
     * @return List of AttendanceRecord objects sorted by student ID
     * @throws SQLException if a database error occurs
     */
    public List<AttendanceRecord> getAttendanceBySession(
            long sessionId) throws SQLException {

        List<AttendanceRecord> records = new ArrayList<>();

        String sql = "SELECT attendance_record_id, attendance_session_id, " +
                "student_id, attendance_status, remarks, recorded_by, recorded_at " +
                "FROM attendance_records " +
                "WHERE attendance_session_id = ? " +
                "ORDER BY student_id";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, sessionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = new AttendanceRecord();

                    record.setAttendanceRecordId(rs.getLong("attendance_record_id"));
                    record.setAttendanceSessionId(rs.getLong("attendance_session_id"));
                    record.setStudentId(rs.getLong("student_id"));
                    record.setAttendanceStatus(rs.getString("attendance_status"));
                    record.setRemarks(rs.getString("remarks"));

                    long recordedBy = rs.getLong("recorded_by");
                    record.setRecordedBy(rs.wasNull() ? null : recordedBy);

                    record.setRecordedAt(rs.getTimestamp("recorded_at"));

                    records.add(record);
                }
            }
        }

        return records;
    }

    /**
     * Approves a student's medical request and updates their attendance to PRESENT.
     * This operation runs in a transaction across both the medicals table and attendance_records table.
     *
     * @param medicalId  The ID of the medical submission
     * @param reviewerId The user ID of the Technical Officer approving the request
     * @param comment    Feedback or review comment provided by the officer
     * @return true if both updates succeeded and committed
     * @throws SQLException if an error occurs during update or rollback
     */
    public boolean approveMedical(long medicalId, long reviewerId, String comment) throws SQLException {
        // SQL to mark the medical certificate as Approved with reviewer info
        String mSql = "UPDATE medicals SET approval_status = 'Approved', reviewed_by = ?, reviewed_at = NOW(), review_comment = ? WHERE medical_id = ?";
        
        // SQL to update the student's attendance record status to PRESENT
        String arSql = "UPDATE attendance_records ar JOIN medicals m ON m.attendance_record_id = ar.attendance_record_id " +
                "SET ar.attendance_status = 'PRESENT' WHERE m.medical_id = ? AND ar.attendance_record_id > 0";

        DBConnection.TO_CON.setAutoCommit(false);

        try {
            // 1. Update medical table
            try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(mSql)) {
                stmt.setLong(1, reviewerId);
                stmt.setString(2, comment);
                stmt.setLong(3, medicalId);
                stmt.executeUpdate();
            }

            // 2. Update linked attendance record to PRESENT
            try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(arSql)) {
                stmt.setLong(1, medicalId);
                stmt.executeUpdate();
            }

            DBConnection.TO_CON.commit();
            return true;
        } catch (SQLException | RuntimeException e) {
            DBConnection.TO_CON.rollback();
            throw e;
        } finally {
            DBConnection.TO_CON.setAutoCommit(true);
        }
    }

    /**
     * Rejects a pending medical request with reviewer ID, review timestamp, and review comment.
     * Only updates the record if it is currently in 'Pending' status.
     *
     * @param medicalId  The ID of the medical submission
     * @param reviewerId The user ID of the Technical Officer rejecting the request
     * @param comment    Reason for rejection
     * @return true if the record was successfully updated
     * @throws SQLException if a query execution error occurs
     */
    public boolean rejectMedical(long medicalId, long reviewerId, String comment)
            throws SQLException {

        String sql = "UPDATE medicals " +
                "SET approval_status = 'Rejected', " +
                "reviewed_by = ?, reviewed_at = NOW(), review_comment = ? " +
                "WHERE medical_id = ? AND approval_status = 'Pending'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, reviewerId);
            stmt.setString(2, comment);
            stmt.setLong(3, medicalId);

            return stmt.executeUpdate() > 0;
        }
    }

}
