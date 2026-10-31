package dao;
import model.AttendanceSession;

import util.DBConnection;
import model.AttendanceRecord;
import model.Medical;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AttendanceDAO {

    public boolean recordAttendanceBatch(List<AttendanceRecord> records) throws SQLException {
        String sql = "INSERT INTO attendance_records (attendance_session_id, student_id, attendance_status, remarks, recorded_by) " +
                     "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE attendance_status = VALUES(attendance_status), remarks = VALUES(remarks)";
        Connection conn = DBConnection.getConnection();
        boolean success = false;
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (AttendanceRecord r : records) {
                    stmt.setLong(1, r.getAttendanceSessionId());
                    stmt.setLong(2, r.getStudentId());
                    stmt.setString(3, r.getAttendanceStatus());
                    stmt.setString(4, r.getRemarks());
                    if (r.getRecordedBy() != null) stmt.setLong(5, r.getRecordedBy()); else stmt.setNull(5, Types.BIGINT);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
            success = true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
        return success;
    }




    public List<AttendanceSession> getSessionsByCourse(
            int courseId, String component) throws SQLException {

        List<AttendanceSession> sessions = new ArrayList<>();

        String sql = "SELECT * FROM attendance_sessions " +
                "WHERE course_id = ? AND component = ? " +
                "ORDER BY session_no";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, component);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceSession session = new AttendanceSession();

                    session.setAttendanceSessionId(
                            rs.getLong("attendance_session_id"));
                    session.setCourseId(rs.getInt("course_id"));
                    session.setComponent(rs.getString("component"));
                    session.setSessionNo(rs.getInt("session_no"));
                    session.setSessionDate(rs.getDate("session_date"));
                    session.setStartTime(rs.getTime("start_time"));
                    session.setEndTime(rs.getTime("end_time"));
                    session.setDurationHours(
                            rs.getBigDecimal("duration_hours"));

                    long conductedBy = rs.getLong("conducted_by");
                    session.setConductedBy(
                            rs.wasNull() ? null : conductedBy);

                    session.setTopic(rs.getString("topic"));

                    sessions.add(session);
                }
            }
        }

        return sessions;
    }




    public List<AttendanceRecord> getAttendanceBySession(
            long sessionId) throws SQLException {

        List<AttendanceRecord> records = new ArrayList<>();

        String sql = "SELECT attendance_record_id, attendance_session_id, " +
                "student_id, attendance_status, remarks, recorded_by, recorded_at " +
                "FROM attendance_records " +
                "WHERE attendance_session_id = ? " +
                "ORDER BY student_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sessionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = new AttendanceRecord();

                    record.setAttendanceRecordId(
                            rs.getLong("attendance_record_id"));
                    record.setAttendanceSessionId(
                            rs.getLong("attendance_session_id"));
                    record.setStudentId(rs.getLong("student_id"));
                    record.setAttendanceStatus(
                            rs.getString("attendance_status"));
                    record.setRemarks(rs.getString("remarks"));

                    long recordedBy = rs.getLong("recorded_by");
                    record.setRecordedBy(
                            rs.wasNull() ? null : recordedBy);

                    record.setRecordedAt(rs.getTimestamp("recorded_at"));

                    records.add(record);
                }
            }
        }

        return records;
    }




    public boolean approveMedical(long medicalId, long reviewerId, String comment) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean success = false;
        try {
            conn.setAutoCommit(false);

            String mSql = "UPDATE medicals SET approval_status = 'Approved', reviewed_by = ?, reviewed_at = NOW(), review_comment = ? WHERE medical_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(mSql)) {
                stmt.setLong(1, reviewerId);
                stmt.setString(2, comment);
                stmt.setLong(3, medicalId);
                stmt.executeUpdate();
            }

            String arSql = "UPDATE attendance_records ar JOIN medicals m ON m.attendance_record_id = ar.attendance_record_id " +
                           "SET ar.attendance_status = 'PRESENT' WHERE m.medical_id = ? AND ar.attendance_record_id > 0";
            try (PreparedStatement stmt = conn.prepareStatement(arSql)) {
                stmt.setLong(1, medicalId);
                stmt.executeUpdate();
            }

            conn.commit();
            success = true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
        return success;
    }



    public boolean rejectMedical(long medicalId, long reviewerId, String comment)
            throws SQLException {

        String sql = "UPDATE medicals " +
                "SET approval_status = 'Rejected', " +
                "reviewed_by = ?, reviewed_at = NOW(), review_comment = ? " +
                "WHERE medical_id = ? AND approval_status = 'Pending'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reviewerId);
            stmt.setString(2, comment);
            stmt.setLong(3, medicalId);

            return stmt.executeUpdate() > 0;
        }
    }



}
