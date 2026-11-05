package dao.undergraduate;

import util.DBConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Undergraduate Medical submissions and records.
 * Directly mapped to attendance_sessions, attendance_records, and medicals tables.
 */
public class UndergraduateMedicalDAO {

    public static class MedicalRecordDTO {
        public long medicalId;
        public String courseCode;
        public String courseName;
        public String medicalDate;
        public String reason;
        public String documentUrl;
        public String approvalStatus;
        public String reviewComment;
    }

    public static class MedicalMetricsDTO {
        public int totalCount = 0;
        public int approvedCount = 0;
        public int pendingCount = 0;
        public int rejectedCount = 0;
    }

    public static List<MedicalRecordDTO> getMedicalHistory(long studentId) {
        List<MedicalRecordDTO> list = new ArrayList<>();
        String sql = "SELECT m.medical_id, c.course_code, c.course_name, m.medical_date, " +
                "m.reason, m.document_path, m.approval_status, m.review_comment " +
                "FROM medicals m " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "WHERE m.student_id = ? " +
                "ORDER BY m.medical_id DESC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MedicalRecordDTO dto = new MedicalRecordDTO();
                    dto.medicalId = rs.getLong("medical_id");
                    dto.courseCode = rs.getString("course_code");
                    dto.courseName = rs.getString("course_name");
                    dto.medicalDate = rs.getString("medical_date");
                    dto.reason = rs.getString("reason");
                    dto.documentUrl = rs.getString("document_path");
                    dto.approvalStatus = rs.getString("approval_status");
                    dto.reviewComment = rs.getString("review_comment");
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static MedicalMetricsDTO getMedicalMetrics(long studentId) {
        MedicalMetricsDTO dto = new MedicalMetricsDTO();
        String sql = "SELECT COUNT(*) AS total, " +
                "SUM(CASE WHEN approval_status = 'Approved' THEN 1 ELSE 0 END) AS approved, " +
                "SUM(CASE WHEN approval_status = 'Pending' THEN 1 ELSE 0 END) AS pending, " +
                "SUM(CASE WHEN approval_status = 'Rejected' THEN 1 ELSE 0 END) AS rejected " +
                "FROM medicals WHERE student_id = ?";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.totalCount = rs.getInt("total");
                    dto.approvedCount = rs.getInt("approved");
                    dto.pendingCount = rs.getInt("pending");
                    dto.rejectedCount = rs.getInt("rejected");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    /**
     * Submits a medical excuse. Validates that the absence date is within 14 days.
     */
    public static boolean submitMedical(long studentId, int courseId, String absenceDate, String reason, String documentUrl) {
        try {
            LocalDate absDate = LocalDate.parse(absenceDate);
            LocalDate today = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(absDate, today);

            // දින 14 සීමාව ඉක්මවා ඇත්නම් හෝ අනාගත දිනයක් නම් ප්‍රතික්ෂේප කරයි
            if (daysBetween > 14 || daysBetween < 0) {
                System.err.println("Validation Error: Absence date must be within the past 14 days.");
                return false;
            }
        } catch (Exception e) {
            // Ignore date parsing fallback
        }

        Long attendanceRecordId = null;

        String lookupSql = "SELECT ar.attendance_record_id " +
                "FROM attendance_records ar " +
                "JOIN attendance_sessions ats ON ats.attendance_session_id = ar.attendance_session_id " +
                "WHERE ats.course_id = ? AND ar.student_id = ? AND ats.session_date = ? " +
                "LIMIT 1";

        try (PreparedStatement psLookup = DBConnection.UG_CON.prepareStatement(lookupSql)) {
            psLookup.setInt(1, courseId);
            psLookup.setLong(2, studentId);
            psLookup.setDate(3, Date.valueOf(absenceDate));
            try (ResultSet rs = psLookup.executeQuery()) {
                if (rs.next()) {
                    attendanceRecordId = rs.getLong("attendance_record_id");
                }
            }
        } catch (Exception ignored) {}

        String sql = "INSERT INTO medicals (student_id, course_id, attendance_record_id, medical_date, reason, document_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setInt(2, courseId);
            if (attendanceRecordId != null) {
                ps.setLong(3, attendanceRecordId);
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            ps.setDate(4, Date.valueOf(absenceDate));
            ps.setString(5, reason);
            ps.setString(6, documentUrl);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Overloaded method supporting Medical Date Range (Start Date & End Date).
     * Validates that the total range does not exceed 14 days.
     */
    public static boolean submitMedicalWithRange(long studentId, int courseId, String absenceDate, String startDate, String endDate, String reason, String documentUrl) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            long duration = ChronoUnit.DAYS.between(start, end) + 1;

            if (duration > 14) {
                System.err.println("Validation Error: Medical date range exceeds 14 days limit.");
                return false;
            }
            if (end.isBefore(start)) {
                System.err.println("Validation Error: End date cannot be before start date.");
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        String enrichedReason = reason + " [Period: " + startDate + " to " + endDate + "]";
        return submitMedical(studentId, courseId, absenceDate, enrichedReason, documentUrl);
    }

    /**
     * Retrieves eligible lecture dates strictly within the last 14 days (2 weeks).
     */
    public static List<String> getCourseLectureDates(long studentId, int courseId) {
        List<String> dates = new ArrayList<>();
        LocalDate twoWeeksAgo = LocalDate.now().minusDays(14);
        LocalDate today = LocalDate.now();

        // -------------------------------------------------------------
        // Priority 1: Fetch ABSENT session dates strictly within the past 14 days
        // -------------------------------------------------------------
        String absentQuery = "SELECT DISTINCT ats.session_date " +
                "FROM attendance_sessions ats " +
                "JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id " +
                "WHERE ats.course_id = ? AND ar.student_id = ? AND ar.attendance_status = 'ABSENT' " +
                "AND ats.session_date >= ? AND ats.session_date <= ? " +
                "ORDER BY ats.session_date DESC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(absentQuery)) {
            ps.setInt(1, courseId);
            ps.setLong(2, studentId);
            ps.setDate(3, Date.valueOf(twoWeeksAgo));
            ps.setDate(4, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String d = rs.getString("session_date");
                    if (d != null && !d.trim().isEmpty() && !dates.contains(d.trim())) {
                        dates.add(d.trim());
                    }
                }
            }
        } catch (Exception ignored) {}

        // -------------------------------------------------------------
        // Priority 2: If no absent records found, fetch session dates within the past 14 days
        // -------------------------------------------------------------
        if (dates.isEmpty()) {
            String allSessionsQuery = "SELECT DISTINCT session_date " +
                    "FROM attendance_sessions " +
                    "WHERE course_id = ? AND session_date >= ? AND session_date <= ? " +
                    "ORDER BY session_date DESC";

            try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(allSessionsQuery)) {
                ps.setInt(1, courseId);
                ps.setDate(2, Date.valueOf(twoWeeksAgo));
                ps.setDate(3, Date.valueOf(today));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String d = rs.getString("session_date");
                        if (d != null && !d.trim().isEmpty() && !dates.contains(d.trim())) {
                            dates.add(d.trim());
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        // -------------------------------------------------------------
        // Priority 3: Fallback (Eligible dates within the 14-day window)
        // -------------------------------------------------------------
        if (dates.isEmpty()) {
            dates.add(today.minusDays(2).toString());
            dates.add(today.minusDays(7).toString());
        }

        return dates;
    }
}