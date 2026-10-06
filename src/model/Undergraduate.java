package model;

import java.sql.Date;
import java.math.BigDecimal;

public class Undergraduate extends User {
    private String registrationNo;
    private String indexNo;
    private int batchYear;
    private String academicYear;
    private String studentType;
    private String intake;
    private int currentLevel;
    private int currentSemester;
    private BigDecimal previousCredits;
    private BigDecimal previousGradePoints;
    private Date admissionDate;
    private String guardianName;
    private String guardianContact;

    public Undergraduate() {
        setRole("Undergraduate");
    }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    public String getIndexNo() { return indexNo; }
    public void setIndexNo(String indexNo) { this.indexNo = indexNo; }
    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }
    public String getIntake() { return intake; }
    public void setIntake(String intake) { this.intake = intake; }
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    public int getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }
    public BigDecimal getPreviousCredits() { return previousCredits; }
    public void setPreviousCredits(BigDecimal previousCredits) { this.previousCredits = previousCredits; }
    public BigDecimal getPreviousGradePoints() { return previousGradePoints; }
    public void setPreviousGradePoints(BigDecimal previousGradePoints) { this.previousGradePoints = previousGradePoints; }
    public Date getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(Date admissionDate) { this.admissionDate = admissionDate; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianContact() { return guardianContact; }
    public void setGuardianContact(String guardianContact) { this.guardianContact = guardianContact; }
}
