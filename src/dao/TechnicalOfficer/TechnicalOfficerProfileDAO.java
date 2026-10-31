package dao.TechnicalOfficer;

import model.TechnicalOfficer;
import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TechnicalOfficerProfileDAO {

    public TechnicalOfficer getProfile(long userId) throws SQLException {

        String sql =
                "SELECT u.user_id, u.username, u.full_name, u.nic, " +
                        "u.date_of_birth, u.gender, u.email, u.contact_no, " +
                        "u.address, u.department_id, u.user_status, " +
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
                    officer.setDepartmentId(
                            (Integer) rs.getObject("department_id"));
                    officer.setUserStatus(rs.getString("user_status"));

                    officer.setTechnicalOfficerCode(
                            rs.getString("technical_officer_code"));
                    officer.setDesignation(rs.getString("designation"));
                    officer.setAssignedLabs(rs.getString("assigned_labs"));

                    return officer;
                }
            }
        }

        return null;
    }

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
}