package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class AttendanceSession {

    private Long attendanceSessionId;
    private Integer courseId;
    private String component;
    private Integer sessionNo;
    private Date sessionDate;
    private Time startTime;
    private Time endTime;
    private BigDecimal durationHours;
    private Long conductedBy;
    private String topic;
    private Timestamp createdAt;

    public AttendanceSession() {
    }

    public AttendanceSession(Long attendanceSessionId, Integer courseId,
                             String component, Integer sessionNo,
                             Date sessionDate, Time startTime,
                             Time endTime, BigDecimal durationHours,
                             Long conductedBy, String topic,
                             Timestamp createdAt) {

        this.attendanceSessionId = attendanceSessionId;
        this.courseId = courseId;
        this.component = component;
        this.sessionNo = sessionNo;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationHours = durationHours;
        this.conductedBy = conductedBy;
        this.topic = topic;
        this.createdAt = createdAt;
    }

    public Long getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Long attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getSessionNo() {
        return sessionNo;
    }

    public void setSessionNo(Integer sessionNo) {
        this.sessionNo = sessionNo;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(BigDecimal durationHours) {
        this.durationHours = durationHours;
    }

    public Long getConductedBy() {
        return conductedBy;
    }

    public void setConductedBy(Long conductedBy) {
        this.conductedBy = conductedBy;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}