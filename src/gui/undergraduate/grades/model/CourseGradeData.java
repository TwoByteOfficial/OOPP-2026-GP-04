package gui.undergraduate.grades.model;

/**
 * Data model representing course assessment score, letter grade, and grade point value (GPV).
 */
public class CourseGradeData {
    private final String courseCode;
    private final String courseName;
    private final int credits;
    private final String grade;
    private final double gpv;
    private final String status; // Completed, Pending, Repeat

    public CourseGradeData(String courseCode, String courseName, int credits, String grade, double gpv, String status) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.grade = grade;
        this.gpv = gpv;
        this.status = status;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getGrade() { return grade; }
    public double getGpv() { return gpv; }
    public String getStatus() { return status; }
}