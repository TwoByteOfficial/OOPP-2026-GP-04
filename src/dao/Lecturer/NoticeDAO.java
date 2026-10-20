
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
}
