package model;

import java.sql.Timestamp;

public class CourseLecturer {

    private Integer courseId;
    private Long lecturerId;
    private String responsibility;
    private Timestamp assignedAt;

    public CourseLecturer() {
    }

    public CourseLecturer(Integer courseId, Long lecturerId,
                          String responsibility, Timestamp assignedAt) {
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.responsibility = responsibility;
        this.assignedAt = assignedAt;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public Timestamp getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Timestamp assignedAt) {
        this.assignedAt = assignedAt;
    }
}