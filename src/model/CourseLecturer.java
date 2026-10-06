package model;

import java.sql.Timestamp;

public class CourseLecturer {
    private int courseId;
    private long lecturerId;
    private String responsibility;
    private Timestamp assignedAt;

    public CourseLecturer() {}

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public long getLecturerId() { return lecturerId; }
    public void setLecturerId(long lecturerId) { this.lecturerId = lecturerId; }
    public String getResponsibility() { return responsibility; }
    public void setResponsibility(String responsibility) { this.responsibility = responsibility; }
    public Timestamp getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
}
