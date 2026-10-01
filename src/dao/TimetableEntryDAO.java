package dao;

import model.TimetableEntry;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableEntryDAO {

    public boolean insert(TimetableEntry t) {

        String sql = """
                INSERT INTO timetable_entries
                (department_id, course_id, lecturer_id, batch_year,
                 component, day_of_week, start_time, end_time,
                 venue, academic_year, semester)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getDepartmentId());
            ps.setInt(2, t.getCourseId());

            if (t.getLecturerId() != null)
                ps.setLong(3, t.getLecturerId());
            else
                ps.setNull(3, Types.BIGINT);

            ps.setInt(4, t.getBatchYear());
            ps.setString(5, t.getComponent());
            ps.setString(6, t.getDayOfWeek());
            ps.setTime(7, t.getStartTime());
            ps.setTime(8, t.getEndTime());
            ps.setString(9, t.getVenue());
            ps.setString(10, t.getAcademicYear());
            ps.setString(11, t.getSemester());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TimetableEntry findById(Long id) {

        String sql = """
                SELECT * FROM timetable_entries
                WHERE timetable_id = ?
                """;

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

    public List<TimetableEntry> findAll() {

        List<TimetableEntry> list = new ArrayList<>();

        String sql = """
                SELECT * FROM timetable_entries
                ORDER BY timetable_id
                """;

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

    public boolean update(TimetableEntry t) {

        String sql = """
                UPDATE timetable_entries
                SET department_id = ?, course_id = ?,
                    lecturer_id = ?, batch_year = ?, component = ?,
                    day_of_week = ?, start_time = ?, end_time = ?,
                    venue = ?, academic_year = ?, semester = ?
                WHERE timetable_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getDepartmentId());
            ps.setInt(2, t.getCourseId());

            if (t.getLecturerId() != null)
                ps.setLong(3, t.getLecturerId());
            else
                ps.setNull(3, Types.BIGINT);

            ps.setInt(4, t.getBatchYear());
            ps.setString(5, t.getComponent());
            ps.setString(6, t.getDayOfWeek());
            ps.setTime(7, t.getStartTime());
            ps.setTime(8, t.getEndTime());
            ps.setString(9, t.getVenue());
            ps.setString(10, t.getAcademicYear());
            ps.setString(11, t.getSemester());
            ps.setLong(12, t.getTimetableId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = """
                DELETE FROM timetable_entries
                WHERE timetable_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TimetableEntry mapResultSet(ResultSet rs) throws SQLException {

        return new TimetableEntry(
                rs.getLong("timetable_id"),
                rs.getInt("department_id"),
                rs.getInt("course_id"),
                (Long) rs.getObject("lecturer_id"),
                rs.getInt("batch_year"),
                rs.getString("component"),
                rs.getString("day_of_week"),
                rs.getTime("start_time"),
                rs.getTime("end_time"),
                rs.getString("venue"),
                rs.getString("academic_year"),
                rs.getString("semester")
        );
    }
}