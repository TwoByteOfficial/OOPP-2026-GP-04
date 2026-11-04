package gui.undergraduate.profile.model;

public class UserProfileData {
    private final long userId;
    private final String studentRegNo;
    private final String fullName;
    private final String email;
    private final String department;
    private final String degreeProgram;
    private final String currentLevel;
    private final String currentSemester;
    private final String academicStatus;
    private String contactNo;
    private String address;
    private String profilePicture;

    // 12-argument Constructor (for full profile with contact details)
    public UserProfileData(long userId, String studentRegNo, String fullName, String email,
                           String department, String degreeProgram, String currentLevel,
                           String currentSemester, String academicStatus,
                           String contactNo, String address, String profilePicture) {
        this.userId = userId;
        this.studentRegNo = studentRegNo;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.degreeProgram = degreeProgram;
        this.currentLevel = currentLevel;
        this.currentSemester = currentSemester;
        this.academicStatus = academicStatus;
        this.contactNo = contactNo;
        this.address = address;
        this.profilePicture = profilePicture;
    }

    // 9-argument Overloaded Constructor (for backward compatibility)
    public UserProfileData(long userId, String studentRegNo, String fullName, String email,
                           String department, String degreeProgram, String currentLevel,
                           String currentSemester, String academicStatus) {
        this(userId, studentRegNo, fullName, email, department, degreeProgram,
                currentLevel, currentSemester, academicStatus, "0713002061", "Matara, Sri Lanka", null);
    }

    public long getUserId() { return userId; }
    public String getStudentRegNo() { return studentRegNo; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getDegreeProgram() { return degreeProgram; }
    public String getCurrentLevel() { return currentLevel; }
    public String getCurrentSemester() { return currentSemester; }
    public String getAcademicStatus() { return academicStatus; }
    public String getContactNo() { return contactNo; }
    public String getAddress() { return address; }
    public String getProfilePicture() { return profilePicture; }

    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    public void setAddress(String address) { this.address = address; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}