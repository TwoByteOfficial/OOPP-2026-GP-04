package model;

public class TechnicalOfficer extends User {
    private String technicalOfficerCode;
    private String designation;
    private String assignedLabs;

    public TechnicalOfficer() {
        setRole("Technical Officer");
    }

    public String getTechnicalOfficerCode() { return technicalOfficerCode; }
    public void setTechnicalOfficerCode(String technicalOfficerCode) { this.technicalOfficerCode = technicalOfficerCode; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getAssignedLabs() { return assignedLabs; }
    public void setAssignedLabs(String assignedLabs) { this.assignedLabs = assignedLabs; }
}
