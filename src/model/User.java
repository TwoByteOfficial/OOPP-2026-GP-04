package model;

public class User {

    private long userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String contactNo;
    private String address;
    private String profilePicture;
    private Integer departmentId;
    private String userStatus;

    // Empty constructor
    public User() {
    }

    // Constructor without userId and timestamps
    public User(String username,
                String passwordHash,
                String fullName,
                String email,
                String contactNo,
                String address,
                String profilePicture,
                Integer departmentId,
                String userStatus) {

        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.contactNo = contactNo;
        this.address = address;
        this.profilePicture = profilePicture;
        this.departmentId = departmentId;
        this.userStatus = userStatus;
    }

    // Full constructor
    public User(long userId,
                String username,
                String passwordHash,
                String fullName,
                String email,
                String contactNo,
                String address,
                String profilePicture,
                Integer departmentId,
                String userStatus) {

        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.contactNo = contactNo;
        this.address = address;
        this.profilePicture = profilePicture;
        this.departmentId = departmentId;
        this.userStatus = userStatus;
    }

    // Getters and Setters

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username='" + username + '\'' + ", fullName='" + fullName + '\'' + ", email='" + email + '\'' + ", contactNo='" + contactNo + '\'' + ", departmentId=" + departmentId + ", userStatus='" + userStatus + '\'' + '}';
    }
}