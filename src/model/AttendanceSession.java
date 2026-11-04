package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Model representing a specific class or lab session conducted for a course.
 * Holds scheduling information such as date, time, component type (Lecture/Practical),
 * and session number so Technical Officers can track attendance per session.
 */
public class AttendanceSession {

    // Unique primary key identifying the attendance session
    private long attendanceSessionId;

    // Foreign key pointing to the course
    private int courseId;

    // Component type: "Theory" or "Practical"
    private String component;

    // Sequential number of the session (e.g., Session 1, Session 2)
    private int sessionNo;

    // Date when the session took place
    private Date sessionDate;

    // Starting time of the session
    private Time startTime;

    // Finishing time of the session
    private Time endTime;

    // Total duration in hours (e.g., 2.0)
    private BigDecimal durationHours;

    // User ID of the lecturer or officer who conducted the session
    private Long conductedBy;

    // Lesson topic or laboratory practical title covered in this session
    private String topic;

    /**
     * Default constructor.
     */
    public AttendanceSession() {}

    /**
     * Parameterized constructor for initializing an attendance session.
     */
    public AttendanceSession(long attendanceSessionId, int courseId, String component, 
                             int sessionNo, Date sessionDate, Time startTime, Time endTime, 
                             BigDecimal durationHours, Long conductedBy, String topic, 
                             Timestamp createdAt) {
    }

    // --- Getters and Setters ---

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

    /**
     * Returns a human-readable string representation used in dropdown combo boxes.
     * Example: "Session 1 - 2026-03-15"
     */
    @Override
    public String toString() {
        return "Session " + sessionNo + " - " + sessionDate;
    }
}
