package model;

import java.math.BigDecimal;

public class StudentGPA {
    private long studentId;
    private String registrationNo;
    private String fullName;
    private BigDecimal previousCredits;
    private BigDecimal previousGradePoints;
    private BigDecimal currentSgpa;
    private BigDecimal semesterCredits;
    private BigDecimal totalGradePoints;
    private BigDecimal cgpa;

    public StudentGPA() {}

    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public BigDecimal getPreviousCredits() { return previousCredits; }
    public void setPreviousCredits(BigDecimal previousCredits) { this.previousCredits = previousCredits; }
    public BigDecimal getPreviousGradePoints() { return previousGradePoints; }
    public void setPreviousGradePoints(BigDecimal previousGradePoints) { this.previousGradePoints = previousGradePoints; }
    public BigDecimal getCurrentSgpa() { return currentSgpa; }
    public void setCurrentSgpa(BigDecimal currentSgpa) { this.currentSgpa = currentSgpa; }
    public BigDecimal getSemesterCredits() { return semesterCredits; }
    public void setSemesterCredits(BigDecimal semesterCredits) { this.semesterCredits = semesterCredits; }
    public BigDecimal getTotalGradePoints() { return totalGradePoints; }
    public void setTotalGradePoints(BigDecimal totalGradePoints) { this.totalGradePoints = totalGradePoints; }
    public BigDecimal getCgpa() { return cgpa; }
    public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
}
