package gui.undergraduate.notices.model;

/**
 * Data model matching DB 'notices' table schema.
 * Represents faculty announcements, circulars, and academic circulars.
 */
public class NoticeData {
    private final long noticeId;
    private final String title;
    private final String content;
    private final String noticeType; // 'Academic', 'Exam', 'Attendance', 'General', 'Urgent'
    private final String targetRole; // 'All', 'Undergraduate'
    private final String publishedAt;
    private final String publisher;

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

    public long getNoticeId() { return noticeId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getNoticeType() { return noticeType; }
    public String getTargetRole() { return targetRole; }
    public String getPublishedAt() { return publishedAt; }
    public String getPublisher() { return publisher; }
}