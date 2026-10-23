package dao.Lecturer;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerProfileDAO {

    // Retrieve profile details for the logged-in lecturer
    public Object[] getLecturerProfile(long userId) throws SQLException {
        String sql =
                "SELECT u.user_id, u.username, l.lecturer_code, l.title, u.full_name, " +
                "u.nic, u.email, u.contact_no, u.address, l.specialization, d.department_name " +
                "FROM users u " +
                "JOIN lecturers l ON u.user_id = l.user_id " +
                "LEFT JOIN departments d ON u.department_id = d.department_id " +
                "WHERE u.user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("lecturer_code"),
                            rs.getString("title") == null ? "Dr." : rs.getString("title"),
                            rs.getString("full_name"),
                            rs.getString("nic") == null ? "" : rs.getString("nic"),
                            rs.getString("email"),
                            rs.getString("contact_no") == null ? "" : rs.getString("contact_no"),
                            rs.getString("address") == null ? "" : rs.getString("address"),
                            rs.getString("specialization") == null ? "" : rs.getString("specialization"),
                            rs.getString("department_name") == null ? "Faculty of Technology" : rs.getString("department_name")
                    };
                }
            }
        }

        return null;
    }

    // Update profile details for the lecturer (Username and password remain unchanged as per spec)
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

        String userSql =
                "UPDATE users SET " +
                "full_name = ?, nic = ?, email = ?, contact_no = ?, address = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ?";

        String lecturerSql =
                "UPDATE lecturers SET " +
                "title = ?, specialization = ? " +
                "WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement uStmt = connection.prepareStatement(userSql)) {
                    uStmt.setString(1, fullName);
                    uStmt.setString(2, nic);
                    uStmt.setString(3, email);
                    uStmt.setString(4, contactNo);
                    uStmt.setString(5, address);
                    uStmt.setLong(6, userId);
                    uStmt.executeUpdate();
                }

                try (PreparedStatement lStmt = connection.prepareStatement(lecturerSql)) {
                    lStmt.setString(1, title);
                    lStmt.setString(2, specialization);
                    lStmt.setLong(3, userId);
                    lStmt.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
