package dao;

import util.DBConnection;
import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public List<Notice> getAllNotices() throws SQLException {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notices WHERE status = 'Published' ORDER BY published_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToNotice(rs));
            }
        }
        return list;
    }

    public List<Notice> getNoticesByRole(String role) throws SQLException {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notices WHERE status = 'Published' AND (target_role = 'All' OR target_role = ?) ORDER BY published_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNotice(rs));
                }
            }
        }
        return list;
    }

    public boolean createNotice(Notice n) throws SQLException {
        String sql = "INSERT INTO notices (title, content, notice_type, target_role, department_id, published_by, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, n.getTitle());
            stmt.setString(2, n.getContent());
            stmt.setString(3, n.getNoticeType());
            stmt.setString(4, n.getTargetRole());
            if (n.getDepartmentId() != null) stmt.setInt(5, n.getDepartmentId()); else stmt.setNull(5, Types.INTEGER);
            if (n.getPublishedBy() != null) stmt.setLong(6, n.getPublishedBy()); else stmt.setNull(6, Types.BIGINT);
            stmt.setString(7, n.getStatus() != null ? n.getStatus() : "Published");
            return stmt.executeUpdate() > 0;
        }
    }

    private Notice mapResultSetToNotice(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setNoticeId(rs.getLong("notice_id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setNoticeType(rs.getString("notice_type"));
        n.setTargetRole(rs.getString("target_role"));
        n.setDepartmentId((Integer) rs.getObject("department_id"));
        n.setPublishedBy((Long) rs.getObject("published_by"));
        n.setPublishedAt(rs.getTimestamp("published_at"));
        n.setExpiresAt(rs.getTimestamp("expires_at"));
        n.setStatus(rs.getString("status"));
        return n;
    }
}
