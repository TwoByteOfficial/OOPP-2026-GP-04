package gui.undergraduate.profile.model;

/**
 * Data model representing the comprehensive student profile and credentials.
 * Encapsulates immutable academic placement records (registration number, degree program,
 * department, level/semester standing) and mutable personal contact details (phone,
 * residential address, cloud profile picture URL).
 */
public class UserProfileData {

    // Unique user account primary key identifier in database
    private final long userId;

    // Institutional identification and academic enrollment records
    private final String studentRegNo;
    private final String fullName;
    private final String email;
    private final String department;
    private final String degreeProgram;
    private final String currentLevel;
    private final String currentSemester;
    private final String academicStatus;

    // Student-managed personal contact information and avatar link
    private String contactNo;
    private String address;
    private String profilePicture;

    /**
     * Primary 12-argument constructor initializing a full student profile record
     * including custom contact settings and profile picture reference.
     *
     * @param userId          Unique primary key in the users table
     * @param studentRegNo    Official university registration number
     * @param fullName        Full name of the student
     * @param email           Institutional student email address
     * @param department      Assigned academic department
     * @param degreeProgram   Enrolled degree title
     * @param currentLevel    Current academic year/level
     * @param currentSemester Current study semester
     * @param academicStatus  Academic standing (e.g., Proper Candidate, Repeat)
     * @param contactNo       Personal contact telephone number
     * @param address         Residential or postal address
     * @param profilePicture  Direct URL or cloud storage path to student avatar image
     */
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

    /**
     * Overloaded 9-argument constructor providing backward compatibility for baseline
     * academic data with default contact placeholders.
     *
     * @param userId          Unique primary key in the users table
     * @param studentRegNo    Official university registration number
     * @param fullName        Full name of the student
     * @param email           Institutional student email address
     * @param department      Assigned academic department
     * @param degreeProgram   Enrolled degree title
     * @param currentLevel    Current academic year/level
     * @param currentSemester Current study semester
     * @param academicStatus  Academic standing (e.g., Proper Candidate, Repeat)
     */
    public UserProfileData(long userId, String studentRegNo, String fullName, String email,
                           String department, String degreeProgram, String currentLevel,
                           String currentSemester, String academicStatus) {
        this(userId, studentRegNo, fullName, email, department, degreeProgram,
                currentLevel, currentSemester, academicStatus, "0713002061", "Matara, Sri Lanka", null);
    }

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

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

    // -------------------------------------------------------------
    // Setter Methods (Mutable Contact & Profile Attributes)
    // -------------------------------------------------------------

    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    public void setAddress(String address) { this.address = address; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}