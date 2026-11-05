package gui.undergraduate.enrollment.model;

/**
 * Immutable Data Transfer Object representing course enrollment state.
 * Encapsulates curriculum course metadata, credit structure, assessment weight allocations,
 * and the student's active enrollment or request status.
 */
public class EnrollmentCourseData {

    // Unique database course identifier
    private final int courseId;

    // Course code identifier (e.g., "ICT2132") and descriptive title
    private final String courseCode;
    private final String courseName;
    private final String description;

    // Academic credit structure and GPA contribution flag
    private final double totalCredits;
    private final double theoryCredits;
    private final double practicalCredits;
    private final boolean isGpa;

    // Evaluation weight allocations (Continuous Assessment vs. Final Examination)
    private final double caWeight;
    private final double endWeight;

    // Academic schedule periods
    private final String semester;
    private final String academicYear;

    // Student enrollment status (e.g., "ENROLLED", "PENDING", or null/available) and review notes
    private final String enrollmentStatus;
    private final String requestNote;

    /**
     * Constructs an immutable enrollment course data model instance.
     */
    public EnrollmentCourseData(int courseId, String courseCode, String courseName, String description,
                                double totalCredits, double theoryCredits, double practicalCredits,
                                boolean isGpa, double caWeight, double endWeight,
                                String semester, String academicYear,
                                String enrollmentStatus, String requestNote) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.totalCredits = totalCredits;
        this.theoryCredits = theoryCredits;
        this.practicalCredits = practicalCredits;
        this.isGpa = isGpa;
        this.caWeight = caWeight;
        this.endWeight = endWeight;
        this.semester = semester;
        this.academicYear = academicYear;
        this.enrollmentStatus = enrollmentStatus;
        this.requestNote = requestNote;
    }

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getDescription() { return description; }
    public double getTotalCredits() { return totalCredits; }
    public double getTheoryCredits() { return theoryCredits; }
    public double getPracticalCredits() { return practicalCredits; }
    public boolean isGpa() { return isGpa; }
    public double getCaWeight() { return caWeight; }
    public double getEndWeight() { return endWeight; }
    public String getSemester() { return semester; }
    public String getAcademicYear() { return academicYear; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public String getRequestNote() { return requestNote; }
}