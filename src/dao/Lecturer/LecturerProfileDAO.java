package dao.Lecturer;

import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerProfileDAO {

    /**
     * Retrieve lecturer profile details.
     *
     * Object[] indexes:
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
     * 10 - department_name
     * 11 - profile_picture
     */
    public Object[] getLecturerProfile(long userId)
            throws SQLException {

        String sql = "SELECT u.user_id, u.username, l.lecturer_code, " +
                "l.title, u.full_name, u.nic, u.email, u.contact_no, " +
                "u.address, l.specialization, d.department_name, " +
                "u.profile_picture " +
                "FROM users u " +
                "JOIN lecturers l ON u.user_id = l.user_id " +
                "LEFT JOIN departments d " +
                "ON u.department_id = d.department_id " +
                "WHERE u.user_id = ?";

        try (PreparedStatement ps = DBConnection.LEC_CON.prepareStatement(sql)) {

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
                                    : rs.getString("department_name"),
                            rs.getString("profile_picture") == null
                                    ? "" : rs.getString("profile_picture")
                    };
                }
            }
        }

        return null;
    }

    /**
     * Backward-compatible update method.
     * Keeps the currently saved profile picture unchanged.
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

        Object[] currentProfile = getLecturerProfile(userId);

        if (currentProfile == null) {
            return false;
        }

        String existingPicture = currentProfile.length > 11
                && currentProfile[11] != null
                ? currentProfile[11].toString()
                : "";

        return updateLecturerProfile(
                userId,
                title,
                fullName,
                nic,
                email,
                contactNo,
                address,
                specialization,
                existingPicture
        );
    }

    /**
     * Update lecturer profile details and profile picture URL.
     *
     * Pass an empty string to remove the profile picture.
     */
    public boolean updateLecturerProfile(
            long userId,
            String title,
            String fullName,
            String nic,
            String email,
            String contactNo,
            String address,
            String specialization,
            String profilePicture
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (profilePicture != null && profilePicture.length() > 500) {
            throw new IllegalArgumentException(
                    "Profile picture URL must be 500 characters or less."
            );
        }

        String checkSql =
                "SELECT 1 FROM users u " +
                        "JOIN lecturers l ON u.user_id = l.user_id " +
                        "WHERE u.user_id = ?";

        // updated_at is not set here: it refreshes itself through
        // ON UPDATE CURRENT_TIMESTAMP, and the lecturer account has no
        // UPDATE privilege on that column.
        String userSql =
                "UPDATE users SET " +
                        "full_name = ?, nic = ?, email = ?, " +
                        "contact_no = ?, address = ?, " +
                        "profile_picture = ? " +
                        "WHERE user_id = ?";

        String lecturerSql =
                "UPDATE lecturers SET title = ?, " +
                        "specialization = ? " +
                        "WHERE user_id = ?";

        DBConnection.LEC_CON.setAutoCommit(false);

        try {

            // Check whether this user is a lecturer.
            try (PreparedStatement ps =
                         DBConnection.LEC_CON.prepareStatement(checkSql)) {

                ps.setLong(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        DBConnection.LEC_CON.rollback();
                        return false;
                    }
                }
            }

            // Update users table.
            try (PreparedStatement ps =
                         DBConnection.LEC_CON.prepareStatement(userSql)) {

                ps.setString(1, fullName.trim());
                ps.setString(2, nic);
                ps.setString(3, email.trim());
                ps.setString(4, contactNo);
                ps.setString(5, address);

                // Empty string removes the picture.
                ps.setString(
                        6,
                        profilePicture == null
                                ? "" : profilePicture.trim()
                );

                ps.setLong(7, userId);

                int userRows = ps.executeUpdate();

                if (userRows == 0) {
                    DBConnection.LEC_CON.rollback();
                    return false;
                }
            }

            // Update lecturers table.
            try (PreparedStatement ps =
                         DBConnection.LEC_CON.prepareStatement(lecturerSql)) {

                ps.setString(1, title);
                ps.setString(2, specialization);
                ps.setLong(3, userId);

                int lecturerRows = ps.executeUpdate();

                if (lecturerRows == 0) {
                    DBConnection.LEC_CON.rollback();
                    return false;
                }
            }

            DBConnection.LEC_CON.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            DBConnection.LEC_CON.rollback();
            throw e;
        } finally {
            DBConnection.LEC_CON.setAutoCommit(true);
        }
    }
}