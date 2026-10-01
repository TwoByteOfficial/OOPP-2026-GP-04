package model;

public class Admin {

    private Long userId;
    private String adminCode;
    private String designation;

    public Admin() {
    }

    public Admin(Long userId, String adminCode, String designation) {
        this.userId = userId;
        this.adminCode = adminCode;
        this.designation = designation;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}