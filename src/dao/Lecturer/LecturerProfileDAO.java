
package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerProfileDAO {

    // READ: Retrieve the logged-in lecturer's profile.
    public Object[] getLecturerProfile(long userId)
            throws SQLException {

        String sql =
                "SELECT u.user_id, u.username, l.lecturer_code, " +
                        "l.title, u.full_name, u.nic, u.email, " +
                        "u.contact_no, u.address, l.specialization, " +
                        "d.department_name " +
                        "FROM users u " +
                        "JOIN lecturers l ON u.user_id = l.user_id " +
                        "LEFT JOIN departments d " +
                        "ON u.department_id = d.department_id " +
                        "WHERE u.user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("lecturer_code"),
                            rs.getString("title") == null
                                    ? "Dr."
                                    : rs.getString("title"),
                            rs.getString("full_name"),
                            rs.getString("nic") == null
                                    ? ""
                                    : rs.getString("nic"),
                            rs.getString("email"),
                            rs.getString("contact_no") == null
                                    ? ""
                                    : rs.getString("contact_no"),
                            rs.getString("address") == null
                                    ? ""
                                    : rs.getString("address"),
                            rs.getString("specialization") == null
                                    ? ""
                                    : rs.getString("specialization"),
                            rs.getString("department_name") == null
                                    ? "Faculty of Technology"
                                    : rs.getString("department_name")
                    };
                }
            }
        }

        return null;
    }

    // UPDATE: Update user and lecturer details together.
    public boolean updateLecturerProfile(
            long userId,
            String title,
            String fullName,
            String nic,
            String email,
            String contactNo,
            String address,
            String specialization
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Full name is required.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email is required.");
        }

        String checkSql =
                "SELECT 1 FROM users u " +
                        "INNER JOIN lecturers l ON u.user_id = l.user_id " +
                        "WHERE u.user_id = ?";

        String userSql =
                "UPDATE users SET " +
                        "full_name = ?, nic = ?, email = ?, " +
                        "contact_no = ?, address = ?, " +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE user_id = ?";

        String lecturerSql =
                "UPDATE lecturers SET title = ?, specialization = ? " +
                        "WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                // Confirm this user is a lecturer before updating.
                try (PreparedStatement checkStatement =
                             connection.prepareStatement(checkSql)) {

                    checkStatement.setLong(1, userId);

                    try (ResultSet rs =
                                 checkStatement.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Update general user information.
                try (PreparedStatement userStatement =
                             connection.prepareStatement(userSql)) {

                    userStatement.setString(1, fullName.trim());
                    userStatement.setString(2, nic);
                    userStatement.setString(3, email.trim());
                    userStatement.setString(4, contactNo);
                    userStatement.setString(5, address);
                    userStatement.setLong(6, userId);

                    userStatement.executeUpdate();
                }

                // Update lecturer-specific information.
                try (PreparedStatement lecturerStatement =
                             connection.prepareStatement(lecturerSql)) {

                    lecturerStatement.setString(1, title);
                    lecturerStatement.setString(2, specialization);
                    lecturerStatement.setLong(3, userId);

                    lecturerStatement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;

            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }
}
