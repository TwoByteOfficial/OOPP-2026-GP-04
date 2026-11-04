package gui.undergraduate.timetable.model;

/**
 * Data model matching DB 'timetable_entries' schema.
 * Represents lecture and lab sessions scheduled across the academic week.
 * Encapsulates scheduling logistics including day, duration slot, course identifiers,
 * delivery mode (Theory/Practical), assigned venue hall, and lecturing staff.
 */
public class TimetableEntryData {

    // Primary key identifier corresponding to timetable_entries table
    private final long timetableId;

    // Temporal scheduling attributes
    private final String dayOfWeek;
    private final String timeSlot;

    // Academic course identifiers
    private final String courseCode;
    private final String courseName;

    // Delivery classification and spatial/staff allocation
    private final String component; // 'Theory' or 'Practical'
    private final String venue;
    private final String lecturerName;

    /**
     * Constructs an immutable TimetableEntryData entity.
     *
     * @param timetableId  Unique record identifier from the database
     * @param dayOfWeek    Day of the week (e.g., 'Monday', 'Tuesday')
     * @param timeSlot     Time duration string (e.g., '08:00 - 10:00')
     * @param courseCode   Course code identifier (e.g., 'ICT2112')
     * @param courseName   Full name of the academic module
     * @param component    Delivery mode classification ('Theory' or 'Practical')
     * @param venue        Allocated lecture room, theater, or computer laboratory
     * @param lecturerName Name of the appointed academic lecturer or instructor
     */
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

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

    public long getTimetableId() { return timetableId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getTimeSlot() { return timeSlot; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getComponent() { return component; }
    public String getVenue() { return venue; }
    public String getLecturerName() { return lecturerName; }
}