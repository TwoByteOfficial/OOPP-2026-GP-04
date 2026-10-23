package gui.undergraduate.timetable.model;

/**
 * Data model matching DB 'timetable_entries' schema.
 * Represents lecture and lab sessions scheduled across the academic week.
 */
public class TimetableEntryData {
    private final long timetableId;
    private final String dayOfWeek;
    private final String timeSlot;
    private final String courseCode;
    private final String courseName;
    private final String component; // 'Theory' or 'Practical'
    private final String venue;
    private final String lecturerName;

    public TimetableEntryData(long timetableId, String dayOfWeek, String timeSlot,
                              String courseCode, String courseName, String component,
                              String venue, String lecturerName) {
        this.timetableId = timetableId;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.component = component;
        this.venue = venue;
        this.lecturerName = lecturerName;
    }

    public long getTimetableId() { return timetableId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getTimeSlot() { return timeSlot; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getComponent() { return component; }
    public String getVenue() { return venue; }
    public String getLecturerName() { return lecturerName; }
}