package model;

public class Lecturer extends User {
    private String lecturerCode;
    private String title;
    private String specialization;

    public Lecturer() {
        setRole("Lecturer");
    }

    public String getLecturerCode() { return lecturerCode; }
    public void setLecturerCode(String lecturerCode) { this.lecturerCode = lecturerCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
