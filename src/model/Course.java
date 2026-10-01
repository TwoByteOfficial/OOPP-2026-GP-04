package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Course {

    private Integer courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private BigDecimal theoryCredits;
    private BigDecimal practicalCredits;
    private BigDecimal totalCredits;
    private BigDecimal caWeight;
    private BigDecimal finalWeight;
    private Integer departmentId;
    private String academicYear;
    private String semester;
    private String status;
    private Timestamp createdAt;

    public Course() {
    }

    public Course(Integer courseId, String courseCode, String courseName,
                  String description, BigDecimal theoryCredits,
                  BigDecimal practicalCredits, BigDecimal totalCredits,
                  BigDecimal caWeight, BigDecimal finalWeight,
                  Integer departmentId, String academicYear,
                  String semester, String status, Timestamp createdAt) {

        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.theoryCredits = theoryCredits;
        this.practicalCredits = practicalCredits;
        this.totalCredits = totalCredits;
        this.caWeight = caWeight;
        this.finalWeight = finalWeight;
        this.departmentId = departmentId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTheoryCredits() {
        return theoryCredits;
    }

    public void setTheoryCredits(BigDecimal theoryCredits) {
        this.theoryCredits = theoryCredits;
    }

    public BigDecimal getPracticalCredits() {
        return practicalCredits;
    }

    public void setPracticalCredits(BigDecimal practicalCredits) {
        this.practicalCredits = practicalCredits;
    }

    public BigDecimal getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(BigDecimal totalCredits) {
        this.totalCredits = totalCredits;
    }

    public BigDecimal getCaWeight() {
        return caWeight;
    }

    public void setCaWeight(BigDecimal caWeight) {
        this.caWeight = caWeight;
    }

    public BigDecimal getFinalWeight() {
        return finalWeight;
    }

    public void setFinalWeight(BigDecimal finalWeight) {
        this.finalWeight = finalWeight;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}