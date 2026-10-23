package gui.undergraduate.attendance.model;

/**
 * Data model representing student attendance records and examination eligibility status for a course.
 */
public class CourseAttendanceData {
    private final String courseCode;
    private final String courseName;
    private final int totalLectures;
    private final int attendedLectures;
    private final int totalPracticals;
    private final int attendedPracticals;
    private final int approvedMedicals;

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

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getTotalLectures() { return totalLectures; }
    public int getAttendedLectures() { return attendedLectures; }
    public int getTotalPracticals() { return totalPracticals; }
    public int getAttendedPracticals() { return attendedPracticals; }
    public int getApprovedMedicals() { return approvedMedicals; }

    public double getTheoryPercentage() {
        if (totalLectures == 0) return 100.0;
        return ((double) (attendedLectures + approvedMedicals) / totalLectures) * 100.0;
    }

    public double getPracticalPercentage() {
        if (totalPracticals == 0) return 100.0;
        return ((double) attendedPracticals / totalPracticals) * 100.0;
    }

    public double getOverallPercentage() {
        int totalSessions = totalLectures + totalPracticals;
        if (totalSessions == 0) return 100.0;
        return ((double) (attendedLectures + attendedPracticals + approvedMedicals) / totalSessions) * 100.0;
    }

    public boolean isEligible() {
        return getOverallPercentage() >= 80.0;
    }
}