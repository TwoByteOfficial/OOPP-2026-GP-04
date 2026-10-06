package model;

import java.sql.Timestamp;

public class Enrollment {
    private long enrollmentId;
    private long studentId;
    private int courseId;
    private String academicYear;
    private String semester;
    private String enrollmentType;
    private String enrollmentStatus;
    private Timestamp enrolledAt;

    public Enrollment() {}

    public long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(long enrollmentId) { this.enrollmentId = enrollmentId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getEnrollmentType() { return enrollmentType; }
    public void setEnrollmentType(String enrollmentType) { this.enrollmentType = enrollmentType; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public Timestamp getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Timestamp enrolledAt) { this.enrolledAt = enrolledAt; }
}
