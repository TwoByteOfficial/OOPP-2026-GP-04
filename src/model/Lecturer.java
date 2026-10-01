package model;

public class Lecturer {

    private Long userId;
    private String lecturerCode;
    private String title;
    private String specialization;

    public Lecturer() {
    }

    public Lecturer(Long userId, String lecturerCode,
                    String title, String specialization) {
        this.userId = userId;
        this.lecturerCode = lecturerCode;
        this.title = title;
        this.specialization = specialization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLecturerCode() {
        return lecturerCode;
    }

    public void setLecturerCode(String lecturerCode) {
        this.lecturerCode = lecturerCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}