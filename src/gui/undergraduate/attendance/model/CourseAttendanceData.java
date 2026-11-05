package gui.undergraduate.attendance.model;

/**
 * Data model representing student attendance records and examination eligibility status for a course.
 * Encapsulates attendance counts, medical adjustments, and mathematical calculation logic.
 */
public class CourseAttendanceData {

    // Immutable course identity and session count attributes
    private final String courseCode;
    private final String courseName;
    private final int totalLectures;
    private final int attendedLectures;
    private final int totalPracticals;
    private final int attendedPracticals;
    private final int approvedMedicals;

    /**
     * Parameterized constructor initializing attendance metrics and medical approvals.
     */
    public CourseAttendanceData(String courseCode, String courseName, int totalLectures,
                                int attendedLectures, int totalPracticals, int attendedPracticals,
                                int approvedMedicals) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalLectures = totalLectures;
        this.attendedLectures = attendedLectures;
        this.totalPracticals = totalPracticals;
        this.attendedPracticals = attendedPracticals;
        this.approvedMedicals = approvedMedicals;
    }

    // -------------------------------------------------------------
    // Getter Methods (Encapsulation)
    // -------------------------------------------------------------
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getTotalLectures() { return totalLectures; }
    public int getAttendedLectures() { return attendedLectures; }
    public int getTotalPracticals() { return totalPracticals; }
    public int getAttendedPracticals() { return attendedPracticals; }
    public int getApprovedMedicals() { return approvedMedicals; }

    // -------------------------------------------------------------
    // Business Logic & Percentage Calculations
    // -------------------------------------------------------------

    /**
     * Calculates theory attendance percentage factoring in approved medical covers.
     * Returns 100.0% if no theory lectures were scheduled to prevent division by zero.
     */
    public double getTheoryPercentage() {
        if (totalLectures == 0) return 100.0;
        return ((double) (attendedLectures + approvedMedicals) / totalLectures) * 100.0;
    }

    /**
     * Calculates practical lab attendance percentage.
     * Returns 100.0% if the course contains no practical component.
     */
    public double getPracticalPercentage() {
        if (totalPracticals == 0) return 100.0;
        return ((double) attendedPracticals / totalPracticals) * 100.0;
    }

    /**
     * Calculates overall aggregate attendance percentage across all session types.
     * Combines physical attendance with approved medical concessions against total sessions.
     */
    public double getOverallPercentage() {
        int totalSessions = totalLectures + totalPracticals;
        if (totalSessions == 0) return 100.0;
        return ((double) (attendedLectures + attendedPracticals + approvedMedicals) / totalSessions) * 100.0;
    }

    /**
     * Evaluates exam eligibility against faculty regulations (80% minimum attendance threshold).
     */
    public boolean isEligible() {
        return getOverallPercentage() >= 80.0;
    }
}