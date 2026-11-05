package dao.TechnicalOfficer;

import model.Medical;
import util.DBConnection;
import java.util.LinkedHashMap;
import java.util.Map;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * [OOP - ABSTRACTION]: Data Access Object for student medical certificate submissions.
 * Encapsulates and abstracts database retrieval, filtering, and JOIN queries between 
 * medicals, undergraduates, users, and courses tables.
 */
public class MedicalDAO {

    /**
     * [OOP - ABSTRACTION]: Retrieves all medical records and automatically constructs 
     * domain model objects from raw database rows.
     *
     * @return List of all Medical records sorted by newest submission first
     * @throws SQLException if a query execution error occurs
     */
    public List<Medical> getAllMedicalRecords() throws SQLException {

        List<Medical> medicalRecords = new ArrayList<>();

        String sql = "SELECT m.*, u.full_name, ug.registration_no, c.course_code " +
                "FROM medicals m " +
                "JOIN undergraduates ug ON m.student_id = ug.user_id " +
                "JOIN users u ON ug.user_id = u.user_id " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "ORDER BY m.submitted_at DESC";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Medical medical = new Medical();

                medical.setMedicalId(rs.getLong("medical_id"));
                medical.setStudentId(rs.getLong("student_id"));
                medical.setCourseId(rs.getInt("course_id"));

                medical.setRegistrationNo(rs.getString("registration_no"));
                medical.setStudentName(rs.getString("full_name"));
                medical.setCourseCode(rs.getString("course_code"));

                medical.setAttendanceRecordId(rs.getObject("attendance_record_id", Long.class));
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

        return medicalRecords;
    }

    /**
     * Retrieves medical records filtered by a specific approval status.
     *
     * @param status The status to filter by ("Pending", "Approved", "Rejected", or "All")
     * @return List of matching Medical records ordered by submission date descending
     * @throws SQLException if a query error occurs
     */
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

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

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

                    medical.setAttendanceRecordId(rs.getObject("attendance_record_id", Long.class));
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

    /**
     * [OOP - ABSTRACTION]: Advanced query abstraction.
     * Searches medical records using multiple criteria simultaneously (status, course, keyword)
     * without exposing SQL LIKE wildcard formatting or JDBC statements to the caller.
     *
     * @param status     Approval status filter ("All", "Pending", "Approved", "Rejected")
     * @param courseId   Course ID filter (or 0 for all courses)
     * @param searchText Keyword to search in registration no, student name, or course code
     * @return List of matching Medical records
     * @throws SQLException if a database query error occurs
     */
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

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

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

                    medical.setAttendanceRecordId(rs.getObject("attendance_record_id", Long.class));
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

    /**
     * Retrieves all courses formatted for dropdown selection.
     * Maps the visual display string (e.g. "ICT101 - Programming") to its course ID integer.
     *
     * @return LinkedHashMap preserving alphabetical order of course codes
     * @throws SQLException if a query execution error occurs
     */
    public Map<String, Integer> getAllCourses() throws SQLException {

        Map<String, Integer> courses = new LinkedHashMap<>();

        String sql = "SELECT course_id, course_code, course_name " +
                "FROM courses ORDER BY course_code";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql);
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
