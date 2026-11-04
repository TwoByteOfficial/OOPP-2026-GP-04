package dao.undergraduate;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Data Access Object for Undergraduate Dashboard summary counters and metrics.
 * Fetches real aggregate analytics directly from MySQL views and tables.
 */
public class UndergraduateDashboardDAO {

    public static class DashboardMetricsDTO {
        public int enrolledCount = 0;
        public double overallAttendance = 0.0;
        public double sgpa = 0.00;
        public double cgpa = 0.00;
        public int approvedMedicals = 0;
        public int pendingMedicals = 0;

        // Breakdown stats
        public int safeAttendanceCount = 0;
        public int borderlineAttendanceCount = 0;
        public int atRiskAttendanceCount = 0;
        public int caBelow40Count = 0;
    }

    public static DashboardMetricsDTO getDashboardMetrics(long studentId) {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();

        // 1. Enrolled Courses Count
        String sqlEnroll = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND enrollment_status = 'Enrolled'";
        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sqlEnroll)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) dto.enrolledCount = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. CGPA & SGPA from Views
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

        // 3. Attendance Distribution & Average from v_attendance_summary
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
                    dto.overallAttendance = Math.round((totalPct / count) * 10.0) / 10.0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Medical Submissions Breakdown
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

        // 5. Continuous Assessments Below 40% from v_ca_summary
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