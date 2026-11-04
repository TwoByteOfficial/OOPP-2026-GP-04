package dao.undergraduate;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Data Access Object for Undergraduate Dashboard summary counters and metrics.
 * Fetches real aggregate analytics directly from MySQL views and tables.
 *
 * Responsibilities:
 * - Computes total actively enrolled modules for the student.
 * - Extracts cumulative GPA (CGPA) and semester GPA (SGPA) from database views.
 * - Categorizes course attendance records into Safe, Borderline, and At-Risk tiers.
 * - Tracks approved vs. pending medical leave certificate submissions.
 * - Identifies continuous assessments (CA) falling below the 40% passing threshold.
 */
public class UndergraduateDashboardDAO {

    /**
     * Data Transfer Object (DTO) holding aggregated key performance indicators
     * and academic status metrics displayed on the undergraduate home dashboard.
     */
    public static class DashboardMetricsDTO {
        // High-level summary metrics
        public int enrolledCount = 0;              // Total number of actively enrolled courses
        public double overallAttendance = 0.0;     // Average attendance percentage across all courses
        public double sgpa = 0.00;                 // Current Semester Grade Point Average
        public double cgpa = 0.00;                 // Cumulative Grade Point Average
        public int approvedMedicals = 0;           // Total approved medical submissions
        public int pendingMedicals = 0;            // Medical submissions awaiting review

        // Attendance eligibility breakdown counters
        public int safeAttendanceCount = 0;        // Courses with attendance > 80% (Safe for exams)
        public int borderlineAttendanceCount = 0;  // Courses with attendance exactly 80%
        public int atRiskAttendanceCount = 0;      // Courses with attendance < 80% (In danger of bar)

        // Academic performance alert counter
        public int caBelow40Count = 0;             // Continuous assessments marked below 40%
    }

    /**
     * Retrieves aggregated academic, attendance, and medical metrics for the specified student.
     * Executes queries against core database tables and pre-computed views.
     *
     * @param studentId The unique identifier (user_id) of the authenticated undergraduate
     * @return Fully populated {@link DashboardMetricsDTO} instance containing all summary values
     */
    public static DashboardMetricsDTO getDashboardMetrics(long studentId) {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();

        // ---------------------------------------------------------------------
        // 1. Enrolled Courses Count
        // Counts all active registrations where status is marked as 'Enrolled'
        // ---------------------------------------------------------------------
        String sqlEnroll = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND enrollment_status = 'Enrolled'";
        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sqlEnroll)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) dto.enrolledCount = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ---------------------------------------------------------------------
        // 2. CGPA & SGPA from Views
        // Queries pre-calculated cumulative and semester GPA from analytical views
        // ---------------------------------------------------------------------
        String sqlGpa = "SELECT c.cgpa, s.sgpa FROM v_cgpa c LEFT JOIN v_sgpa s ON c.student_id = s.student_id WHERE c.student_id = ?";
        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sqlGpa)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.cgpa = rs.getDouble("cgpa");
                    dto.sgpa = rs.getDouble("sgpa");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ---------------------------------------------------------------------
        // 3. Attendance Distribution & Average from v_attendance_summary
        // Iterates through every enrolled course to calculate average attendance
        // and classify courses into Safe (>80%), Borderline (=80%), or At-Risk (<80%)
        // ---------------------------------------------------------------------
        String sqlAtt = "SELECT COALESCE(adjusted_attendance_percent, 0) AS pct FROM v_attendance_summary WHERE student_id = ?";
        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sqlAtt)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                double totalPct = 0;
                int count = 0;
                while (rs.next()) {
                    double p = rs.getDouble("pct");
                    totalPct += p;
                    count++;
                    if (p > 80.0) dto.safeAttendanceCount++;
                    else if (p == 80.0) dto.borderlineAttendanceCount++;
                    else dto.atRiskAttendanceCount++;
                }
                if (count > 0) {
                    // Rounds overall average percentage to 1 decimal place
                    dto.overallAttendance = Math.round((totalPct / count) * 10.0) / 10.0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ---------------------------------------------------------------------
        // 4. Medical Submissions Breakdown
        // Aggregates counts of approved and pending medical excuses
        // ---------------------------------------------------------------------
        String sqlMed = "SELECT " +
                "SUM(CASE WHEN approval_status = 'Approved' THEN 1 ELSE 0 END) AS approved, " +
                "SUM(CASE WHEN approval_status = 'Pending' THEN 1 ELSE 0 END) AS pending " +
                "FROM medicals WHERE student_id = ?";
        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sqlMed)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.approvedMedicals = rs.getInt("approved");
                    dto.pendingMedicals = rs.getInt("pending");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ---------------------------------------------------------------------
        // 5. Continuous Assessments Below 40% from v_ca_summary
        // Counts courses where current CA score is below the mandatory 40% passing mark
        // ---------------------------------------------------------------------
        String sqlCa = "SELECT COUNT(*) FROM v_ca_summary WHERE student_id = ? AND ca_mark < 40.0";
        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sqlCa)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) dto.caBelow40Count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}