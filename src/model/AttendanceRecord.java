package model;

import java.sql.Timestamp;

/**
 * Model representing a single student's attendance record for an academic session.
 * Used by Technical Officers to record, view, and update attendance (Present, Absent, etc.).
 */
public class AttendanceRecord {

    // [OOP - ENCAPSULATION]: Private member variables protect data integrity.
    private long attendanceRecordId;
    private long attendanceSessionId;
    private long studentId;
    private String attendanceStatus;
    private String remarks;
    private Long recordedBy;
    private Timestamp recordedAt;

    /**
     * Default constructor.
     */
    public AttendanceRecord() {}

    /**
     * [OOP - POLYMORPHISM (OVERLOADING)]: Overloaded constructor providing multiple ways
     * to instantiate an AttendanceRecord object (Compile-time Polymorphism).
     */
    public AttendanceRecord(long attendanceRecordId, long attendanceSessionId, long studentId, 
                            String attendanceStatus, String remarks, Long recordedBy, 
                            Timestamp recordedAt, Timestamp updatedAt) {
    }

    // [OOP - ENCAPSULATION]: Public getter and setter methods providing regulated access.
    public long getAttendanceRecordId() { return attendanceRecordId; }
    public void setAttendanceRecordId(long attendanceRecordId) { this.attendanceRecordId = attendanceRecordId; }

    public long getAttendanceSessionId() { return attendanceSessionId; }
    public void setAttendanceSessionId(long attendanceSessionId) { this.attendanceSessionId = attendanceSessionId; }

    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Long getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Long recordedBy) { this.recordedBy = recordedBy; }

    public Timestamp getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Timestamp recordedAt) { this.recordedAt = recordedAt; }
}
