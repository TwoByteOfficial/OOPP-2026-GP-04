package dao.admin;

import util.DBConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        try (Connection con = DBConnection.con();
             Statement stmt = con.createStatement();
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

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, role);

            try (ResultSet rs = pstmt.executeQuery()) {
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

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public boolean createAdmin(Admin admin) throws SQLException {
        admin.setRole("Admin");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO admins (user_id, admin_code, designation) VALUES (?, ?, ?)";

        Connection con = DBConnection.con();
        try {
            con.setAutoCommit(false);
            long userId;
            try (PreparedStatement pstmt = con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(pstmt, admin);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        admin.setUserId(userId);
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = con.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, admin.getAdminCode());
                childStmt.setString(3, admin.getDesignation());
                childStmt.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    public boolean createLecturer(Lecturer lecturer) throws SQLException {
        lecturer.setRole("Lecturer");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO lecturers (user_id, lecturer_code, title, specialization) VALUES (?, ?, ?, ?)";

        Connection con = DBConnection.con();
        try {
            con.setAutoCommit(false);
            long userId;
            try (PreparedStatement pstmt = con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(pstmt, lecturer);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        lecturer.setUserId(userId);
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = con.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, lecturer.getLecturerCode());
                childStmt.setString(3, lecturer.getTitle());
                childStmt.setString(4, lecturer.getSpecialization());
                childStmt.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    public boolean createTechnicalOfficer(TechnicalOfficer officer) throws SQLException {
        officer.setRole("Technical Officer");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO technical_officers (user_id, technical_officer_code, designation, assigned_labs) VALUES (?, ?, ?, ?)";

        Connection con = DBConnection.con();
        try {
            con.setAutoCommit(false);
            long userId;
            try (PreparedStatement pstmt = con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(pstmt, officer);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        officer.setUserId(userId);
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = con.prepareStatement(childSql)) {
                childStmt.setLong(1, userId);
                childStmt.setString(2, officer.getTechnicalOfficerCode());
                childStmt.setString(3, officer.getDesignation());
                childStmt.setString(4, officer.getAssignedLabs());
                childStmt.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    public boolean createUndergraduate(Undergraduate ug) throws SQLException {
        ug.setRole("Undergraduate");
        String userSql = "INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id, user_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String childSql = "INSERT INTO undergraduates (user_id, registration_no, index_no, batch_year, academic_year, student_type, intake, current_level, current_semester, admission_date, guardian_name, guardian_contact) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = DBConnection.con();
        try {
            con.setAutoCommit(false);
            long userId;
            try (PreparedStatement pstmt = con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                bindUserBaseParameters(pstmt, ug);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                        ug.setUserId(userId);
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement childStmt = con.prepareStatement(childSql)) {
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

            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, nic = ?, date_of_birth = ?, gender = ?, email = ?, contact_no = ?, address = ?, user_status = ?, department_id = ? WHERE user_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getNic());
            pstmt.setDate(3, user.getDateOfBirth());
            pstmt.setString(4, user.getGender());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getContactNo());
            pstmt.setString(7, user.getAddress());
            pstmt.setString(8, user.getUserStatus());
            if (user.getDepartmentId() != null) pstmt.setInt(9, user.getDepartmentId()); else pstmt.setNull(9, Types.INTEGER);
            pstmt.setLong(10, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateAdminProfileSettings(long userId, String fullName, String nic, String email, String contactNo, String address) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, nic = ?, email = ?, contact_no = ?, address = ? WHERE user_id = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, nic);
            pstmt.setString(3, email);
            pstmt.setString(4, contactNo);
            pstmt.setString(5, address);
            pstmt.setLong(6, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateUserPassword(String username, String currentPassword, String newPassword) throws SQLException {
        String verifySql = "SELECT user_id FROM users WHERE username = ? AND password_hash = ?";
        String updateSql = "UPDATE users SET password_hash = ? WHERE username = ?";

        try (Connection con = DBConnection.con();
             PreparedStatement verifyStmt = con.prepareStatement(verifySql)) {

            verifyStmt.setString(1, username);
            verifyStmt.setString(2, currentPassword);

            try (ResultSet rs = verifyStmt.executeQuery()) {
                if (!rs.next()) {
                    return false; // Current password incorrect or user not found
                }
            }

            try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, username);
                return updateStmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteUser(long userId) throws SQLException {
        String childAdminSql = "DELETE FROM admins WHERE user_id = ?";
        String childLecturerSql = "DELETE FROM lecturers WHERE user_id = ?";
        String childToSql = "DELETE FROM technical_officers WHERE user_id = ?";
        String childUgSql = "DELETE FROM undergraduates WHERE user_id = ?";
        String userSql = "DELETE FROM users WHERE user_id = ?";

        Connection con = DBConnection.con();
        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(childAdminSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(childLecturerSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(childToSql)) { ps.setLong(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(childUgSql)) { ps.setLong(1, userId); ps.executeUpdate(); }

            int rowsAffected;
            try (PreparedStatement ps = con.prepareStatement(userSql)) {
                ps.setLong(1, userId);
                rowsAffected = ps.executeUpdate();
            }

            con.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void bindUserBaseParameters(PreparedStatement pstmt, User u) throws SQLException {
        pstmt.setString(1, u.getUsername());
        pstmt.setString(2, u.getPasswordHash());
        pstmt.setString(3, u.getRole());
        pstmt.setString(4, u.getFullName());
        pstmt.setString(5, u.getNic());
        pstmt.setDate(6, u.getDateOfBirth());
        pstmt.setString(7, u.getGender());
        pstmt.setString(8, u.getEmail());
        pstmt.setString(9, u.getContactNo());
        pstmt.setString(10, u.getAddress());
        if (u.getDepartmentId() != null) pstmt.setInt(11, u.getDepartmentId()); else pstmt.setNull(11, Types.INTEGER);
        pstmt.setString(12, u.getUserStatus() != null ? u.getUserStatus() : "Active");
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