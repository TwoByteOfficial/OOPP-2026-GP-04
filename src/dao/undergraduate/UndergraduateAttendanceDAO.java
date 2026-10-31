package dao.undergraduate;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Undergraduate Attendance Directory and Detailed Eligibility.
 */
public class UndergraduateAttendanceDAO {

    public static class AttendanceRecordDTO {
        public int courseId;
        public String courseCode;
        public String courseName;
        public int totalSessions;
        public int attendedSessions;
        public int medicalSessions;
        public int absentSessions;
        public double adjustedPercentage;

        // Specific hours breakdown
        public int totalTheoryHours;
        public int attendedTheoryHours;
        public int totalPracticalHours;
        public int attendedPracticalHours;

        public boolean isEligible() {
            return adjustedPercentage >= 80.0;
        }
    }

    public static class AttendanceStripDTO {
        public double overallPercentage = 0.0;
        public int eligibleModulesCount = 0;
        public int totalModulesCount = 0;
        public int totalAttendedHours = 0;
        public int totalScheduledHours = 0;
        public int approvedMedicalsCount = 0;
    }

    public static List<AttendanceRecordDTO> getStudentAttendanceDirectory(long studentId) {
        List<AttendanceRecordDTO> list = new ArrayList<>();
        String sql = "SELECT s.course_id, s.course_code, s.course_name, s.total_sessions, " +
                "s.attended_sessions, s.medical_sessions, s.absent_sessions, " +
                "ROUND(s.adjusted_attendance_percent, 1) AS adj_pct " +
                "FROM v_attendance_summary s WHERE s.student_id = ? ORDER BY s.course_code ASC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecordDTO dto = new AttendanceRecordDTO();
                    dto.courseId = rs.getInt("course_id");
                    dto.courseCode = rs.getString("course_code");
                    dto.courseName = rs.getString("course_name");
                    dto.totalSessions = rs.getInt("total_sessions");
                    dto.attendedSessions = rs.getInt("attended_sessions");
                    dto.medicalSessions = rs.getInt("medical_sessions");
                    dto.absentSessions = rs.getInt("absent_sessions");
                    dto.adjustedPercentage = rs.getDouble("adj_pct");

                    loadHoursBreakdown(studentId, dto.courseId, dto);
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void loadHoursBreakdown(long studentId, int courseId, AttendanceRecordDTO dto) {
        String sql = "SELECT ats.component, " +
                "COUNT(ats.attendance_session_id) * 2 AS scheduled_hrs, " +
                "SUM(CASE WHEN ar.attendance_status IN ('PRESENT', 'LATE', 'MEDICAL') THEN 2 ELSE 0 END) AS attended_hrs " +
                "FROM attendance_sessions ats " +
                "LEFT JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id AND ar.student_id = ? " +
                "WHERE ats.course_id = ? " +
                "GROUP BY ats.component";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String comp = rs.getString("component");
                    int sched = rs.getInt("scheduled_hrs");
                    int att = rs.getInt("attended_hrs");
                    if ("Theory".equalsIgnoreCase(comp)) {
                        dto.totalTheoryHours = sched;
                        dto.attendedTheoryHours = att;
                    } else if ("Practical".equalsIgnoreCase(comp)) {
                        dto.totalPracticalHours = sched;
                        dto.attendedPracticalHours = att;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static AttendanceStripDTO getAttendanceStripMetrics(long studentId) {
        AttendanceStripDTO strip = new AttendanceStripDTO();
        List<AttendanceRecordDTO> records = getStudentAttendanceDirectory(studentId);

        if (records.isEmpty()) return strip;

        strip.totalModulesCount = records.size();
        double sumPct = 0;

        for (AttendanceRecordDTO r : records) {
            sumPct += r.adjustedPercentage;
            if (r.isEligible()) strip.eligibleModulesCount++;
            strip.totalAttendedHours += (r.attendedTheoryHours + r.attendedPracticalHours);
            strip.totalScheduledHours += (r.totalTheoryHours + r.totalPracticalHours);
            strip.approvedMedicalsCount += r.medicalSessions;
        }

        strip.overallPercentage = Math.round((sumPct / records.size()) * 10.0) / 10.0;
        return strip;
    }
}