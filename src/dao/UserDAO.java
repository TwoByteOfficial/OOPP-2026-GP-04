package dao;

import util.DBConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Authenticates user against MySQL DB using Username, Registration Number, or Staff Code.
     * Supports case-insensitive matches for student registration numbers like TG/2024/2061 or tg2061.
     */
    public User authenticate(String identifier, String password) throws SQLException {
        if (identifier == null || identifier.trim().isEmpty() || password == null) {
            return null;
        }

        String sql = "SELECT * FROM v_users u JOIN users usr ON u.user_id = usr.user_id " +
                     "WHERE (LOWER(u.username) = LOWER(?) " +
                     "   OR LOWER(u.registration_no) = LOWER(?) " +
                     "   OR LOWER(REPLACE(u.registration_no, '/', '')) = LOWER(REPLACE(?, '/', '')) " +
                     "   OR LOWER(u.admin_code) = LOWER(?) " +
                     "   OR LOWER(u.lecturer_code) = LOWER(?) " +
                     "   OR LOWER(u.technical_officer_code) = LOWER(?)) " +
                     "  AND usr.password_hash = ? AND u.user_status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String trimmed = identifier.trim();
            stmt.setString(1, trimmed);
            stmt.setString(2, trimmed);
            stmt.setString(3, trimmed);
            stmt.setString(4, trimmed);
            stmt.setString(5, trimmed);
            stmt.setString(6, trimmed);
            stmt.setString(7, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM v_users u JOIN users usr ON u.user_id = usr.user_id ORDER BY u.user_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        }
        return list;
    }

    public List<Undergraduate> getAllUndergraduates() throws SQLException {
        List<Undergraduate> list = new ArrayList<>();
        String sql = "SELECT * FROM v_users u JOIN users usr ON u.user_id = usr.user_id " +
                     "JOIN undergraduates ug ON ug.user_id = u.user_id WHERE u.role = 'Undergraduate'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add((Undergraduate) mapResultSetToUser(rs));
            }
        }
        return list;
    }

    public List<Lecturer> getAllLecturers() throws SQLException {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM v_users u JOIN users usr ON u.user_id = usr.user_id " +
                     "JOIN lecturers l ON l.user_id = u.user_id WHERE u.role = 'Lecturer'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add((Lecturer) mapResultSetToUser(rs));
            }
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User u;

        if ("Admin".equalsIgnoreCase(role)) {
            Admin a = new Admin();
            a.setAdminCode(rs.getString("admin_code"));
            u = a;
        } else if ("Lecturer".equalsIgnoreCase(role)) {
            Lecturer l = new Lecturer();
            l.setLecturerCode(rs.getString("lecturer_code"));
            u = l;
        } else if ("Technical Officer".equalsIgnoreCase(role)) {
            TechnicalOfficer t = new TechnicalOfficer();
            t.setTechnicalOfficerCode(rs.getString("technical_officer_code"));
            u = t;
        } else if ("Undergraduate".equalsIgnoreCase(role)) {
            Undergraduate ug = new Undergraduate();
            ug.setRegistrationNo(rs.getString("registration_no"));
            ug.setBatchYear(rs.getInt("batch_year"));
            ug.setStudentType(rs.getString("student_type"));
            u = ug;
        } else {
            u = new User();
        }

        u.setUserId(rs.getLong("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setNic(rs.getString("nic"));
        u.setEmail(rs.getString("email"));
        u.setContactNo(rs.getString("contact_no"));
        u.setAddress(rs.getString("address"));
        u.setProfilePicture(rs.getString("profile_picture"));
        u.setDepartmentId((Integer) rs.getObject("department_id"));
        u.setUserStatus(rs.getString("user_status"));
        u.setRole(role);

        return u;
    }
}
