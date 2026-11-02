package dao.undergraduate;

import util.DBConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Undergraduate Medical submissions and records.
 * Adheres strictly to Zero Local File Handling via cloud link persistence.
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

    public static boolean submitMedical(long studentId, int courseId, String absenceDate, String reason, String documentUrl) {
        String sql = "INSERT INTO medicals (student_id, course_id, medical_date, reason, document_path, approval_status) " +
                "VALUES (?, ?, ?, ?, ?, 'Pending')";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, Date.valueOf(absenceDate));
            ps.setString(4, reason);
            ps.setString(5, documentUrl);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}