package gui.undergraduate.profile.model;

/**
 * Data model matching DB 'users' and 'students' entities.
 * Encapsulates undergraduate identification, academic placement, and contact info.
 */
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

    public UserProfileData(long userId, String studentRegNo, String fullName,
                           String email, String department, String degreeProgram,
                           String currentLevel, String currentSemester, String academicStatus) {
        this.userId = userId;
        this.studentRegNo = studentRegNo;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.degreeProgram = degreeProgram;
        this.currentLevel = currentLevel;
        this.currentSemester = currentSemester;
        this.academicStatus = academicStatus;
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
}