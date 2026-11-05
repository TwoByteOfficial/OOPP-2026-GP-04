
package dao.Lecturer;

import util.DBConnection;

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
                        "ORDER BY n.published_at DESC, n.notice_id DESC " +
                        "LIMIT 5";

        try (PreparedStatement ps =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

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
     * Get all notices visible to a particular lecturer.
     *
     * Object[] indexes:
     * 0 - notice_id
     * 1 - title
     * 2 - content
     * 3 - notice_type
     * 4 - target_role
     * 5 - published_at
     * 6 - publisher_name
     */
    public List<Object[]> getAllNotices(
            long lecturerId,
            String typeFilter,
            String searchQuery
    ) throws SQLException {

        List<Object[]> notices = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(
                "SELECT n.notice_id, n.title, n.content, " +
                        "n.notice_type, n.target_role, n.published_at, " +
                        "COALESCE(u.full_name, 'Faculty Administration') " +
                        "AS publisher_name " +
                        "FROM notices n " +
                        "LEFT JOIN users u ON n.published_by = u.user_id " +
                        "WHERE n.status = 'Published' " +
                        "AND n.target_role IN ('All', 'Lecturer') " +
                        "AND (n.expires_at IS NULL " +
                        "OR n.expires_at > CURRENT_TIMESTAMP) " +
                        "AND (n.department_id IS NULL OR n.department_id = " +
                        "(SELECT department_id FROM users WHERE user_id = ?)) "
        );

        boolean filterType = typeFilter != null
                && !typeFilter.equalsIgnoreCase("All");

        boolean filterSearch = searchQuery != null
                && !searchQuery.trim().isEmpty();

        if (filterType) {
            sql.append("AND n.notice_type = ? ");
        }

        if (filterSearch) {
            sql.append(
                    "AND (LOWER(n.title) LIKE ? " +
                            "OR LOWER(n.content) LIKE ?) "
            );
        }

        // Latest notices first.
        sql.append(
                "ORDER BY n.published_at DESC, n.notice_id DESC"
        );

        try (PreparedStatement ps =
                     DBConnection.LEC_CON.prepareStatement(sql.toString())) {

            int position = 1;

            // Lecturer ID is required for department filtering.
            ps.setLong(position++, lecturerId);

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
                            rs.getTimestamp("published_at"),
                            rs.getString("publisher_name")
                    };

                    notices.add(notice);
                }
            }
        }

        return notices;
    }
}
