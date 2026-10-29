
package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    // READ: Get the five latest published notices.
    public List<String[]> getRecentNotices() throws SQLException {

        List<String[]> notices = new ArrayList<>();

        String sql = "SELECT title, content FROM notices " +
                "WHERE status = 'Published' " +
                "AND target_role IN ('All', 'Lecturer') " +
                "AND (expires_at IS NULL " +
                "OR expires_at >= CURRENT_TIMESTAMP) " +
                "ORDER BY published_at DESC LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String title = rs.getString("title");
                String content = rs.getString("content");

                String[] notice = {
                        title == null ? "Untitled Notice" : title,
                        content == null ? "" : content
                };

                notices.add(notice);
            }
        }

        return notices;
    }

    // READ: Get notices using an optional type and search filter.
    public List<Object[]> getAllNotices(
            String typeFilter,
            String searchQuery) throws SQLException {

        List<Object[]> notices = new ArrayList<>();

        String sql = "SELECT notice_id, title, content, notice_type, " +
                "target_role, published_at FROM notices " +
                "WHERE status = 'Published' " +
                "AND target_role IN ('All', 'Lecturer') " +
                "AND (expires_at IS NULL " +
                "OR expires_at >= CURRENT_TIMESTAMP)";

        boolean filterType = typeFilter != null
                && !typeFilter.equalsIgnoreCase("All");

        boolean filterSearch = searchQuery != null
                && !searchQuery.trim().isEmpty();

        if (filterType) {
            sql += " AND notice_type = ?";
        }

        if (filterSearch) {
            sql += " AND (LOWER(title) LIKE ? OR LOWER(content) LIKE ?)";
        }

        sql += " ORDER BY published_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int position = 1;

            if (filterType) {
                ps.setString(position++, typeFilter);
            }

            if (filterSearch) {
                String search = "%" +
                        searchQuery.trim().toLowerCase() + "%";

                ps.setString(position++, search);
                ps.setString(position++, search);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Object[] notice = {
                            rs.getLong("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("notice_type"),
                            rs.getString("target_role"),
                            rs.getTimestamp("published_at")
                    };

                    notices.add(notice);
                }
            }
        }

        return notices;
    }
}
