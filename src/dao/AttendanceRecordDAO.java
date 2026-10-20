package dao;

import model.AttendanceRecord;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRecordDAO {

    public boolean insert(AttendanceRecord a) {

        String sql = """
                INSERT INTO attendance_records
                (attendance_session_id, student_id,
                 attendance_status, remarks, recorded_by)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, a.getAttendanceSessionId());
            ps.setLong(2, a.getStudentId());
            ps.setString(3, a.getAttendanceStatus());
            ps.setString(4, a.getRemarks());

            if (a.getRecordedBy() != null)
                ps.setLong(5, a.getRecordedBy());
            else
                ps.setNull(5, Types.BIGINT);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AttendanceRecord findById(Long id) {

        String sql = "SELECT * FROM attendance_records WHERE attendance_record_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<AttendanceRecord> findAll() {

        List<AttendanceRecord> list = new ArrayList<>();

        String sql = "SELECT * FROM attendance_records ORDER BY attendance_record_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapResultSet(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(AttendanceRecord a) {

        String sql = """
                UPDATE attendance_records
                SET attendance_session_id = ?, student_id = ?,
                    attendance_status = ?, remarks = ?, recorded_by = ?
                WHERE attendance_record_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, a.getAttendanceSessionId());
            ps.setLong(2, a.getStudentId());
            ps.setString(3, a.getAttendanceStatus());
            ps.setString(4, a.getRemarks());

            if (a.getRecordedBy() != null)
                ps.setLong(5, a.getRecordedBy());
            else
                ps.setNull(5, Types.BIGINT);

            ps.setLong(6, a.getAttendanceRecordId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM attendance_records WHERE attendance_record_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private AttendanceRecord mapResultSet(ResultSet rs) throws SQLException {

        return new AttendanceRecord(
                rs.getLong("attendance_record_id"),
                rs.getLong("attendance_session_id"),
                rs.getLong("student_id"),
                rs.getString("attendance_status"),
                rs.getString("remarks"),
                (Long) rs.getObject("recorded_by"),
                rs.getTimestamp("recorded_at"),
                rs.getTimestamp("updated_at")
        );
    }
}