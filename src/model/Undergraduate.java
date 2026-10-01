package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Undergraduate {

    private Long userId;
    private String registrationNo;
    private String indexNo;
    private Integer batchYear;
    private String academicYear;
    private String studentType;
    private String intake;
    private Integer currentLevel;
    private Integer currentSemester;
    private BigDecimal previousCredits;
    private BigDecimal previousGradePoints;
    private Date admissionDate;
    private String guardianName;
    private String guardianContact;

    public Undergraduate() {
    }

    public Undergraduate(Long userId, String registrationNo, String indexNo,
                         Integer batchYear, String academicYear,
                         String studentType, String intake,
                         Integer currentLevel, Integer currentSemester,
                         BigDecimal previousCredits,
                         BigDecimal previousGradePoints,
                         Date admissionDate, String guardianName,
                         String guardianContact) {

        this.userId = userId;
        this.registrationNo = registrationNo;
        this.indexNo = indexNo;
        this.batchYear = batchYear;
        this.academicYear = academicYear;
        this.studentType = studentType;
        this.intake = intake;
        this.currentLevel = currentLevel;
        this.currentSemester = currentSemester;
        this.previousCredits = previousCredits;
        this.previousGradePoints = previousGradePoints;
        this.admissionDate = admissionDate;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public Integer getBatchYear() {
        return batchYear;
    }

    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(Integer currentSemester) {
        this.currentSemester = currentSemester;
    }

    public BigDecimal getPreviousCredits() {
        return previousCredits;
    }

    public void setPreviousCredits(BigDecimal previousCredits) {
        this.previousCredits = previousCredits;
    }

    public BigDecimal getPreviousGradePoints() {
        return previousGradePoints;
    }

    public void setPreviousGradePoints(BigDecimal previousGradePoints) {
        this.previousGradePoints = previousGradePoints;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianContact() {
        return guardianContact;
    }

    public void setGuardianContact(String guardianContact) {
        this.guardianContact = guardianContact;
    }
}