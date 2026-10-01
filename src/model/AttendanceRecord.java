package model;

import java.sql.Timestamp;

public class AttendanceRecord {

    private Long attendanceRecordId;
    private Long attendanceSessionId;
    private Long studentId;
    private String attendanceStatus;
    private String remarks;
    private Long recordedBy;
    private Timestamp recordedAt;
    private Timestamp updatedAt;

    public AttendanceRecord() {
    }

    public AttendanceRecord(Long attendanceRecordId,
                            Long attendanceSessionId,
                            Long studentId,
                            String attendanceStatus,
                            String remarks,
                            Long recordedBy,
                            Timestamp recordedAt,
                            Timestamp updatedAt) {

        this.attendanceRecordId = attendanceRecordId;
        this.attendanceSessionId = attendanceSessionId;
        this.studentId = studentId;
        this.attendanceStatus = attendanceStatus;
        this.remarks = remarks;
        this.recordedBy = recordedBy;
        this.recordedAt = recordedAt;
        this.updatedAt = updatedAt;
    }

    public Long getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public void setAttendanceRecordId(Long attendanceRecordId) {
        this.attendanceRecordId = attendanceRecordId;
    }

    public Long getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Long attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Long recordedBy) {
        this.recordedBy = recordedBy;
    }

    public Timestamp getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Timestamp recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}