package model;

import java.math.BigDecimal;

public class EligibilityResult {
    private long studentId;
    private String registrationNo;
    private String fullName;
    private int courseId;
    private String courseCode;
    private String courseName;
    private BigDecimal attendancePercent;
    private BigDecimal caMark;
    private String finalExamEligibility;
    private String attendanceRequirement;
    private String caRequirement;

    public EligibilityResult() {}

    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public BigDecimal getAttendancePercent() { return attendancePercent; }
    public void setAttendancePercent(BigDecimal attendancePercent) { this.attendancePercent = attendancePercent; }
    public BigDecimal getCaMark() { return caMark; }
    public void setCaMark(BigDecimal caMark) { this.caMark = caMark; }
    public String getFinalExamEligibility() { return finalExamEligibility; }
    public void setFinalExamEligibility(String finalExamEligibility) { this.finalExamEligibility = finalExamEligibility; }
    public String getAttendanceRequirement() { return attendanceRequirement; }
    public void setAttendanceRequirement(String attendanceRequirement) { this.attendanceRequirement = attendanceRequirement; }
    public String getCaRequirement() { return caRequirement; }
    public void setCaRequirement(String caRequirement) { this.caRequirement = caRequirement; }
}
