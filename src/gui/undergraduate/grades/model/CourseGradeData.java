package gui.undergraduate.grades.model;

/**
 * Data model representing course assessment score, letter grade, and grade point value (GPV).
 * Encapsulates evaluated course identifiers, credit values, final letter grades,
 * numeric grade points, and academic completion statuses (e.g., Completed, Pending, Repeat).
 */
public class CourseGradeData {

    // Course module identification
    private final String courseCode;
    private final String courseName;

    // Academic credit weight
    private final int credits;

    // Academic evaluation metrics
    private final String grade;
    private final double gpv;
    private final String status; // Completed, Pending, Repeat

    /**
     * Constructs an immutable CourseGradeData record.
     */
    public CourseGradeData(String courseCode, String courseName, int credits, String grade, double gpv, String status) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.grade = grade;
        this.gpv = gpv;
        this.status = status;
    }

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getGrade() { return grade; }
    public double getGpv() { return gpv; }
    public String getStatus() { return status; }
}