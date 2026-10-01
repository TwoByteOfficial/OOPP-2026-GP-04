package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Assessment {

    private Long assessmentId;
    private Integer courseId;
    private String assessmentName;
    private String assessmentCategory;
    private String assessmentType;
    private String component;
    private BigDecimal weightPercent;
    private BigDecimal maxMark;
    private Date assessmentDate;
    private Long createdBy;

    public Assessment() {
    }

    public Assessment(Long assessmentId, Integer courseId,
                      String assessmentName, String assessmentCategory,
                      String assessmentType, String component,
                      BigDecimal weightPercent, BigDecimal maxMark,
                      Date assessmentDate, Long createdBy) {

        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.assessmentName = assessmentName;
        this.assessmentCategory = assessmentCategory;
        this.assessmentType = assessmentType;
        this.component = component;
        this.weightPercent = weightPercent;
        this.maxMark = maxMark;
        this.assessmentDate = assessmentDate;
        this.createdBy = createdBy;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getAssessmentCategory() {
        return assessmentCategory;
    }

    public void setAssessmentCategory(String assessmentCategory) {
        this.assessmentCategory = assessmentCategory;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public BigDecimal getWeightPercent() {
        return weightPercent;
    }

    public void setWeightPercent(BigDecimal weightPercent) {
        this.weightPercent = weightPercent;
    }

    public BigDecimal getMaxMark() {
        return maxMark;
    }

    public void setMaxMark(BigDecimal maxMark) {
        this.maxMark = maxMark;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}