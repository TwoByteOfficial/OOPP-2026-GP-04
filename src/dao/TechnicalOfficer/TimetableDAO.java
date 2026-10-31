package dao.TechnicalOfficer;

import util.DBConnection;
import model.TimetableEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    public List<TimetableEntry> getTimetableByBatch(int batchYear) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable_entries WHERE batch_year = ? ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), start_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, batchYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    list.add(t);
                }
            }
        }
        return list;
    }



    public List<TimetableEntry> getTimetableByDepartment(
            int departmentId, String academicYear, String semester)
            throws SQLException {

        List<TimetableEntry> list = new ArrayList<>();

        String sql = "SELECT t.*, c.course_code, c.course_name, " +
                "u.full_name AS lecturer_name " +
                "FROM timetable_entries t " +
                "JOIN courses c ON t.course_id = c.course_id " +
                "LEFT JOIN lecturers l ON t.lecturer_id = l.user_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id " +
                "WHERE t.department_id = ? " +
                "AND t.academic_year = ? " +
                "AND t.semester = ? " +
                "ORDER BY FIELD(t.day_of_week, " +
                "'Monday','Tuesday','Wednesday','Thursday'," +
                "'Friday','Saturday','Sunday'), t.start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);
            stmt.setString(2, academicYear);
            stmt.setString(3, semester);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimetableEntry t = new TimetableEntry();

                    t.setTimetableId(rs.getLong("timetable_id"));
                    t.setDepartmentId(rs.getInt("department_id"));
                    t.setCourseId(rs.getInt("course_id"));

                    long lecturerId = rs.getLong("lecturer_id");
                    if (!rs.wasNull()) {
                        t.setLecturerId(lecturerId);
                    }

                    t.setBatchYear(rs.getInt("batch_year"));
                    t.setComponent(rs.getString("component"));
                    t.setDayOfWeek(rs.getString("day_of_week"));
                    t.setStartTime(rs.getTime("start_time"));
                    t.setEndTime(rs.getTime("end_time"));
                    t.setVenue(rs.getString("venue"));
                    t.setAcademicYear(rs.getString("academic_year"));
                    t.setSemester(rs.getString("semester"));

                    t.setCourseCode(rs.getString("course_code"));
                    t.setCourseName(rs.getString("course_name"));
                    t.setLecturerName(rs.getString("lecturer_name"));

                    list.add(t);
                }
            }
        }

        return list;
    }


    public List<TimetableEntry> getTimetableByDepartmentUser(long userId)
            throws SQLException {

        List<TimetableEntry> list = new ArrayList<>();

        String sql =
                "SELECT t.*, c.course_code, c.course_name, " +
                        "lecturer_user.full_name AS lecturer_name " +
                        "FROM timetable_entries t " +
                        "JOIN users officer ON officer.user_id = ? " +
                        "AND officer.department_id = t.department_id " +
                        "JOIN courses c ON t.course_id = c.course_id " +
                        "LEFT JOIN users lecturer_user " +
                        "ON t.lecturer_id = lecturer_user.user_id " +
                        "ORDER BY FIELD(t.day_of_week, " +
                        "'Monday','Tuesday','Wednesday','Thursday'," +
                        "'Friday','Saturday','Sunday'), t.start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

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

                    t.setCourseCode(rs.getString("course_code"));
                    t.setCourseName(rs.getString("course_name"));
                    t.setLecturerName(rs.getString("lecturer_name"));

                    list.add(t);
                }
            }
        }

        return list;
    }






}
