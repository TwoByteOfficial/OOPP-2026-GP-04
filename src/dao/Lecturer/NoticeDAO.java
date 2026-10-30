
package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    /**
     * Get the five latest notices visible to a lecturer.
     *
     * Object[] indexes:
     * 0 - notice_id
     * 1 - title
     * 2 - content
     * 3 - notice_type
     * 4 - published_at
     * 5 - publisher_name
     */
    public List<Object[]> getRecentNotices(long lecturerId)
            throws SQLException {

        List<Object[]> notices = new ArrayList<>();

        String sql =
                "SELECT n.notice_id, n.title, n.content, " +
                        "n.notice_type, n.published_at, " +
                        "COALESCE(u.full_name, 'Faculty Administration') " +
                        "AS publisher_name " +
                        "FROM notices n " +
                        "LEFT JOIN users u ON n.published_by = u.user_id " +
                        "WHERE n.status = 'Published' " +
                        "AND n.target_role IN ('All', 'Lecturer') " +
                        "AND (n.expires_at IS NULL " +
                        "OR n.expires_at > CURRENT_TIMESTAMP) " +
                        "AND (n.department_id IS NULL OR n.department_id = " +
                        "(SELECT department_id FROM users WHERE user_id = ?)) " +
                        "ORDER BY n.published_at DESC " +
                        "LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Object[] notice = {
                            rs.getLong("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("notice_type"),
                            rs.getTimestamp("published_at"),
                            rs.getString("publisher_name")
                    };

                    notices.add(notice);
                }
            }
        }

        return notices;
    }

    /**
     * Get notices using an optional type and search filter.
     */
    public List<Object[]> getAllNotices(
            String typeFilter,
            String searchQuery) throws SQLException {

        List<Object[]> notices = new ArrayList<>();

        String sql =
                "SELECT notice_id, title, content, notice_type, " +
                        "target_role, published_at FROM notices " +
                        "WHERE status = 'Published' " +
                        "AND target_role IN ('All', 'Lecturer') " +
                        "AND (expires_at IS NULL " +
                        "OR expires_at > CURRENT_TIMESTAMP)";

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
                String search =
                        "%" + searchQuery.trim().toLowerCase() + "%";

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
