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

    // [OOP - ENCAPSULATION]: Private fields ensure encapsulation and prevent direct tampering.
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

    /**
     * Default constructor.
     */
    public AttendanceSession() {}

    /**
     * [OOP - POLYMORPHISM (OVERLOADING)]: Constructor overloading with full parameter list.
     */
    public AttendanceSession(long attendanceSessionId, int courseId, String component, 
                             int sessionNo, Date sessionDate, Time startTime, Time endTime, 
                             BigDecimal durationHours, Long conductedBy, String topic, 
                             Timestamp createdAt) {
    }

    // [OOP - ENCAPSULATION]: Public getters and setters for controlled data access.
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
     * [OOP - POLYMORPHISM (OVERRIDING)]: Overriding Object's toString() method.
     * At runtime, whenever Java needs a String representation of this object (such as
     * in a JComboBox dropdown), this custom implementation is dynamically invoked (Dynamic Dispatch).
     */
    @Override
    public String toString() {
        return "Session " + sessionNo + " - " + sessionDate;
    }
}
