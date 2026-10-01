package model;

public class TechnicalOfficer {

    private Long userId;
    private String technicalOfficerCode;
    private String designation;

    public TechnicalOfficer() {
    }

    public TechnicalOfficer(Long userId, String technicalOfficerCode,
                            String designation) {
        this.userId = userId;
        this.technicalOfficerCode = technicalOfficerCode;
        this.designation = designation;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTechnicalOfficerCode() {
        return technicalOfficerCode;
    }

    public void setTechnicalOfficerCode(String technicalOfficerCode) {
        this.technicalOfficerCode = technicalOfficerCode;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}