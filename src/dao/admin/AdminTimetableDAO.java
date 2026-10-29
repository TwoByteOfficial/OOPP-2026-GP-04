package dao.admin;

import util.DBConnection;
import model.TimetableEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminTimetableDAO {

    public List<TimetableEntry> getAllTimetableEntries() throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable_entries " +
                "ORDER BY department_id, batch_year, FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), start_time";

        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToTimetableEntry(rs));
            }
        }
        return list;
    }

    public List<TimetableEntry> getTimetablesByFilter(int departmentId, int batchYear, String semester) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable_entries " +
                "WHERE department_id = ? AND batch_year = ? AND semester = ? " +
                "ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), start_time";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);
            pstmt.setInt(2, batchYear);
            pstmt.setString(3, semester);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTimetableEntry(rs));
                }
            }
        }
        return list;
    }

    public TimetableEntry getTimetableById(long timetableId) throws SQLException {
        String sql = "SELECT * FROM timetable_entries WHERE timetable_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, timetableId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTimetableEntry(rs);
                }
            }
        }
        return null;
    }

    public boolean createTimetableEntry(TimetableEntry t) throws SQLException {
        String sql = "INSERT INTO timetable_entries (department_id, course_id, lecturer_id, batch_year, component, day_of_week, start_time, end_time, venue, academic_year, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, t.getDepartmentId());
            pstmt.setInt(2, t.getCourseId());
            if (t.getLecturerId() != null) pstmt.setLong(3, t.getLecturerId()); else pstmt.setNull(3, Types.BIGINT);
            pstmt.setInt(4, t.getBatchYear());
            pstmt.setString(5, t.getComponent());
            pstmt.setString(6, t.getDayOfWeek());
            pstmt.setTime(7, t.getStartTime());
            pstmt.setTime(8, t.getEndTime());
            pstmt.setString(9, t.getVenue());
            pstmt.setString(10, t.getAcademicYear());
            pstmt.setString(11, t.getSemester());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        t.setTimetableId(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateTimetableEntry(TimetableEntry t) throws SQLException {
        String sql = "UPDATE timetable_entries SET department_id = ?, course_id = ?, lecturer_id = ?, batch_year = ?, component = ?, day_of_week = ?, start_time = ?, end_time = ?, venue = ?, academic_year = ?, semester = ? WHERE timetable_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, t.getDepartmentId());
            pstmt.setInt(2, t.getCourseId());
            if (t.getLecturerId() != null) pstmt.setLong(3, t.getLecturerId()); else pstmt.setNull(3, Types.BIGINT);
            pstmt.setInt(4, t.getBatchYear());
            pstmt.setString(5, t.getComponent());
            pstmt.setString(6, t.getDayOfWeek());
            pstmt.setTime(7, t.getStartTime());
            pstmt.setTime(8, t.getEndTime());
            pstmt.setString(9, t.getVenue());
            pstmt.setString(10, t.getAcademicYear());
            pstmt.setString(11, t.getSemester());
            pstmt.setLong(12, t.getTimetableId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteTimetableEntry(long timetableId) throws SQLException {
        String sql = "DELETE FROM timetable_entries WHERE timetable_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, timetableId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private TimetableEntry mapResultSetToTimetableEntry(ResultSet rs) throws SQLException {
        TimetableEntry t = new TimetableEntry();
        t.setTimetableId(rs.getLong("timetable_id"));
        t.setDepartmentId(rs.getInt("department_id"));
        t.setCourseId(rs.getInt("course_id"));
        t.setLecturerId((Long) rs.getObject("lecturer_id"));
        t.setBatchYear(rs.getInt("batch_year"));
        t.setComponent(rs.getString("component"));
        t.setDayOfWeek(rs.getString("day_of_week"));
        t.setStartTime(rs.getTime("start_time"));
        t.setEndTime(rs.getTime("end_time"));
        t.setVenue(rs.getString("venue"));
        t.setAcademicYear(rs.getString("academic_year"));
        t.setSemester(rs.getString("semester"));
        return t;
    }
}