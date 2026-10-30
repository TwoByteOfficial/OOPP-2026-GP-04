package dao.undergraduate;

import util.DBConnection;
import gui.undergraduate.notices.model.NoticeData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateNoticeDAO {

    public static List<NoticeData> getActiveNotices() {
        List<NoticeData> list = new ArrayList<>();
        String sql = "SELECT n.notice_id, n.title, n.content, n.notice_type, n.target_role, " +
                "DATE_FORMAT(n.published_at, '%Y-%m-%d') AS pub_date, u.full_name AS publisher " +
                "FROM notices n " +
                "LEFT JOIN users u ON n.published_by = u.user_id " +
                "WHERE n.target_role IN ('All', 'Undergraduate') AND n.status = 'Published' " +
                "ORDER BY n.published_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new NoticeData(
                        rs.getLong("notice_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("notice_type"),
                        rs.getString("target_role"),
                        rs.getString("pub_date"),
                        rs.getString("publisher") != null ? rs.getString("publisher") : "Faculty Administration"
                ));
            }
        } catch (Exception e) {
            System.err.println("[UndergraduateNoticeDAO] Fallback triggered: " + e.getMessage());
        }
        return list;
    }
}