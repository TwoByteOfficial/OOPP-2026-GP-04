package gui.undergraduate.notices.model;

/**
 * Data model matching DB 'notices' table schema.
 * Represents faculty announcements, circulars, and academic circulars.
 * Encapsulates the core metadata for bulletin items including publication timestamp,
 * target role audience, category classification, and publisher credentials.
 */
public class NoticeData {

    // Unique primary key identifier in database
    private final long noticeId;

    // Headline and full message content
    private final String title;
    private final String content;

    // Classification category and designated target audience
    private final String noticeType; // 'Academic', 'Exam', 'Attendance', 'General', 'Urgent'
    private final String targetRole; // 'All', 'Undergraduate'

    // Publication audit details
    private final String publishedAt;
    private final String publisher;

    /**
     * Constructs an immutable NoticeData entity snapshot.
     *
     * @param noticeId    Unique record identifier
     * @param title       Notice headline title
     * @param content     Full announcement body or description
     * @param noticeType  Notice category classification
     * @param targetRole  Audience restriction (e.g., 'All', 'Undergraduate')
     * @param publishedAt Date or timestamp string when notice was issued
     * @param publisher   Issuing authority or department name
     */
    public NoticeData(long noticeId, String title, String content,
                      String noticeType, String targetRole,
                      String publishedAt, String publisher) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.noticeType = noticeType;
        this.targetRole = targetRole;
        this.publishedAt = publishedAt;
        this.publisher = publisher;
    }

    // -------------------------------------------------------------
    // Getter Methods
    // -------------------------------------------------------------

    public long getNoticeId() { return noticeId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getNoticeType() { return noticeType; }
    public String getTargetRole() { return targetRole; }
    public String getPublishedAt() { return publishedAt; }
    public String getPublisher() { return publisher; }
}