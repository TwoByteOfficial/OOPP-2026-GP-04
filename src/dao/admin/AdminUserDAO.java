package dao.admin;

import util.DBConnection;
import model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUserDAO {

    // =========================================================
    // PASSWORD HASHING — SHA-256 hex, matches PasswordHasher
    // (whatever PasswordHasher your build is actually running)
    // =========================================================

    private static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash password.", e);
        }
    }

    private static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null || storedHash.isEmpty()) return false;
        String entered = hashPassword(password);
        return MessageDigest.isEqual(
                entered.getBytes(StandardCharsets.UTF_8),
                storedHash.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }

    // =========================================================
    // SQL
    // =========================================================

    private static final String USER_SELECT =
            "SELECT u.*, " +
                    "a.admin_code, a.designation AS admin_designation, " +
                    "l.lecturer_code, l.title, l.specialization, " +
                    "t.technical_officer_code, t.designation AS to_designation, t.assigned_labs, " +
                    "ug.registration_no, ug.index_no, ug.batch_year, " +
                    "ug.academic_year AS ug_academic_year, ug.student_type, " +
                    "ug.intake, ug.current_level, ug.current_semester, " +
                    "ug.admission_date, ug.guardian_name, ug.guardian_contact " +
                    "FROM users u " +
                    "LEFT JOIN admins a ON u.user_id = a.user_id " +
                    "LEFT JOIN lecturers l ON u.user_id = l.user_id " +
                    "LEFT JOIN technical_officers t ON u.user_id = t.user_id " +
                    "LEFT JOIN undergraduates ug ON u.user_id = ug.user_id ";

    private static final String INSERT_USER =
            "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, " +
                    "gender, email, contact_no, address, department_id, user_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // =========================================================
    // FUNCTIONAL TYPES
    // =========================================================

    @FunctionalInterface private interface TxWork { boolean run() throws SQLException; }
    @FunctionalInterface private interface ChildBinder {
        void bind(PreparedStatement ps, long userId) throws SQLException;
    }

    private boolean inTransaction(TxWork work) throws SQLException {
        Connection c = DBConnection.ADMIN_CON;
        c.setAutoCommit(false);
        try {
            if (!work.run()) { c.rollback(); return false; }
            c.commit();
            return true;
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    // =========================================================
    // READS
    // =========================================================

    public List<User> getAllUsers() throws SQLException {
        try (Statement st = DBConnection.ADMIN_CON.createStatement();
             ResultSet rs = st.executeQuery(USER_SELECT + "ORDER BY u.user_id DESC")) {
            return collect(rs);
        }
    }

    public List<User> getUsersByRole(String role) throws SQLException {
        try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                USER_SELECT + "WHERE u.role = ? ORDER BY u.user_id DESC")) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) { return collect(rs); }
        }
    }

    public User getUserById(long userId) throws SQLException {
        try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                USER_SELECT + "WHERE u.user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUser(rs) : null;
            }
        }
    }

    private List<User> collect(ResultSet rs) throws SQLException {
        List<User> list = new ArrayList<>();
        while (rs.next()) list.add(mapResultSetToUser(rs));
        return list;
    }

    // =========================================================
    // CREATES
    // =========================================================

    private boolean createUserWithChild(
            User u, String role, String childSql, ChildBinder binder) throws SQLException {

        u.setRole(role);
        return inTransaction(() -> {
            long userId;
            try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                    INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(ps, u);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) return false;
                    userId = keys.getLong(1);
                }
            }
            u.setUserId(userId);
            try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(childSql)) {
                binder.bind(ps, userId);
                ps.executeUpdate();
            }
            return true;
        });
    }

    public boolean createAdmin(Admin a) throws SQLException {
        return createUserWithChild(a, "Admin",
                "INSERT INTO admins (user_id, admin_code, designation) VALUES (?, ?, ?)",
                (ps, id) -> {
                    ps.setLong(1, id);
                    ps.setString(2, a.getAdminCode());
                    ps.setString(3, a.getDesignation());
                });
    }

    public boolean createLecturer(Lecturer l) throws SQLException {
        return createUserWithChild(l, "Lecturer",
                "INSERT INTO lecturers (user_id, lecturer_code, title, specialization) VALUES (?, ?, ?, ?)",
                (ps, id) -> {
                    ps.setLong(1, id);
                    ps.setString(2, l.getLecturerCode());
                    ps.setString(3, l.getTitle());
                    ps.setString(4, l.getSpecialization());
                });
    }

    public boolean createTechnicalOfficer(TechnicalOfficer t) throws SQLException {
        return createUserWithChild(t, "Technical Officer",
                "INSERT INTO technical_officers (user_id, technical_officer_code, designation, assigned_labs) VALUES (?, ?, ?, ?)",
                (ps, id) -> {
                    ps.setLong(1, id);
                    ps.setString(2, t.getTechnicalOfficerCode());
                    ps.setString(3, t.getDesignation());
                    ps.setString(4, t.getAssignedLabs());
                });
    }

    public boolean createUndergraduate(Undergraduate ug) throws SQLException {
        return createUserWithChild(ug, "Undergraduate",
                "INSERT INTO undergraduates (user_id, registration_no, index_no, batch_year, academic_year, " +
                        "student_type, intake, current_level, current_semester, admission_date, guardian_name, guardian_contact) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                (ps, id) -> {
                    ps.setLong(1, id);
                    ps.setString(2, ug.getRegistrationNo());
                    ps.setString(3, ug.getIndexNo());
                    ps.setInt(4, ug.getBatchYear());
                    ps.setString(5, ug.getAcademicYear());
                    ps.setString(6, ug.getStudentType());
                    ps.setString(7, ug.getIntake());
                    ps.setInt(8, ug.getCurrentLevel());
                    ps.setInt(9, ug.getCurrentSemester());
                    ps.setDate(10, ug.getAdmissionDate());
                    ps.setString(11, ug.getGuardianName());
                    ps.setString(12, ug.getGuardianContact());
                });
    }

    // =========================================================
    // UPDATES
    // =========================================================

    public boolean updateUser(User u) throws SQLException {
        return inTransaction(() -> {
            try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                    "UPDATE users SET full_name=?, nic=?, date_of_birth=?, gender=?, " +
                            "email=?, contact_no=?, address=?, user_status=?, department_id=? " +
                            "WHERE user_id=?")) {
                ps.setString(1, u.getFullName());
                ps.setString(2, u.getNic());
                ps.setDate(3, u.getDateOfBirth());
                ps.setString(4, u.getGender());
                ps.setString(5, u.getEmail());
                ps.setString(6, u.getContactNo());
                ps.setString(7, u.getAddress());
                ps.setString(8, u.getUserStatus());
                if (u.getDepartmentId() != null) ps.setInt(9, u.getDepartmentId());
                else                              ps.setNull(9, Types.INTEGER);
                ps.setLong(10, u.getUserId());
                if (ps.executeUpdate() == 0) return false;
            }

            if (u instanceof Undergraduate ug) {
                try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                        "UPDATE undergraduates SET student_type=? WHERE user_id=?")) {
                    ps.setString(1, ug.getStudentType());
                    ps.setLong(2, u.getUserId());
                    if (ps.executeUpdate() == 0) return false;
                }
            }
            return true;
        });
    }

    public boolean updateAdminProfileSettings(
            long userId, String fullName, String nic,
            String email, String contactNo, String address) throws SQLException {

        try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                "UPDATE users SET full_name=?, nic=?, email=?, contact_no=?, address=? WHERE user_id=?")) {
            ps.setString(1, fullName);
            ps.setString(2, nic);
            ps.setString(3, email);
            ps.setString(4, contactNo);
            ps.setString(5, address);
            ps.setLong(6, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUserPassword(
            String username, String currentPassword, String newPassword) throws SQLException {

        if (username == null || username.isBlank()
                || currentPassword == null
                || newPassword == null || newPassword.isBlank()) return false;

        long userId;
        String storedHash;

        try (PreparedStatement ps = DBConnection.AUTH_CON.prepareStatement(
                "SELECT user_id, password_hash FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                userId = rs.getLong("user_id");
                storedHash = rs.getString("password_hash");
            }
        }

        if (!verifyPassword(currentPassword, storedHash)) return false;

        try (PreparedStatement ps = DBConnection.AUTH_CON.prepareStatement(
                "UPDATE users SET password_hash = ? WHERE user_id = ?")) {
            ps.setString(1, hashPassword(newPassword));
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    private static final String[] CHILD_TABLES = {
            "admins", "lecturers", "technical_officers", "undergraduates"
    };

    public boolean deleteUser(long userId) throws SQLException {
        return inTransaction(() -> {
            for (String table : CHILD_TABLES) {
                try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                        "DELETE FROM " + table + " WHERE user_id = ?")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?")) {
                ps.setLong(1, userId);
                return ps.executeUpdate() > 0;
            }
        });
    }

    // =========================================================
    // BASE PARAM BINDING
    // =========================================================

    private void bindUserBaseParameters(PreparedStatement ps, User u) throws SQLException {
        String plain = u.getPasswordHash();
        if (plain == null || plain.isBlank())
            throw new SQLException("Password cannot be empty.");

        String hashed = hashPassword(plain);
        u.setPasswordHash(hashed);

        ps.setString(1,  u.getUsername());
        ps.setString(2,  hashed);
        ps.setString(3,  u.getRole());
        ps.setString(4,  u.getFullName());
        ps.setString(5,  u.getNic());
        ps.setDate  (6,  u.getDateOfBirth());
        ps.setString(7,  u.getGender());
        ps.setString(8,  u.getEmail());
        ps.setString(9,  u.getContactNo());
        ps.setString(10, u.getAddress());
        if (u.getDepartmentId() != null) ps.setInt(11, u.getDepartmentId());
        else                              ps.setNull(11, Types.INTEGER);
        ps.setString(12, u.getUserStatus() != null ? u.getUserStatus() : "Active");
    }

    // =========================================================
    // RESULT SET -> MODEL
    // =========================================================

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");

        User u = switch (role.toLowerCase()) {
            case "admin" -> {
                Admin a = new Admin();
                a.setAdminCode(rs.getString("admin_code"));
                a.setDesignation(rs.getString("admin_designation"));
                yield a;
            }
            case "lecturer" -> {
                Lecturer l = new Lecturer();
                l.setLecturerCode(rs.getString("lecturer_code"));
                l.setTitle(rs.getString("title"));
                l.setSpecialization(rs.getString("specialization"));
                yield l;
            }
            case "technical officer" -> {
                TechnicalOfficer t = new TechnicalOfficer();
                t.setTechnicalOfficerCode(rs.getString("technical_officer_code"));
                t.setDesignation(rs.getString("to_designation"));
                t.setAssignedLabs(rs.getString("assigned_labs"));
                yield t;
            }
            case "undergraduate" -> {
                Undergraduate ug = new Undergraduate();
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
                yield ug;
            }
            default -> new User();
        };

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