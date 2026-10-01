package model;

import java.sql.Time;

public class TimetableEntry {

    private Long timetableId;
    private Integer departmentId;
    private Integer courseId;
    private Long lecturerId;
    private Integer batchYear;
    private String component;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private String venue;
    private String academicYear;
    private String semester;

    public TimetableEntry() {
    }

    public TimetableEntry(Long timetableId, Integer departmentId,
                          Integer courseId, Long lecturerId,
                          Integer batchYear, String component,
                          String dayOfWeek, Time startTime,
                          Time endTime, String venue,
                          String academicYear, String semester) {

        this.timetableId = timetableId;
        this.departmentId = departmentId;
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.batchYear = batchYear;
        this.component = component;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.academicYear = academicYear;
        this.semester = semester;
    }

    public Long getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(Long timetableId) {
        this.timetableId = timetableId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
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

    public Integer getBatchYear() {
        return batchYear;
    }

    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}