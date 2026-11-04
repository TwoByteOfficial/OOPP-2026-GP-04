package model;

import java.sql.Timestamp;

/**
 * Model representing a single student's attendance record for an academic session.
 * Used by Technical Officers to record, view, and update attendance (Present, Absent, etc.).
 */
public class AttendanceRecord {

    // Primary key of the attendance record in database
    private long attendanceRecordId;

    // Foreign key pointing to the specific class session (lecture or practical)
    private long attendanceSessionId;

    // Foreign key pointing to the undergraduate student (user_id)
    private long studentId;

    // Status of attendance: "PRESENT", "ABSENT", or "EXCUSED"
    private String attendanceStatus;

    // Optional remarks or notes (e.g., "Late by 15 mins", "Medical pending")
    private String remarks;

    // User ID of the Technical Officer or Lecturer who recorded this attendance
    private Long recordedBy;

    // Date and time when this attendance entry was submitted or updated
    private Timestamp recordedAt;

    /**
     * Default constructor.
     */
    public AttendanceRecord() {}

    /**
     * Parameterized constructor for initializing an attendance record.
     */
    public AttendanceRecord(long attendanceRecordId, long attendanceSessionId, long studentId, 
                            String attendanceStatus, String remarks, Long recordedBy, 
                            Timestamp recordedAt, Timestamp updatedAt) {
    }

    // --- Getters and Setters ---

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
