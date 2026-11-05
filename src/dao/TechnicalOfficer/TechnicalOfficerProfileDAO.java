package dao.TechnicalOfficer;

import model.TechnicalOfficer;
import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * [OOP - ABSTRACTION]: Data Access Object for Technical Officer profile operations.
 * Isolates and abstracts database persistence for officer records across both 
 * the 'users' and 'technical_officers' tables.
 */
public class TechnicalOfficerProfileDAO {

    /**
     * [OOP - ABSTRACTION]: Queries joined user and technical_officer tables 
     * and maps the result into a polymorphic TechnicalOfficer instance.
     *
     * @param userId The user ID of the Technical Officer
     * @return Populated TechnicalOfficer object, or null if no matching officer is found
     * @throws SQLException if a query execution error occurs
     */
    public TechnicalOfficer getProfile(long userId) throws SQLException {

        String sql =
                "SELECT u.user_id, u.username, u.full_name, u.nic, " +
                        "u.date_of_birth, u.gender, u.email, u.contact_no, " +
                        "u.address, u.profile_picture, u.department_id, u.user_status, " +
                        "t.technical_officer_code, t.designation, t.assigned_labs " +
                        "FROM users u " +
                        "JOIN technical_officers t ON u.user_id = t.user_id " +
                        "WHERE u.user_id = ? AND u.role = 'Technical Officer'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    TechnicalOfficer officer = new TechnicalOfficer();

                    officer.setUserId(rs.getLong("user_id"));
                    officer.setUsername(rs.getString("username"));
                    officer.setFullName(rs.getString("full_name"));
                    officer.setNic(rs.getString("nic"));
                    officer.setDateOfBirth(rs.getDate("date_of_birth"));
                    officer.setGender(rs.getString("gender"));
                    officer.setEmail(rs.getString("email"));
                    officer.setContactNo(rs.getString("contact_no"));
                    officer.setAddress(rs.getString("address"));
                    officer.setProfilePicture(rs.getString("profile_picture"));
                    officer.setDepartmentId((Integer) rs.getObject("department_id"));
                    officer.setUserStatus(rs.getString("user_status"));

                    officer.setTechnicalOfficerCode(rs.getString("technical_officer_code"));
                    officer.setDesignation(rs.getString("designation"));
                    officer.setAssignedLabs(rs.getString("assigned_labs"));

                    return officer;
                }
            }
        }

        return null;
    }

    /**
     * [OOP - ABSTRACTION]: Abstracts the multi-table UPDATE query for user and technical_officers tables.
     *
     * @param userId       The user ID of the Technical Officer
     * @param fullName     Updated full name
     * @param email        Updated email address
     * @param contactNo    Updated phone number
     * @param address      Updated residential address
     * @param assignedLabs Updated list/description of assigned labs
     * @return true if the database row was successfully updated
     * @throws SQLException if an update error occurs
     */
    public boolean updateProfile(
            long userId,
            String fullName,
            String email,
            String contactNo,
            String address,
            String assignedLabs) throws SQLException {

        String sql =
                "UPDATE users u " +
                        "JOIN technical_officers t ON u.user_id = t.user_id " +
                        "SET u.full_name = ?, u.email = ?, u.contact_no = ?, " +
                        "u.address = ?, t.assigned_labs = ? " +
                        "WHERE u.user_id = ? " +
                        "AND u.role = 'Technical Officer'";

        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, contactNo);
            stmt.setString(4, address);
            stmt.setString(5, assignedLabs);
            stmt.setLong(6, userId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * [OOP - ABSTRACTION]: Updates the profile picture path or URL in the users table.
     *
     * @param userId         The user ID of the Technical Officer
     * @param profilePicture The image file path or URL (or null to remove)
     * @return true if updated successfully
     * @throws SQLException if a database error occurs
     */
    public boolean updateProfilePicture(long userId, String profilePicture) throws SQLException {
        String sql = "UPDATE users SET profile_picture = ? WHERE user_id = ? AND role = 'Technical Officer'";
        try (PreparedStatement stmt = DBConnection.TO_CON.prepareStatement(sql)) {
            stmt.setString(1, profilePicture);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
