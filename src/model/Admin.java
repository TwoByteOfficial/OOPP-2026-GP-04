package model;

public class Admin extends User {
    private String adminCode;
    private String designation;

    public Admin() {
        setRole("Admin");
    }

    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
}
