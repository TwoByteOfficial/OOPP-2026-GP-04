package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public List<String[]> getRecentNotices() throws SQLException {

        List<String[]> notices = new ArrayList<>();

        String sql =
                "SELECT title, content " +
                        "FROM notices " +
                        "WHERE status = 'Published' " +
                        "AND target_role IN ('All', 'Lecturer') " +
                        "AND (expires_at IS NULL OR expires_at >= CURRENT_TIMESTAMP) " +
                        "ORDER BY published_at DESC " +
                        "LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String content = rs.getString("content");

                notices.add(new String[]{
                        title == null ? "Untitled Notice" : title,
                        content == null ? "" : content
                });
            }
        }

        return notices;
    }

    public List<Object[]> getAllNotices(String typeFilter, String searchQuery) throws SQLException {

        List<Object[]> notices = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT notice_id, title, content, notice_type, target_role, published_at " +
                "FROM notices " +
                "WHERE status = 'Published' " +
                "AND target_role IN ('All', 'Lecturer') " +
                "AND (expires_at IS NULL OR expires_at >= CURRENT_TIMESTAMP) "
        );

        if (typeFilter != null && !typeFilter.equalsIgnoreCase("All")) {
            sql.append("AND notice_type = ? ");
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (LOWER(title) LIKE ? OR LOWER(content) LIKE ?) ");
        }

        sql.append("ORDER BY published_at DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (typeFilter != null && !typeFilter.equalsIgnoreCase("All")) {
                ps.setString(paramIndex++, typeFilter);
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String pattern = "%" + searchQuery.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notices.add(new Object[]{
                            rs.getLong("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("notice_type"),
                            rs.getString("target_role"),
                            rs.getTimestamp("published_at")
                    });
                }
            }
        }

        return notices;
    }
}
