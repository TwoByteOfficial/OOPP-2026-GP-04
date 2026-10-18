package dao.admin;

import util.DBConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsible for Admin User Management (User Profiles CRUD across 4 roles)
 * and Admin Settings profile updates.
 */
public class AdminUserDAO {

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, " +
                     "a.admin_code, a.designation AS admin_designation, " +
                     "l.lecturer_code, l.title, l.specialization, " +
                     "t.technical_officer_code, t.designation AS to_designation, t.assigned_labs, " +
                     "ug.registration_no, ug.index_no, ug.batch_year, ug.academic_year AS ug_academic_year, ug.student_type " +
                     "FROM users u " +
                     "LEFT JOIN admins a ON u.user_id = a.user_id " +
                     "LEFT JOIN lecturers l ON u.user_id = l.user_id " +
                     "LEFT JOIN technical_officers t ON u.user_id = t.user_id " +
                     "LEFT JOIN undergraduates ug ON u.user_id = ug.user_id " +
                     "ORDER BY u.user_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        }
        return list;
    }

    public List<User> getUsersByRole(String role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, " +
                     "a.admin_code, a.designation AS admin_designation, " +
                     "l.lecturer_code, l.title, l.specialization, " +
                     "t.technical_officer_code, t.designation AS to_designation, t.assigned_labs, " +
                     "ug.registration_no, ug.index_no, ug.batch_year, ug.academic_year AS ug_academic_year, ug.student_type " +
                     "FROM users u " +
                     "LEFT JOIN admins a ON u.user_id = a.user_id " +
                     "LEFT JOIN lecturers l ON u.user_id = l.user_id " +
                     "LEFT JOIN technical_officers t ON u.user_id = t.user_id " +
                     "LEFT JOIN undergraduates ug ON u.user_id = ug.user_id " +
                     "WHERE u.role = ? " +
                     "ORDER BY u.user_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        }
        return list;
    }

    public User getUserById(long userId) throws SQLException {
        String sql = "SELECT u.*, " +
                     "a.admin_code, a.designation AS admin_designation, " +
                     "l.lecturer_code, l.title, l.specialization, " +
                     "t.technical_officer_code, t.designation AS to_designation, t.assigned_labs, " +
                     "ug.registration_no, ug.index_no, ug.batch_year, ug.academic_year AS ug_academic_year, ug.student_type, " +
                     "ug.intake, ug.current_level, ug.current_semester, ug.admission_date, ug.guardian_name, ug.guardian_contact " +
                     "FROM users u " +
                     "LEFT JOIN admins a ON u.user_id = a.user_id " +
                     "LEFT JOIN lecturers l ON u.user_id = l.user_id " +
                     "LEFT JOIN technical_officers t ON u.user_id = t.user_id " +
                     "LEFT JOIN undergraduates ug ON u.user_id = ug.user_id " +
                     "WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Creates an Admin account inside a transaction (inserts into `users` and `admins`).
     */
    public boolean createAdmin(Admin admin) throws SQLException {
        admin.setRole("Admin");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO admins (user_id, admin_code, designation) VALUES (?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            long userId;
            try (PreparedStatement stmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(stmt, admin);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        admin.setUserId(userId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = conn.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, admin.getAdminCode());
                childStmt.setString(3, admin.getDesignation());
                childStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Creates a Lecturer account inside a transaction (inserts into `users` and `lecturers`).
     */
    public boolean createLecturer(Lecturer lecturer) throws SQLException {
        lecturer.setRole("Lecturer");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO lecturers (user_id, lecturer_code, title, specialization) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            long userId;
            try (PreparedStatement stmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(stmt, lecturer);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        lecturer.setUserId(userId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = conn.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, lecturer.getLecturerCode());
                childStmt.setString(3, lecturer.getTitle());
                childStmt.setString(4, lecturer.getSpecialization());
                childStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Creates a Technical Officer account inside a transaction (inserts into `users` and `technical_officers`).
     */
    public boolean createTechnicalOfficer(TechnicalOfficer officer) throws SQLException {
        officer.setRole("Technical Officer");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO technical_officers (user_id, technical_officer_code, designation, assigned_labs) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            long userId;
            try (PreparedStatement stmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(stmt, officer);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        officer.setUserId(userId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = conn.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, officer.getTechnicalOfficerCode());
                childStmt.setString(3, officer.getDesignation());
                childStmt.setString(4, officer.getAssignedLabs());
                childStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Creates an Undergraduate account inside a transaction (inserts into `users` and `undergraduates`).
     */
    public boolean createUndergraduate(Undergraduate ug) throws SQLException {
        ug.setRole("Undergraduate");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO undergraduates (user_id, registration_no, index_no, batch_year, academic_year, student_type, intake, current_level, current_semester, admission_date, guardian_name, guardian_contact) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            long userId;
            try (PreparedStatement stmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(stmt, ug);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        ug.setUserId(userId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = conn.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, ug.getRegistrationNo());
                childStmt.setString(3, ug.getIndexNo());
                childStmt.setInt(4, ug.getBatchYear());
                childStmt.setString(5, ug.getAcademicYear());
                childStmt.setString(6, ug.getStudentType());
                childStmt.setString(7, ug.getIntake());
                childStmt.setInt(8, ug.getCurrentLevel());
                childStmt.setInt(9, ug.getCurrentSemester());
                childStmt.setDate(10, ug.getAdmissionDate());
                childStmt.setString(11, ug.getGuardianName());
                childStmt.setString(12, ug.getGuardianContact());
                childStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Updates common user details in the `users` table.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, nic = ?, date_of_birth = ?, gender = ?, email = ?, contact_no = ?, address = ?, user_status = ?, department_id = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getNic());
            stmt.setDate(3, user.getDateOfBirth());
            stmt.setString(4, user.getGender());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getContactNo());
            stmt.setString(7, user.getAddress());
            stmt.setString(8, user.getUserStatus());
            if (user.getDepartmentId() != null) stmt.setInt(9, user.getDepartmentId()); else stmt.setNull(9, Types.INTEGER);
            stmt.setLong(10, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates Admin Settings profile details (full_name, nic, email, contact_no, address).
     * Note: username and password are protected/locked on this view.
     */
    public boolean updateAdminProfileSettings(long userId, String fullName, String nic, String email, String contactNo, String address) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, nic = ?, email = ?, contact_no = ?, address = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, nic);
            stmt.setString(3, email);
            stmt.setString(4, contactNo);
            stmt.setString(5, address);
            stmt.setLong(6, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user account cleanly across parent and child tables.
     */
    public boolean deleteUser(long userId) throws SQLException {
        String childAdminSql = "DELETE FROM admins WHERE user_id = ?";
        String childLecturerSql = "DELETE FROM lecturers WHERE user_id = ?";
        String childToSql = "DELETE FROM technical_officers WHERE user_id = ?";
        String childUgSql = "DELETE FROM undergraduates WHERE user_id = ?";
        String userSql = "DELETE FROM users WHERE user_id = ?";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(childAdminSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(childLecturerSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(childToSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(childUgSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            
            int affected;
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setLong(1, userId);
                affected = ps.executeUpdate();
            }

            conn.commit();
            return affected > 0;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    private void bindUserBaseParameters(PreparedStatement stmt, User u) throws SQLException {
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
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User u;

        if ("Admin".equalsIgnoreCase(role)) {
            Admin a = new Admin();
            try { a.setAdminCode(rs.getString("admin_code")); } catch (Exception ignored) {}
            try { a.setDesignation(rs.getString("admin_designation")); } catch (Exception ignored) {}
            u = a;
        } else if ("Lecturer".equalsIgnoreCase(role)) {
            Lecturer l = new Lecturer();
            try { l.setLecturerCode(rs.getString("lecturer_code")); } catch (Exception ignored) {}
            try { l.setTitle(rs.getString("title")); } catch (Exception ignored) {}
            try { l.setSpecialization(rs.getString("specialization")); } catch (Exception ignored) {}
            u = l;
        } else if ("Technical Officer".equalsIgnoreCase(role)) {
            TechnicalOfficer t = new TechnicalOfficer();
            try { t.setTechnicalOfficerCode(rs.getString("technical_officer_code")); } catch (Exception ignored) {}
            try { t.setDesignation(rs.getString("to_designation")); } catch (Exception ignored) {}
            try { t.setAssignedLabs(rs.getString("assigned_labs")); } catch (Exception ignored) {}
            u = t;
        } else if ("Undergraduate".equalsIgnoreCase(role)) {
            Undergraduate ug = new Undergraduate();
            try {
                ug.setRegistrationNo(rs.getString("registration_no"));
                ug.setIndexNo(rs.getString("index_no"));
                ug.setBatchYear(rs.getInt("batch_year"));
                ug.setAcademicYear(rs.getString("ug_academic_year"));
                ug.setStudentType(rs.getString("student_type"));
                ug.setIntake(rs.getString("intake"));
                ug.setCurrentLevel(rs.getInt("current_level"));
                ug.setCurrentSemester(rs.getInt("current_semester"));
                ug.setAdmissionDate(rs.getDate("admission_date"));
                ug.setGuardianName(rs.getString("guardian_name"));
                ug.setGuardianContact(rs.getString("guardian_contact"));
            } catch (Exception ignored) {}
            u = ug;
        } else {
            u = new User();
        }

        u.setUserId(rs.getLong("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
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
        u.setRole(role);
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));

        return u;
    }
}
