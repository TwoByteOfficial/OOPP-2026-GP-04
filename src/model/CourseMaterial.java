package model;

import java.sql.Timestamp;

public class CourseMaterial {
    private long materialId;
    private int courseId;
    private long lecturerId;
    private String title;
    private String description;
    private String materialType;
    private String filePath;
    private String externalUrl;
    private Timestamp uploadedAt;

    public CourseMaterial() {}

    public long getMaterialId() { return materialId; }
    public void setMaterialId(long materialId) { this.materialId = materialId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public long getLecturerId() { return lecturerId; }
    public void setLecturerId(long lecturerId) { this.lecturerId = lecturerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    public Timestamp getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt) { this.uploadedAt = uploadedAt; }
}
