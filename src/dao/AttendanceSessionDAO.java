package dao;

import model.AttendanceSession;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceSessionDAO {

    public boolean insert(AttendanceSession a) {

        String sql = """
                INSERT INTO attendance_sessions
                (course_id, component, session_no, session_date,
                 start_time, end_time, duration_hours, conducted_by, topic)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getCourseId());
            ps.setString(2, a.getComponent());
            ps.setInt(3, a.getSessionNo());
            ps.setDate(4, a.getSessionDate());

            if (a.getStartTime() != null)
                ps.setTime(5, a.getStartTime());
            else
                ps.setNull(5, Types.TIME);

            if (a.getEndTime() != null)
                ps.setTime(6, a.getEndTime());
            else
                ps.setNull(6, Types.TIME);

            ps.setBigDecimal(7, a.getDurationHours());

            if (a.getConductedBy() != null)
                ps.setLong(8, a.getConductedBy());
            else
                ps.setNull(8, Types.BIGINT);

            ps.setString(9, a.getTopic());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AttendanceSession findById(Long id) {

        String sql = "SELECT * FROM attendance_sessions WHERE attendance_session_id = ?";

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

    public List<AttendanceSession> findAll() {

        List<AttendanceSession> list = new ArrayList<>();

        String sql = "SELECT * FROM attendance_sessions ORDER BY attendance_session_id";

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

    public boolean update(AttendanceSession a) {

        String sql = """
                UPDATE attendance_sessions
                SET course_id = ?, component = ?, session_no = ?,
                    session_date = ?, start_time = ?, end_time = ?,
                    duration_hours = ?, conducted_by = ?, topic = ?
                WHERE attendance_session_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getCourseId());
            ps.setString(2, a.getComponent());
            ps.setInt(3, a.getSessionNo());
            ps.setDate(4, a.getSessionDate());

            if (a.getStartTime() != null)
                ps.setTime(5, a.getStartTime());
            else
                ps.setNull(5, Types.TIME);

            if (a.getEndTime() != null)
                ps.setTime(6, a.getEndTime());
            else
                ps.setNull(6, Types.TIME);

            ps.setBigDecimal(7, a.getDurationHours());

            if (a.getConductedBy() != null)
                ps.setLong(8, a.getConductedBy());
            else
                ps.setNull(8, Types.BIGINT);

            ps.setString(9, a.getTopic());
            ps.setLong(10, a.getAttendanceSessionId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM attendance_sessions WHERE attendance_session_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private AttendanceSession mapResultSet(ResultSet rs) throws SQLException {

        return new AttendanceSession(
                rs.getLong("attendance_session_id"),
                rs.getInt("course_id"),
                rs.getString("component"),
                rs.getInt("session_no"),
                rs.getDate("session_date"),
                rs.getTime("start_time"),
                rs.getTime("end_time"),
                rs.getBigDecimal("duration_hours"),
                (Long) rs.getObject("conducted_by"),
                rs.getString("topic"),
                rs.getTimestamp("created_at")
        );
    }
}