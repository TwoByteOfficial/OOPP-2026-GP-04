package model;

/**
 * Model representing a Technical Officer in the university system.
 * 
 * [OOP - INHERITANCE]: Extends the base class 'User'. TechnicalOfficer inherits all core 
 * account attributes (userId, username, email, fullName, etc.) while adding specialized fields 
 * specific to a technical officer (staff code, designation, assigned laboratories).
 */
public class TechnicalOfficer extends User {

    // [OOP - ENCAPSULATION]: Fields are declared private to ensure data hiding.
    private String technicalOfficerCode;
    private String designation;
    private String assignedLabs;

    /**
     * Default constructor.
     * Inherited method setRole() from User is called to set the specific role.
     */
    public TechnicalOfficer() {
        setRole("Technical Officer");
    }

    /**
     * [OOP - POLYMORPHISM (OVERLOADING)]: Constructor overloading allows creating 
     * a TechnicalOfficer object with different sets of initial arguments (Compile-time polymorphism).
     *
     * @param userId               The user ID from the users table
     * @param technicalOfficerCode Unique technical officer code
     * @param designation          Job title or designation
     */
    public TechnicalOfficer(long userId, String technicalOfficerCode, String designation) {
    }

    // [OOP - ENCAPSULATION]: Getters and Setters provide controlled access to private fields.
    public String getTechnicalOfficerCode() { return technicalOfficerCode; }
    public void setTechnicalOfficerCode(String technicalOfficerCode) { this.technicalOfficerCode = technicalOfficerCode; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getAssignedLabs() { return assignedLabs; }
    public void setAssignedLabs(String assignedLabs) { this.assignedLabs = assignedLabs; }
}
