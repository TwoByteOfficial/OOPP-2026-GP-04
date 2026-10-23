package dao.admin;

import util.DBConnection;
import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminNoticeDAO {

    public List<Notice> getAllNotices() throws SQLException {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notices ORDER BY published_at DESC";

        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToNotice(rs));
            }
        }
        return list;
    }

    public Notice getNoticeById(long noticeId) throws SQLException {
        String sql = "SELECT * FROM notices WHERE notice_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, noticeId);

            try (ResultSet rs = pstmt.executeQuery()) {
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

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, n.getTitle());
            pstmt.setString(2, n.getContent());
            pstmt.setString(3, n.getNoticeType());
            pstmt.setString(4, n.getTargetRole());
            if (n.getDepartmentId() != null) pstmt.setInt(5, n.getDepartmentId()); else pstmt.setNull(5, Types.INTEGER);
            if (n.getPublishedBy() != null) pstmt.setLong(6, n.getPublishedBy()); else pstmt.setNull(6, Types.BIGINT);
            pstmt.setTimestamp(7, n.getExpiresAt());
            pstmt.setString(8, n.getStatus() != null ? n.getStatus() : "Published");

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
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

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, n.getTitle());
            pstmt.setString(2, n.getContent());
            pstmt.setString(3, n.getNoticeType());
            pstmt.setString(4, n.getTargetRole());
            if (n.getDepartmentId() != null) pstmt.setInt(5, n.getDepartmentId()); else pstmt.setNull(5, Types.INTEGER);
            pstmt.setTimestamp(6, n.getExpiresAt());
            pstmt.setString(7, n.getStatus());
            pstmt.setLong(8, n.getNoticeId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteNotice(long noticeId) throws SQLException {
        String sql = "DELETE FROM notices WHERE notice_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, noticeId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateNoticeStatus(long noticeId, String status) throws SQLException {
        String sql = "UPDATE notices SET status = ? WHERE notice_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setLong(2, noticeId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
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
