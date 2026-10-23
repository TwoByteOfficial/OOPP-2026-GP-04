package gui.undergraduate.courses.model;

/**
 * Data model representing course details and its assessment breakdown.
 */
public class CourseEvaluationData {
    private final String courseName;
    private final String lecturer;
    private final String department;
    private final int credits;
    private final int quiz;
    private final int assignment;
    private final int midExam;
    private final int endExam;

    public CourseEvaluationData(String courseName, String lecturer, String department,
                                int credits, int quiz, int assignment, int midExam, int endExam) {
        this.courseName = courseName;
        this.lecturer = lecturer;
        this.department = department;
        this.credits = credits;
        this.quiz = quiz;
        this.assignment = assignment;
        this.midExam = midExam;
        this.endExam = endExam;
    }

    public String getCourseName() { return courseName; }
    public String getLecturer() { return lecturer; }
    public String getDepartment() { return department; }
    public int getCredits() { return credits; }
    public int getQuiz() { return quiz; }
    public int getAssignment() { return assignment; }
    public int getMidExam() { return midExam; }
    public int getEndExam() { return endExam; }
}