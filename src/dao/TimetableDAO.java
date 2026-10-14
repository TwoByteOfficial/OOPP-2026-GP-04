package dao;

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
}
