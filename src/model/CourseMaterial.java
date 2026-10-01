package model;

import java.sql.Timestamp;

public class CourseMaterial {

    private Long materialId;
    private Integer courseId;
    private Long lecturerId;
    private String title;
    private String description;
    private String materialType;
    private String filePath;
    private String externalUrl;
    private Timestamp uploadedAt;
    private Timestamp updatedAt;

    public CourseMaterial() {
    }

    public CourseMaterial(Long materialId, Integer courseId, Long lecturerId,
                          String title, String description,
                          String materialType, String filePath,
                          String externalUrl, Timestamp uploadedAt,
                          Timestamp updatedAt) {

        this.materialId = materialId;
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.title = title;
        this.description = description;
        this.materialType = materialType;
        this.filePath = filePath;
        this.externalUrl = externalUrl;
        this.uploadedAt = uploadedAt;
        this.updatedAt = updatedAt;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}