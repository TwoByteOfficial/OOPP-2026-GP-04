package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Mark {
    private long markId;
    private long assessmentId;
    private long studentId;
    private BigDecimal mark;
    private Long enteredBy;
    private Timestamp enteredAt;

    public Mark() {}

    public long getMarkId() { return markId; }
    public void setMarkId(long markId) { this.markId = markId; }
    public long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(long assessmentId) { this.assessmentId = assessmentId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public BigDecimal getMark() { return mark; }
    public void setMark(BigDecimal mark) { this.mark = mark; }
    public Long getEnteredBy() { return enteredBy; }
    public void setEnteredBy(Long enteredBy) { this.enteredBy = enteredBy; }
    public Timestamp getEnteredAt() { return enteredAt; }
    public void setEnteredAt(Timestamp enteredAt) { this.enteredAt = enteredAt; }
}
