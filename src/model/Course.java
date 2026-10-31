package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private BigDecimal theoryCredits;
    private BigDecimal practicalCredits;
    private BigDecimal totalCredits;
    private Boolean isGpa;

    // CA Sub-weights
    private BigDecimal quizWeight;         // Max Quiz 2 Weight
    private BigDecimal assignmentWeight;   // Assignment Weight (if have)
    private BigDecimal midTheoryWeight;    // Mid Exam Theory Weight (if have)
    private BigDecimal midPracticalWeight; // Mid Exam Practical Weight (if have)
    private BigDecimal caWeight;           // Sum of CA sub-weights

    // End Exam Sub-weights
    private BigDecimal endTheoryWeight;    // End Exam Theory Weight (if have)
    private BigDecimal endPracticalWeight; // End Exam Practical Weight (if have)
    private BigDecimal finalWeight;        // Sum of End Exam sub-weights

    private int departmentId;
    private String academicYear;
    private String semester;
    private String status;
    private Timestamp createdAt;

    public Course() {
        this.theoryCredits = new BigDecimal("2.0");
        this.practicalCredits = new BigDecimal("0.0");
        this.totalCredits = new BigDecimal("2.0");
        this.isGpa = true;
        this.quizWeight = new BigDecimal("10.00");
        this.assignmentWeight = new BigDecimal("10.00");
        this.midTheoryWeight = new BigDecimal("10.00");
        this.midPracticalWeight = new BigDecimal("0.00");
        this.caWeight = new BigDecimal("30.00");
        this.endTheoryWeight = new BigDecimal("70.00");
        this.endPracticalWeight = new BigDecimal("0.00");
        this.finalWeight = new BigDecimal("70.00");
        this.departmentId = 1;
        this.academicYear = "2024/2025";
        this.semester = "Semester I";
        this.status = "Active";
    }

    // Getters and Setters
    public Course(int courseId, String courseCode, String courseName, String description, BigDecimal theoryCredits, BigDecimal practicalCredits, BigDecimal totalCredits, BigDecimal caWeight, BigDecimal finalWeight, int departmentId, String academicYear, String semester, String status, Timestamp createdAt) {
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTheoryCredits() { return theoryCredits; }
    public void setTheoryCredits(BigDecimal theoryCredits) { this.theoryCredits = theoryCredits; }

    public BigDecimal getPracticalCredits() { return practicalCredits; }
    public void setPracticalCredits(BigDecimal practicalCredits) { this.practicalCredits = practicalCredits; }

    public BigDecimal getTotalCredits() { return totalCredits; }
    public void setTotalCredits(BigDecimal totalCredits) { this.totalCredits = totalCredits; }

    public Boolean getIsGpa() { return isGpa; }
    public void setIsGpa(Boolean isGpa) { this.isGpa = isGpa; }

    public BigDecimal getQuizWeight() { return quizWeight; }
    public void setQuizWeight(BigDecimal quizWeight) { this.quizWeight = quizWeight; }

    public BigDecimal getAssignmentWeight() { return assignmentWeight; }
    public void setAssignmentWeight(BigDecimal assignmentWeight) { this.assignmentWeight = assignmentWeight; }

    public BigDecimal getMidTheoryWeight() { return midTheoryWeight; }
    public void setMidTheoryWeight(BigDecimal midTheoryWeight) { this.midTheoryWeight = midTheoryWeight; }

    public BigDecimal getMidPracticalWeight() { return midPracticalWeight; }
    public void setMidPracticalWeight(BigDecimal midPracticalWeight) { this.midPracticalWeight = midPracticalWeight; }

    public BigDecimal getCaWeight() { return caWeight; }
    public void setCaWeight(BigDecimal caWeight) { this.caWeight = caWeight; }

    public BigDecimal getEndTheoryWeight() { return endTheoryWeight; }
    public void setEndTheoryWeight(BigDecimal endTheoryWeight) { this.endTheoryWeight = endTheoryWeight; }

    public BigDecimal getEndPracticalWeight() { return endPracticalWeight; }
    public void setEndPracticalWeight(BigDecimal endPracticalWeight) { this.endPracticalWeight = endPracticalWeight; }

    public BigDecimal getFinalWeight() { return finalWeight; }
    public void setFinalWeight(BigDecimal finalWeight) { this.finalWeight = finalWeight; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}