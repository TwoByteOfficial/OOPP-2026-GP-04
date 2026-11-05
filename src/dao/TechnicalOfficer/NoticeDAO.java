package dao.TechnicalOfficer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * [OOP - ABSTRACTION]: Data Access Object for retrieving campus notices.
 * Encapsulates the SQL query filtering logic (role targeting, department matching, and expiration date checks).
 */
public class NoticeDAO {

    /**
     * [OOP - ABSTRACTION]: Retrieves all active notices targeted to Technical Officers.
     *
     * @param userId The user ID of the logged-in Technical Officer
     * @return List of Object arrays representing notice records sorted by newest published first
     * @throws SQLException if a database query error occurs
     */
    public List<Object[]> getNoticesForTechnicalOfficer(
            long userId) throws SQLException {

        List<Object[]> notices = new ArrayList<>();

        String sql =
                "SELECT n.notice_id, n.title, n.content, " +
                        "n.notice_type, n.target_role, n.published_at, " +
                        "n.expires_at, n.status " +
                        "FROM notices n " +
                        "JOIN users u ON u.user_id = ? " +
                        "WHERE n.status = 'Published' " +
                        "AND (n.target_role = 'All' " +
                        "OR n.target_role = 'Technical Officer') " +
                        "AND (n.department_id IS NULL " +
                        "OR n.department_id = u.department_id) " +
                        "AND (n.expires_at IS NULL " +
                        "OR n.expires_at >= NOW()) " +
                        "ORDER BY n.published_at DESC";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Object[] notice = {
                            rs.getLong("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("notice_type"),
                            rs.getString("target_role"),
                            rs.getTimestamp("published_at"),
                            rs.getTimestamp("expires_at"),
                            rs.getString("status")
                    };

                    notices.add(notice);
                }
            }
        }
        return notices;
    }
}
