
package dao.Lecturer;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerProfileDAO {

    /**
     * Object[] indexes:
     *
     * 0  - user_id
     * 1  - username
     * 2  - lecturer_code
     * 3  - title
     * 4  - full_name
     * 5  - nic
     * 6  - email
     * 7  - contact_no
     * 8  - address
     * 9  - specialization
     * 10 - department_name (unavailable in current schema)
     * 11 - profile_picture
     * 12 - residence_address
     */

    public Object[] getLecturerProfile(long userId)
            throws SQLException {

        String sql =
                "SELECT " +
                        "u.user_id, " +
                        "u.username, " +
                        "l.lecturer_code, " +
                        "l.title, " +
                        "u.full_name, " +
                        "u.nic, " +
                        "u.email, " +
                        "u.contact_no, " +
                        "u.address, " +
                        "l.specialization, " +
                        "NULL AS department_name, " +
                        "u.profile_picture, " +
                        "u.residence_address " +
                        "FROM users u " +
                        "INNER JOIN lecturers l ON u.user_id = l.user_id " +
                        "WHERE u.user_id = ?";

        try (PreparedStatement pst =
                     DBConnection.LEC_CON.prepareStatement(sql)) {

            pst.setLong(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    Object[] profile = new Object[13];

                    profile[0] = rs.getLong("user_id");
                    profile[1] = rs.getString("username");
                    profile[2] = rs.getString("lecturer_code");
                    profile[3] = rs.getString("title");
                    profile[4] = rs.getString("full_name");
                    profile[5] = rs.getString("nic");
                    profile[6] = rs.getString("email");
                    profile[7] = rs.getString("contact_no");
                    profile[8] = rs.getString("address");
                    profile[9] = rs.getString("specialization");
                    profile[10] = rs.getString("department_name");
                    profile[11] = rs.getString("profile_picture");
                    profile[12] = rs.getString("residence_address");

                    return profile;
                }
            }
        }

        return null;
    }

    /**
     * Compatibility method for older calls.
     * NIC is ignored and never updated.
     */
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

        Object[] profile = getLecturerProfile(userId);

        if (profile == null) {
            return false;
        }

        return updateLecturerProfile(
                userId,
                title,
                fullName,
                email,
                contactNo,
                address,
                (String) profile[12],
                specialization,
                (String) profile[11]
        );
    }

    /**
     * Main method used by the current LecturerProfile GUI.
     * NIC is never updated.
     */
    public boolean updateLecturerProfile(
            long userId,
            String title,
            String fullName,
            String email,
            String contactNo,
            String address,
            String residenceAddress,
            String specialization,
            String profilePicture
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Full name cannot be empty."
            );
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty."
            );
        }

        if (profilePicture != null && profilePicture.length() > 500) {
            throw new IllegalArgumentException(
                    "Profile picture URL cannot exceed 500 characters."
            );
        }

        Connection connection = DBConnection.LEC_CON;

        if (connection == null || connection.isClosed()) {
            throw new SQLException(
                    "Database connection is unavailable."
            );
        }

        boolean originalAutoCommit = connection.getAutoCommit();

        String checkSql =
                "SELECT user_id FROM lecturers WHERE user_id = ?";

        String userSql =
                "UPDATE users SET " +
                        "full_name = ?, " +
                        "email = ?, " +
                        "contact_no = ?, " +
                        "address = ?, " +
                        "residence_address = ?, " +
                        "profile_picture = ? " +
                        "WHERE user_id = ?";

        String lecturerSql =
                "UPDATE lecturers SET " +
                        "title = ?, " +
                        "specialization = ? " +
                        "WHERE user_id = ?";

        try {

            connection.setAutoCommit(false);

            // Check lecturer account.
            try (PreparedStatement pst =
                         connection.prepareStatement(checkSql)) {

                pst.setLong(1, userId);

                try (ResultSet rs = pst.executeQuery()) {

                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // Update users table.
            // NIC is intentionally excluded.
            try (PreparedStatement pst =
                         connection.prepareStatement(userSql)) {

                pst.setString(1, fullName.trim());
                pst.setString(2, email.trim());
                pst.setString(3, contactNo);
                pst.setString(4, address);
                pst.setString(5, residenceAddress);
                pst.setString(6, profilePicture);
                pst.setLong(7, userId);

                if (pst.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Update lecturers table.
            try (PreparedStatement pst =
                         connection.prepareStatement(lecturerSql)) {

                pst.setString(1, title);
                pst.setString(2, specialization);
                pst.setLong(3, userId);

                if (pst.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (SQLException | RuntimeException e) {

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
