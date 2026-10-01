package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Medical {

    private Long medicalId;
    private Long studentId;
    private Integer courseId;
    private Long attendanceRecordId;
    private Date medicalDate;
    private String reason;
    private String documentPath;
    private String approvalStatus;
    private Timestamp submittedAt;
    private Long reviewedBy;
    private java.sql.Timestamp reviewedAt;
    private String reviewComment;

    public Medical() {
    }

    public Medical(Long medicalId, Long studentId, Integer courseId,
                   Long attendanceRecordId, Date medicalDate,
                   String reason, String documentPath,
                   String approvalStatus, Timestamp submittedAt,
                   Long reviewedBy, Timestamp reviewedAt,
                   String reviewComment) {

        this.medicalId = medicalId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceRecordId = attendanceRecordId;
        this.medicalDate = medicalDate;
        this.reason = reason;
        this.documentPath = documentPath;
        this.approvalStatus = approvalStatus;
        this.submittedAt = submittedAt;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.reviewComment = reviewComment;
    }

    public Long getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(Long medicalId) {
        this.medicalId = medicalId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Long getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public void setAttendanceRecordId(Long attendanceRecordId) {
        this.attendanceRecordId = attendanceRecordId;
    }

    public Date getMedicalDate() {
        return medicalDate;
    }

    public void setMedicalDate(Date medicalDate) {
        this.medicalDate = medicalDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}