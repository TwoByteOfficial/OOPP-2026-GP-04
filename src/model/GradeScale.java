package model;

import java.math.BigDecimal;

public class GradeScale {

    private Integer gradeId;
    private String gradeCode;
    private BigDecimal minimumMark;
    private BigDecimal gradePoint;
    private String description;

    public GradeScale() {
    }

    public GradeScale(Integer gradeId, String gradeCode,
                      BigDecimal minimumMark,
                      BigDecimal gradePoint,
                      String description) {
        this.gradeId = gradeId;
        this.gradeCode = gradeCode;
        this.minimumMark = minimumMark;
        this.gradePoint = gradePoint;
        this.description = description;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    public BigDecimal getMinimumMark() {
        return minimumMark;
    }

    public void setMinimumMark(BigDecimal minimumMark) {
        this.minimumMark = minimumMark;
    }

    public BigDecimal getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(BigDecimal gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}