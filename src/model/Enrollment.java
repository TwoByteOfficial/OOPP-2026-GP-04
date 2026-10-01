package model;

import java.sql.Timestamp;

public class Enrollment {

    private Long enrollmentId;
    private Long studentId;
    private Integer courseId;
    private String academicYear;
    private String semester;
    private String enrollmentType;
    private String enrollmentStatus;
    private Timestamp enrolledAt;

    public Enrollment() {
    }

    public Enrollment(Long enrollmentId, Long studentId,
                      Integer courseId, String academicYear,
                      String semester, String enrollmentType,
                      String enrollmentStatus, Timestamp enrolledAt) {

        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.enrollmentType = enrollmentType;
        this.enrollmentStatus = enrollmentStatus;
        this.enrolledAt = enrolledAt;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public Timestamp getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Timestamp enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}