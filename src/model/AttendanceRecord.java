package model;

import java.sql.Timestamp;

public class AttendanceRecord {
    private long attendanceRecordId;
    private long attendanceSessionId;
    private long studentId;
    private String attendanceStatus;
    private String remarks;
    private Long recordedBy;
    private Timestamp recordedAt;

    public AttendanceRecord() {}

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
