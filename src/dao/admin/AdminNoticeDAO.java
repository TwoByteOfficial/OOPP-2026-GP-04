package dao.admin;

import util.DBConnection;
import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsible for Admin Notice Management (Notices page CRUD & status updates).
 */
public class AdminNoticeDAO {

    public List<Notice> getAllNotices() throws SQLException {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notices ORDER BY published_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToNotice(rs));
            }
        }
        return list;
    }

    public Notice getNoticeById(long noticeId) throws SQLException {
        String sql = "SELECT * FROM notices WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, noticeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotice(rs);
                }
            }
        }
        return null;
    }

    public boolean createNotice(Notice n) throws SQLException {
        String sql = "INSERT INTO notices (title, content, notice_type, target_role, department_id, published_by, expires_at, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, n.getTitle());
            stmt.setString(2, n.getContent());
            stmt.setString(3, n.getNoticeType());
            stmt.setString(4, n.getTargetRole());
            if (n.getDepartmentId() != null) stmt.setInt(5, n.getDepartmentId()); else stmt.setNull(5, Types.INTEGER);
            if (n.getPublishedBy() != null) stmt.setLong(6, n.getPublishedBy()); else stmt.setNull(6, Types.BIGINT);
            stmt.setTimestamp(7, n.getExpiresAt());
            stmt.setString(8, n.getStatus() != null ? n.getStatus() : "Published");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        n.setNoticeId(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateNotice(Notice n) throws SQLException {
        String sql = "UPDATE notices SET title = ?, content = ?, notice_type = ?, target_role = ?, department_id = ?, expires_at = ?, status = ? WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, n.getTitle());
            stmt.setString(2, n.getContent());
            stmt.setString(3, n.getNoticeType());
            stmt.setString(4, n.getTargetRole());
            if (n.getDepartmentId() != null) stmt.setInt(5, n.getDepartmentId()); else stmt.setNull(5, Types.INTEGER);
            stmt.setTimestamp(6, n.getExpiresAt());
            stmt.setString(7, n.getStatus());
            stmt.setLong(8, n.getNoticeId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteNotice(long noticeId) throws SQLException {
        String sql = "DELETE FROM notices WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, noticeId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateNoticeStatus(long noticeId, String status) throws SQLException {
        String sql = "UPDATE notices SET status = ? WHERE notice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, noticeId);
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
