package dao.undergraduate;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Official Notices and Circulars feed.
 */
public class UndergraduateNoticeDAO {

    public static class NoticeItemDTO {
        public long noticeId;
        public String title;
        public String content;
        public String noticeType; // Academic, Exam, Attendance, Urgent, General
        public String targetRole;
        public String publishedAt;
        public String publisher;
    }

    public static class NoticeCountsDTO {
        public int totalCount = 0;
        public int academicCount = 0;
        public int examCount = 0;
        public int urgentCount = 0;
    }

    public static List<NoticeItemDTO> getActiveNotices() {
        List<NoticeItemDTO> list = new ArrayList<>();
        String sql = "SELECT n.notice_id, n.title, n.content, n.notice_type, n.target_role, " +
                "DATE_FORMAT(n.published_at, '%Y-%m-%d') AS pub_date, " +
                "COALESCE(u.full_name, 'Faculty Administration') AS publisher " +
                "FROM notices n " +
                "LEFT JOIN users u ON n.published_by = u.user_id " +
                "WHERE n.target_role IN ('All', 'Undergraduate') AND n.status = 'Published' " +
                "ORDER BY n.published_at DESC";

        try (PreparedStatement ps = DBConnection.UG_CON.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NoticeItemDTO dto = new NoticeItemDTO();
                dto.noticeId = rs.getLong("notice_id");
                dto.title = rs.getString("title");
                dto.content = rs.getString("content");
                dto.noticeType = rs.getString("notice_type");
                dto.targetRole = rs.getString("target_role");
                dto.publishedAt = rs.getString("pub_date");
                dto.publisher = rs.getString("publisher");
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static NoticeCountsDTO getNoticeCounts() {
        NoticeCountsDTO counts = new NoticeCountsDTO();
        List<NoticeItemDTO> notices = getActiveNotices();
        counts.totalCount = notices.size();

        for (NoticeItemDTO n : notices) {
            if ("Academic".equalsIgnoreCase(n.noticeType)) counts.academicCount++;
            else if ("Exam".equalsIgnoreCase(n.noticeType)) counts.examCount++;
            else if ("Urgent".equalsIgnoreCase(n.noticeType) || "Emergency".equalsIgnoreCase(n.noticeType)) counts.urgentCount++;
        }
        return counts;
    }
}