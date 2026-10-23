package dao;

import model.Notice;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public boolean insert(Notice n) {

        String sql = """
                INSERT INTO notices
                (title, content, notice_type, target_role,
                 department_id, published_by, expires_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, n.getTitle());
            ps.setString(2, n.getContent());
            ps.setString(3, n.getNoticeType());
            ps.setString(4, n.getTargetRole());

            if (n.getDepartmentId() != null)
                ps.setInt(5, n.getDepartmentId());
            else
                ps.setNull(5, Types.INTEGER);

            if (n.getPublishedBy() != null)
                ps.setLong(6, n.getPublishedBy());
            else
                ps.setNull(6, Types.BIGINT);

            if (n.getExpiresAt() != null)
                ps.setTimestamp(7, n.getExpiresAt());
            else
                ps.setNull(7, Types.TIMESTAMP);

            ps.setString(8, n.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Notice findById(Long id) {

        String sql = "SELECT * FROM notices WHERE notice_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Notice> findAll() {

        List<Notice> list = new ArrayList<>();

        String sql = "SELECT * FROM notices ORDER BY notice_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapResultSet(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(Notice n) {

        String sql = """
                UPDATE notices
                SET title = ?, content = ?, notice_type = ?,
                    target_role = ?, department_id = ?, published_by = ?,
                    expires_at = ?, status = ?
                WHERE notice_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, n.getTitle());
            ps.setString(2, n.getContent());
            ps.setString(3, n.getNoticeType());
            ps.setString(4, n.getTargetRole());

            if (n.getDepartmentId() != null)
                ps.setInt(5, n.getDepartmentId());
            else
                ps.setNull(5, Types.INTEGER);

            if (n.getPublishedBy() != null)
                ps.setLong(6, n.getPublishedBy());
            else
                ps.setNull(6, Types.BIGINT);

            if (n.getExpiresAt() != null)
                ps.setTimestamp(7, n.getExpiresAt());
            else
                ps.setNull(7, Types.TIMESTAMP);

            ps.setString(8, n.getStatus());
            ps.setLong(9, n.getNoticeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {

        String sql = "DELETE FROM notices WHERE notice_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Notice mapResultSet(ResultSet rs) throws SQLException {

        return new Notice(
                rs.getLong("notice_id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("notice_type"),
                rs.getString("target_role"),
                (Integer) rs.getObject("department_id"),
                (Long) rs.getObject("published_by"),
                rs.getTimestamp("published_at"),
                rs.getTimestamp("expires_at"),
                rs.getString("status")
        );
    }
}