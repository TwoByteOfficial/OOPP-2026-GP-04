package model;

import java.sql.Time;

public class TimetableEntry {
    private long timetableId;
    private int departmentId;
    private int courseId;
    private Long lecturerId;
    private int batchYear;
    private String component;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private String venue;
    private String academicYear;
    private String semester;

    public TimetableEntry() {}

    public long getTimetableId() { return timetableId; }
    public void setTimetableId(long timetableId) { this.timetableId = timetableId; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public Long getLecturerId() { return lecturerId; }
    public void setLecturerId(Long lecturerId) { this.lecturerId = lecturerId; }
    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
