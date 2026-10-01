package model;

import java.sql.Timestamp;

public class Notice {

    private Long noticeId;
    private String title;
    private String content;
    private String noticeType;
    private String targetRole;
    private Integer departmentId;
    private Long publishedBy;
    private Timestamp publishedAt;
    private Timestamp expiresAt;
    private String status;

    public Notice() {
    }

    public Notice(Long noticeId, String title, String content,
                  String noticeType, String targetRole,
                  Integer departmentId, Long publishedBy,
                  Timestamp publishedAt, Timestamp expiresAt,
                  String status) {

        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.noticeType = noticeType;
        this.targetRole = targetRole;
        this.departmentId = departmentId;
        this.publishedBy = publishedBy;
        this.publishedAt = publishedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(Long publishedBy) {
        this.publishedBy = publishedBy;
    }

    public Timestamp getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Timestamp publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}