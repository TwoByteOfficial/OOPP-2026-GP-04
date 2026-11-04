package dao.undergraduate;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Undergraduate Attendance Directory and Detailed Eligibility.
 * Facilitates attendance metric retrieval, course-wise session aggregates from database views,
 * component breakdown calculation (Theory vs. Practical hours), and overall student eligibility computation.
 */
public class UndergraduateAttendanceDAO {

    /**
     * Data Transfer Object representing detailed attendance records and examination eligibility for an enrolled course.
     */
    public static class AttendanceRecordDTO {
        // Enrolled module identifiers and nomenclature
        public int courseId;
        public String courseCode;
        public String courseName;

        // Session frequency distribution counts
        public int totalSessions;
        public int attendedSessions;
        public int medicalSessions;
        public int absentSessions;

        // Final adjusted attendance percentage after applying approved medical leave credit
        public double adjustedPercentage;

        // Specific contact hours breakdown divided by course component
        public int totalTheoryHours;
        public int attendedTheoryHours;
        public int totalPracticalHours;
        public int attendedPracticalHours;

        /**
         * Evaluates academic examination eligibility against the statutory 80% attendance regulation.
         *
         * @return {@code true} if adjusted attendance meets or exceeds 80.0%, otherwise {@code false}
         */
        public boolean isEligible() {
            return adjustedPercentage >= 80.0;
        }
    }

    /**
     * Data Transfer Object aggregating student-wide attendance key performance indicators for metric strip views.
     */
    public static class AttendanceStripDTO {
        public double overallPercentage = 0.0;
        public int eligibleModulesCount = 0;
        public int totalModulesCount = 0;
        public int totalAttendedHours = 0;
        public int totalScheduledHours = 0;
        public int approvedMedicalsCount = 0;
    }

    /**
     * Queries the attendance summary database view for all enrolled courses assigned to a student,
     * ordering results lexicographically by course code and enriching each record with component hours breakdown.
     *
     * @param studentId Authenticated student primary key identifier
     * @return List of populated {@link AttendanceRecordDTO} instances
     */
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

                    // Hydrate component-level theory and practical scheduled/attended hours
                    loadHoursBreakdown(studentId, dto.courseId, dto);
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Computes scheduled and attended hours partitioned by session component ('Theory' or 'Practical'),
     * assuming standard 2-hour duration blocks per lecture session slot.
     *
     * @param studentId Authenticated student primary key identifier
     * @param courseId  Specific course primary key identifier
     * @param dto       Target DTO instance to populate with computed hours breakdown
     */
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

    /**
     * Calculates high-level cumulative attendance statistics across all enrolled modules for a student,
     * including average percentage, eligibility tallies, cumulative contact hours, and medical certificates count.
     *
     * @param studentId Authenticated student primary key identifier
     * @return Fully populated {@link AttendanceStripDTO} containing aggregated metrics
     */
    public static AttendanceStripDTO getAttendanceStripMetrics(long studentId) {
        AttendanceStripDTO strip = new AttendanceStripDTO();
        List<AttendanceRecordDTO> records = getStudentAttendanceDirectory(studentId);

        if (records.isEmpty()) return strip;

        strip.totalModulesCount = records.size();
        double sumPct = 0;

        // Aggregate statistics across each registered module record
        for (AttendanceRecordDTO r : records) {
            sumPct += r.adjustedPercentage;
            if (r.isEligible()) strip.eligibleModulesCount++;
            strip.totalAttendedHours += (r.attendedTheoryHours + r.attendedPracticalHours);
            strip.totalScheduledHours += (r.totalTheoryHours + r.totalPracticalHours);
            strip.approvedMedicalsCount += r.medicalSessions;
        }

        // Round aggregate attendance percentage to 1 decimal place
        strip.overallPercentage = Math.round((sumPct / records.size()) * 10.0) / 10.0;
        return strip;
    }
}