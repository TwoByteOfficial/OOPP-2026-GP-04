package model;

/**
 * Model representing a Technical Officer in the university system.
 * Extends the base User class with technical officer specific attributes
 * such as staff code, job designation, and assigned computer/hardware laboratories.
 */
public class TechnicalOfficer extends User {

    // Unique staff code assigned to the technical officer (e.g., "TO1001")
    private String technicalOfficerCode;

    // Job designation or title (e.g., "Senior Technical Officer")
    private String designation;

    // Laboratories managed by this officer (e.g., "Network Lab, Electronics Lab")
    private String assignedLabs;

    /**
     * Default constructor.
     * Automatically sets the user role to "Technical Officer".
     */
    public TechnicalOfficer() {
        setRole("Technical Officer");
    }

    /**
     * Parameterized constructor for quick initialization.
     *
     * @param userId               The user ID from the users table
     * @param technicalOfficerCode Unique technical officer code
     * @param designation          Job title or designation
     */
    public TechnicalOfficer(long userId, String technicalOfficerCode, String designation) {
    }

    // --- Getters and Setters ---

    public String getTechnicalOfficerCode() { return technicalOfficerCode; }
    public void setTechnicalOfficerCode(String technicalOfficerCode) { this.technicalOfficerCode = technicalOfficerCode; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getAssignedLabs() { return assignedLabs; }
    public void setAssignedLabs(String assignedLabs) { this.assignedLabs = assignedLabs; }
}
