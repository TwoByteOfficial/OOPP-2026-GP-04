package gui.undergraduate.medicals.model;

/**
 * Data model representing student medical certificate submission matching unimis DB schema.
 */
public class MedicalRecordData {
    private final long medicalId;
    private final String courseCode;
    private final String courseName;
    private final String medicalDate;
    private final String reason;
    private final String documentPath;
    private final String approvalStatus; // Pending, Approved, Rejected
    private final String reviewComment;

    public MedicalRecordData(long medicalId, String courseCode, String courseName,
                             String medicalDate, String reason, String documentPath,
                             String approvalStatus, String reviewComment) {
        this.medicalId = medicalId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.medicalDate = medicalDate;
        this.reason = reason;
        this.documentPath = documentPath;
        this.approvalStatus = approvalStatus;
        this.reviewComment = reviewComment;
    }

    public long getMedicalId() { return medicalId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getMedicalDate() { return medicalDate; }
    public String getReason() { return reason; }
    public String getDocumentPath() { return documentPath; }
    public String getApprovalStatus() { return approvalStatus; }
    public String getReviewComment() { return reviewComment; }
}