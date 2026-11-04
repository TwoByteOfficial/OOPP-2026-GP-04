package dao.undergraduate;

import util.DBConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Undergraduate Medical submissions and records.
 * Directly mapped to attendance_sessions, attendance_records, and medicals tables.
 * Provides capabilities to query past medical submissions, calculate summary statistics,
 * link absence records for automatic attendance adjustment upon administrative approval,
 * and populate eligible absence dates for enrolled course modules.
 */
public class UndergraduateMedicalDAO {

    /**
     * Data Transfer Object representing an individual medical excuse submission,
     * including associated course details, documentary evidence link, and review remarks.
     */
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

    /**
     * Data Transfer Object aggregating total, approved, pending, and rejected medical submission counts.
     */
    public static class MedicalMetricsDTO {
        public int totalCount = 0;
        public int approvedCount = 0;
        public int pendingCount = 0;
        public int rejectedCount = 0;
    }

    /**
     * Retrieves the complete medical excuse submission history for a specific student,
     * joined with course metadata and sorted by submission ID in descending order.
     *
     * @param studentId Authenticated student primary key identifier
     * @return List of {@link MedicalRecordDTO} instances representing submitted medical records
     */
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

    /**
     * Computes aggregate summary metrics for medical certificates submitted by a student,
     * categorizing totals by approval states ('Approved', 'Pending', 'Rejected').
     *
     * @param studentId Authenticated student primary key identifier
     * @return Populated {@link MedicalMetricsDTO} holding status frequency counts
     */
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
     * Submits a medical excuse and links the matching attendance_record_id
     * to ensure attendance percentage updates upon approval.
     * Looks up the corresponding session record for the date and course, then inserts the medical claim with 'Pending' status.
     *
     * @param studentId   Authenticated student primary key identifier
     * @param courseId    Enrolled course identifier
     * @param absenceDate Date of absence in 'YYYY-MM-DD' format
     * @param reason      Explanation or diagnosis notes for the absence
     * @param documentUrl URL or file system path pointing to the uploaded supporting document
     * @return {@code true} if the medical submission was successfully recorded; {@code false} otherwise
     */
    public static boolean submitMedical(long studentId, int courseId, String absenceDate, String reason, String documentUrl) {
        Long attendanceRecordId = null;

        // -------------------------------------------------------------
        // Find matching attendance_record_id for this session date
        // -------------------------------------------------------------
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

        // -------------------------------------------------------------
        // Insert new medical submission record with initial Pending status
        // -------------------------------------------------------------
        String sql = "INSERT INTO medicals (student_id, course_id, attendance_record_id, medical_date, reason, document_path, approval_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";

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
     * Retrieves lecture dates for a specific course module.
     * Prioritizes dates where the student was marked ABSENT.
     * If no absent dates exist, returns all session dates for the course from attendance_sessions.
     * Provides deterministic past dates as fallback if no session records exist in the database.
     *
     * @param studentId Authenticated student primary key identifier
     * @param courseId  Enrolled course primary key identifier
     * @return Ordered list of unique session date strings (YYYY-MM-DD)
     */
    public static List<String> getCourseLectureDates(long studentId, int courseId) {
        List<String> dates = new ArrayList<>();

        // -------------------------------------------------------------
        // Priority 1: Fetch session dates where the student was marked ABSENT
        // -------------------------------------------------------------
        String absentQuery = "SELECT DISTINCT ats.session_date " +
                "FROM attendance_sessions ats " +
                "JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id " +
                "WHERE ats.course_id = ? AND ar.student_id = ? AND ar.attendance_status = 'ABSENT' " +
                "ORDER BY ats.session_date DESC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(absentQuery)) {
            ps.setInt(1, courseId);
            ps.setLong(2, studentId);
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
        // Priority 2: If no absent records found, fetch all conducted session dates for this course
        // -------------------------------------------------------------
        if (dates.isEmpty()) {
            String allSessionsQuery = "SELECT DISTINCT session_date " +
                    "FROM attendance_sessions " +
                    "WHERE course_id = ? " +
                    "ORDER BY session_date DESC";

            try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(allSessionsQuery)) {
                ps.setInt(1, courseId);
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
        // Priority 3: Fallback if course has no sessions recorded yet
        // -------------------------------------------------------------
        if (dates.isEmpty()) {
            int seed = Math.abs(courseId * 41 + 17);
            dates.add(LocalDate.now().minusDays(1 + (seed % 6)).toString());
            dates.add(LocalDate.now().minusDays(8 + (seed % 6)).toString());
        }

        return dates;
    }
}