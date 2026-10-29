
package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerProfileDAO {

    // READ: Get the lecturer's profile.
    public Object[] getLecturerProfile(long userId)
            throws SQLException {

        String sql = "SELECT u.user_id, u.username, l.lecturer_code, " +
                "l.title, u.full_name, u.nic, u.email, u.contact_no, " +
                "u.address, l.specialization, d.department_name " +
                "FROM users u " +
                "JOIN lecturers l ON u.user_id = l.user_id " +
                "LEFT JOIN departments d ON u.department_id = d.department_id " +
                "WHERE u.user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Object[]{
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("lecturer_code"),
                            rs.getString("title") == null
                                    ? "Dr." : rs.getString("title"),
                            rs.getString("full_name"),
                            rs.getString("nic") == null
                                    ? "" : rs.getString("nic"),
                            rs.getString("email"),
                            rs.getString("contact_no") == null
                                    ? "" : rs.getString("contact_no"),
                            rs.getString("address") == null
                                    ? "" : rs.getString("address"),
                            rs.getString("specialization") == null
                                    ? "" : rs.getString("specialization"),
                            rs.getString("department_name") == null
                                    ? "Faculty of Technology"
                                    : rs.getString("department_name")
                    };
                }
            }
        }

        return null;
    }

    // UPDATE: Update the user's and lecturer's details.
    public boolean updateLecturerProfile(
            long userId,
            String title,
            String fullName,
            String nic,
            String email,
            String contactNo,
            String address,
            String specialization) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String checkSql = "SELECT 1 FROM users u " +
                "JOIN lecturers l ON u.user_id = l.user_id " +
                "WHERE u.user_id = ?";

        String userSql = "UPDATE users SET full_name = ?, nic = ?, " +
                "email = ?, contact_no = ?, address = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        String lecturerSql = "UPDATE lecturers SET title = ?, " +
                "specialization = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            boolean originalAutoCommit = con.getAutoCommit();

            try {
                con.setAutoCommit(false);

                // Check that this user is a lecturer.
                try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                    ps.setLong(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                // Update the users table.
                try (PreparedStatement ps = con.prepareStatement(userSql)) {

                    ps.setString(1, fullName.trim());
                    ps.setString(2, nic);
                    ps.setString(3, email.trim());
                    ps.setString(4, contactNo);
                    ps.setString(5, address);
                    ps.setLong(6, userId);

                    ps.executeUpdate();
                }

                // Update the lecturers table.
                try (PreparedStatement ps =
                             con.prepareStatement(lecturerSql)) {

                    ps.setString(1, title);
                    ps.setString(2, specialization);
                    ps.setLong(3, userId);

                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {

                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;

            } finally {
                con.setAutoCommit(originalAutoCommit);
            }
        }
    }
}
