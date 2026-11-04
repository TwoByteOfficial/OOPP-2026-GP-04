package dao.TechnicalOfficer;

import util.DBConnection;
import model.TimetableEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for retrieving timetable schedules for Technical Officers.
 * Supports loading timetable schedules by undergraduate batch year, department semester,
 * and officer department user ID.
 */
public class TimetableDAO {

    /**
     * Retrieves all timetable entries for a specific undergraduate batch year.
     * Results are sorted logically by day of the week (Monday through Sunday) then start time.
     *
     * @param batchYear The academic intake year (e.g. 2022)
     * @return List of TimetableEntry records
     * @throws SQLException if a query execution error occurs
     */
    public List<TimetableEntry> getTimetableByBatch(int batchYear) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable_entries WHERE batch_year = ? ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), start_time";
        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {
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

    /**
     * Retrieves timetable entries filtered by department ID, academic year, and semester.
     * Includes course code, course name, and lecturer name via JOINs.
     *
     * @param departmentId The department ID
     * @param academicYear Academic year string (e.g., "2025/2026")
     * @param semester     Semester string (e.g., "Semester 1")
     * @return List of TimetableEntry records
     * @throws SQLException if a database query error occurs
     */
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

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

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

    /**
     * Retrieves the entire department timetable matching the department of the logged-in Technical Officer.
     * Used by DepartmentTimetablePanel on the dashboard to display the weekly schedule.
     *
     * @param userId The user ID of the Technical Officer
     * @return List of TimetableEntry records for the officer's department
     * @throws SQLException if a query execution error occurs
     */
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

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

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
