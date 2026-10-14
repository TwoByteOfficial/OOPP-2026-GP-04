package dao;

import util.DBConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Authenticates user against MySQL DB using Username, Registration Number, Email, or Staff Code.
     * Uses the physical 'role' column in the 'users' table directly for role resolution.
     */
    public User authenticate(String identifier, String password) throws SQLException {
        if (identifier == null || identifier.trim().isEmpty() || password == null) {
            return null;
        }

        String sql = "SELECT u.*, " +
                     "a.admin_code, " +
                     "l.lecturer_code, " +
                     "t.technical_officer_code, " +
                     "ug.registration_no, ug.batch_year, ug.student_type " +
                     "FROM users u " +
                     "LEFT JOIN admins a ON u.user_id = a.user_id " +
                     "LEFT JOIN lecturers l ON u.user_id = l.user_id " +
                     "LEFT JOIN technical_officers t ON u.user_id = t.user_id " +
                     "LEFT JOIN undergraduates ug ON u.user_id = ug.user_id " +
                     "WHERE (LOWER(u.username) = LOWER(?) " +
                     "   OR LOWER(u.email) = LOWER(?) " +
                     "   OR LOWER(ug.registration_no) = LOWER(?) " +
                     "   OR LOWER(REPLACE(ug.registration_no, '/', '')) = LOWER(REPLACE(?, '/', '')) " +
                     "   OR LOWER(a.admin_code) = LOWER(?) " +
                     "   OR LOWER(l.lecturer_code) = LOWER(?) " +
                     "   OR LOWER(t.technical_officer_code) = LOWER(?)) " +
                     "  AND u.password_hash = ? AND u.user_status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String trimmed = identifier.trim();
            stmt.setString(1, trimmed);
            stmt.setString(2, trimmed);
            stmt.setString(3, trimmed);
            stmt.setString(4, trimmed);
            stmt.setString(5, trimmed);
            stmt.setString(6, trimmed);
            stmt.setString(7, trimmed);
            stmt.setString(8, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Authenticates strictly checking the 'users' table directly for username/email and password.
     * Gets the user's role directly from the 'role' ENUM column in 'users'.
     */
    public User authenticateUserOnly(String usernameOrEmail, String password) throws SQLException {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || password == null) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE (LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)) " +
                     "AND password_hash = ? AND user_status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String trimmed = usernameOrEmail.trim();
            stmt.setString(1, trimmed);
            stmt.setString(2, trimmed);
            stmt.setString(3, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getLong("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRole(rs.getString("role"));
                    u.setFullName(rs.getString("full_name"));
                    u.setNic(rs.getString("nic"));
                    u.setDateOfBirth(rs.getDate("date_of_birth"));
                    u.setGender(rs.getString("gender"));
                    u.setEmail(rs.getString("email"));
                    u.setContactNo(rs.getString("contact_no"));
                    u.setAddress(rs.getString("address"));
                    u.setProfilePicture(rs.getString("profile_picture"));
                    u.setDepartmentId((Integer) rs.getObject("department_id"));
                    u.setUserStatus(rs.getString("user_status"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    u.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return u;
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM v_users ORDER BY user_id";
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
        String sql = "SELECT * FROM v_users WHERE role = 'Undergraduate' ORDER BY registration_no";
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
        String sql = "SELECT * FROM v_users WHERE role = 'Lecturer' ORDER BY lecturer_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add((Lecturer) mapResultSetToUser(rs));
            }
        }
        return list;
    }

    /**
     * Inserts a new user record into the 'users' table including the physical 'role' column.
     */
    public boolean createUser(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPasswordHash());
            stmt.setString(3, u.getRole());
            stmt.setString(4, u.getFullName());
            stmt.setString(5, u.getNic());
            stmt.setDate(6, u.getDateOfBirth());
            stmt.setString(7, u.getGender());
            stmt.setString(8, u.getEmail());
            stmt.setString(9, u.getContactNo());
            stmt.setString(10, u.getAddress());
            if (u.getDepartmentId() != null) stmt.setInt(11, u.getDepartmentId()); else stmt.setNull(11, Types.INTEGER);
            stmt.setString(12, u.getUserStatus() != null ? u.getUserStatus() : "Active");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setUserId(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateUser(User u) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, nic = ?, email = ?, contact_no = ?, address = ?, user_status = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getFullName());
            stmt.setString(2, u.getNic());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getContactNo());
            stmt.setString(5, u.getAddress());
            stmt.setString(6, u.getUserStatus());
            stmt.setString(7, u.getRole());
            stmt.setLong(8, u.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User u;

        if ("Admin".equalsIgnoreCase(role)) {
            Admin a = new Admin();
            try { a.setAdminCode(rs.getString("admin_code")); } catch (Exception e) {}
            u = a;
        } else if ("Lecturer".equalsIgnoreCase(role)) {
            Lecturer l = new Lecturer();
            try { l.setLecturerCode(rs.getString("lecturer_code")); } catch (Exception e) {}
            u = l;
        } else if ("Technical Officer".equalsIgnoreCase(role)) {
            TechnicalOfficer t = new TechnicalOfficer();
            try { t.setTechnicalOfficerCode(rs.getString("technical_officer_code")); } catch (Exception e) {}
            u = t;
        } else if ("Undergraduate".equalsIgnoreCase(role)) {
            Undergraduate ug = new Undergraduate();
            try {
                ug.setRegistrationNo(rs.getString("registration_no"));
                ug.setBatchYear(rs.getInt("batch_year"));
                ug.setStudentType(rs.getString("student_type"));
            } catch (Exception e) {}
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

    public boolean deleteUser(long userId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
