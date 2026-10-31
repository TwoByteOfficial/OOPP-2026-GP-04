
package dao.TechnicalOfficer;

import model.Medical;
import util.DBConnection;
import java.util.LinkedHashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public List<Medical> getAllMedicalRecords() throws SQLException {

        List<Medical> medicalRecords = new ArrayList<>();

        String sql = "SELECT m.*, u.full_name, ug.registration_no, c.course_code " +
                "FROM medicals m " +
                "JOIN undergraduates ug ON m.student_id = ug.user_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "ORDER BY m.submitted_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Medical medical = new Medical();

                medical.setMedicalId(rs.getLong("medical_id"));
                medical.setStudentId(rs.getLong("student_id"));
                medical.setCourseId(rs.getInt("course_id"));

                medical.setRegistrationNo(rs.getString("registration_no"));
                medical.setStudentName(rs.getString("full_name"));
                medical.setCourseCode(rs.getString("course_code"));

                medical.setAttendanceRecordId(
                        rs.getObject("attendance_record_id", Long.class));
                medical.setMedicalDate(rs.getDate("medical_date"));
                medical.setReason(rs.getString("reason"));
                medical.setDocumentPath(rs.getString("document_path"));
                medical.setApprovalStatus(rs.getString("approval_status"));
                medical.setSubmittedAt(rs.getTimestamp("submitted_at"));

                long reviewedBy = rs.getLong("reviewed_by");
                medical.setReviewedBy(
                        rs.wasNull() ? null : reviewedBy);

                medical.setReviewedAt(rs.getTimestamp("reviewed_at"));
                medical.setReviewComment(rs.getString("review_comment"));

                medicalRecords.add(medical);
            }
        }

        return medicalRecords;
    }



    public List<Medical> getMedicalRecordsByStatus(String status)
            throws SQLException {

        if (status.equals("All")) {
            return getAllMedicalRecords();
        }

        List<Medical> medicalRecords = new ArrayList<>();

        String sql = "SELECT m.*, u.full_name, ug.registration_no, c.course_code " +
                "FROM medicals m " +
                "JOIN undergraduates ug ON m.student_id = ug.user_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "WHERE m.approval_status = ? " +
                "ORDER BY m.submitted_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medical medical = new Medical();

                    medical.setMedicalId(rs.getLong("medical_id"));
                    medical.setStudentId(rs.getLong("student_id"));
                    medical.setCourseId(rs.getInt("course_id"));

                    medical.setRegistrationNo(rs.getString("registration_no"));
                    medical.setStudentName(rs.getString("full_name"));
                    medical.setCourseCode(rs.getString("course_code"));

                    medical.setAttendanceRecordId(
                            rs.getObject("attendance_record_id", Long.class));
                    medical.setMedicalDate(rs.getDate("medical_date"));
                    medical.setReason(rs.getString("reason"));
                    medical.setDocumentPath(rs.getString("document_path"));
                    medical.setApprovalStatus(rs.getString("approval_status"));
                    medical.setSubmittedAt(rs.getTimestamp("submitted_at"));

                    long reviewedBy = rs.getLong("reviewed_by");
                    medical.setReviewedBy(rs.wasNull() ? null : reviewedBy);

                    medical.setReviewedAt(rs.getTimestamp("reviewed_at"));
                    medical.setReviewComment(rs.getString("review_comment"));

                    medicalRecords.add(medical);
                }
            }
        }

        return medicalRecords;
    }





    public List<Medical> searchMedicalRecords(
            String status, int courseId, String searchText) throws SQLException {

        List<Medical> medicalRecords = new ArrayList<>();

        String sql = "SELECT m.*, u.full_name, ug.registration_no, " +
                "c.course_code, c.course_name " +
                "FROM medicals m " +
                "JOIN undergraduates ug ON m.student_id = ug.user_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "WHERE (? = 'All' OR m.approval_status = ?) " +
                "AND (? = 0 OR m.course_id = ?) " +
                "AND (ug.registration_no LIKE ? " +
                "OR u.full_name LIKE ? " +
                "OR c.course_code LIKE ?) " +
                "ORDER BY m.submitted_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchText.trim() + "%";

            stmt.setString(1, status);
            stmt.setString(2, status);
            stmt.setInt(3, courseId);
            stmt.setInt(4, courseId);
            stmt.setString(5, searchPattern);
            stmt.setString(6, searchPattern);
            stmt.setString(7, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medical medical = new Medical();

                    medical.setMedicalId(rs.getLong("medical_id"));
                    medical.setStudentId(rs.getLong("student_id"));
                    medical.setCourseId(rs.getInt("course_id"));

                    medical.setRegistrationNo(rs.getString("registration_no"));
                    medical.setStudentName(rs.getString("full_name"));
                    medical.setCourseCode(rs.getString("course_code"));
                    medical.setCourseName(rs.getString("course_name"));

                    medical.setAttendanceRecordId(
                            rs.getObject("attendance_record_id", Long.class));
                    medical.setMedicalDate(rs.getDate("medical_date"));
                    medical.setReason(rs.getString("reason"));
                    medical.setDocumentPath(rs.getString("document_path"));
                    medical.setApprovalStatus(rs.getString("approval_status"));
                    medical.setSubmittedAt(rs.getTimestamp("submitted_at"));

                    long reviewedBy = rs.getLong("reviewed_by");
                    medical.setReviewedBy(rs.wasNull() ? null : reviewedBy);

                    medical.setReviewedAt(rs.getTimestamp("reviewed_at"));
                    medical.setReviewComment(rs.getString("review_comment"));

                    medicalRecords.add(medical);
                }
            }
        }

        return medicalRecords;
    }









    public Map<String, Integer> getAllCourses() throws SQLException {

        Map<String, Integer> courses = new LinkedHashMap<>();

        String sql = "SELECT course_id, course_code, course_name " +
                "FROM courses ORDER BY course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseDisplay =
                        rs.getString("course_code") + " - " +
                                rs.getString("course_name");

                courses.put(
                        courseDisplay,
                        rs.getInt("course_id")
                );
            }
        }

        return courses;
    }











}
