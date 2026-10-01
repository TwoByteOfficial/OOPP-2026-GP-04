package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Mark {

    private Long markId;
    private Long assessmentId;
    private Long studentId;
    private BigDecimal mark;
    private Long enteredBy;
    private Timestamp enteredAt;
    private Timestamp updatedAt;

    public Mark() {
    }

    public Mark(Long markId, Long assessmentId, Long studentId,
                BigDecimal mark, Long enteredBy,
                Timestamp enteredAt, Timestamp updatedAt) {

        this.markId = markId;
        this.assessmentId = assessmentId;
        this.studentId = studentId;
        this.mark = mark;
        this.enteredBy = enteredBy;
        this.enteredAt = enteredAt;
        this.updatedAt = updatedAt;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getMark() {
        return mark;
    }

    public void setMark(BigDecimal mark) {
        this.mark = mark;
    }

    public Long getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(Long enteredBy) {
        this.enteredBy = enteredBy;
    }

    public Timestamp getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Timestamp enteredAt) {
        this.enteredAt = enteredAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}