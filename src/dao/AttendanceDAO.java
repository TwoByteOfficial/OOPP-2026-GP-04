package dao;

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
            conn.setAutoCommit(true);
        }
        return success;
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
                           "SET ar.attendance_status = 'MEDICAL' WHERE m.medical_id = ? AND ar.attendance_record_id > 0";
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
            conn.setAutoCommit(true);
        }
        return success;
    }
}
