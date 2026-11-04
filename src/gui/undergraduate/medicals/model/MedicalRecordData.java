package gui.undergraduate.medicals.model;

/**
 * Data model representing student medical certificate submission matching unimis DB schema.
 * Encapsulates submitted certificate metadata, associated course details, absence date,
 * supporting document cloud path, and administrative approval workflow states.
 */
public class MedicalRecordData {

    // Unique medical record database identifier
    private final long medicalId;

    // Associated course module identifiers
    private final String courseCode;
    private final String courseName;

    // Date of absence for the excused lecture session
    private final String medicalDate;

    // Student's diagnosis or medical explanation
    private final String reason;

    // Cloud storage URL (e.g., Google Drive) pointing to certified medical documentation
    private final String documentPath;

    // Review status of the application (e.g., Pending, Approved, Rejected) and reviewer remarks
    private final String approvalStatus; // Pending, Approved, Rejected
    private final String reviewComment;

    /**
     * Constructs an immutable MedicalRecordData transfer object.
     *
     * @param medicalId      Unique primary key identifier in database
     * @param courseCode     Code of the absent course module
     * @param courseName     Title of the absent course module
     * @param medicalDate    Date of the lecture session missed
     * @param reason         Medical reason or diagnosis description
     * @param documentPath   Certified cloud link/path to certificate
     * @param approvalStatus Administrative review status (Pending, Approved, Rejected)
     * @param reviewComment  Remarks provided by the reviewing officer
     */
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

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

    public long getMedicalId() { return medicalId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getMedicalDate() { return medicalDate; }
    public String getReason() { return reason; }
    public String getDocumentPath() { return documentPath; }
    public String getApprovalStatus() { return approvalStatus; }
    public String getReviewComment() { return reviewComment; }
}