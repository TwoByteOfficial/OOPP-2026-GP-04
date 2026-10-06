package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class AttendanceSession {
    private long attendanceSessionId;
    private int courseId;
    private String component;
    private int sessionNo;
    private Date sessionDate;
    private Time startTime;
    private Time endTime;
    private BigDecimal durationHours;
    private Long conductedBy;
    private String topic;

    public AttendanceSession() {}

    public long getAttendanceSessionId() { return attendanceSessionId; }
    public void setAttendanceSessionId(long attendanceSessionId) { this.attendanceSessionId = attendanceSessionId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public int getSessionNo() { return sessionNo; }
    public void setSessionNo(int sessionNo) { this.sessionNo = sessionNo; }
    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public BigDecimal getDurationHours() { return durationHours; }
    public void setDurationHours(BigDecimal durationHours) { this.durationHours = durationHours; }
    public Long getConductedBy() { return conductedBy; }
    public void setConductedBy(Long conductedBy) { this.conductedBy = conductedBy; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
