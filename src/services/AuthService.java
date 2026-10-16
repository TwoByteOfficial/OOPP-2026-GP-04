package service;

import util.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public final class AuthService {

    private AuthService() {}

    /** Row returned on a successful login. */
    public record AuthUser(long id, String username, String fullName, String email, String role) {}

    /** Result of {@link #authenticate}: either a user OR an error message. */
    public record LoginResult(AuthUser user, String error) {
        public boolean ok() { return user != null; }
        static LoginResult fail(String msg) { return new LoginResult(null, msg); }
    }

    /* ---------------- Login ---------------- */

    public static LoginResult authenticate(String username, String password) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            // Some group members may have imported an earlier schema where role is
            // derived from the subclass tables rather than stored in users.role.
            boolean hasRoleColumn = hasColumn(con, "users", "role");
            String roleExpression = hasRoleColumn
                    ? "u.role"
                    : "CASE " +
                      "WHEN EXISTS (SELECT 1 FROM admins a WHERE a.user_id=u.user_id) THEN 'Admin' " +
                      "WHEN EXISTS (SELECT 1 FROM lecturers l WHERE l.user_id=u.user_id) THEN 'Lecturer' " +
                      "WHEN EXISTS (SELECT 1 FROM technical_officers t WHERE t.user_id=u.user_id) THEN 'Technical Officer' " +
                      "WHEN EXISTS (SELECT 1 FROM undergraduates ug WHERE ug.user_id=u.user_id) THEN 'Undergraduate' " +
                      "ELSE 'Unknown' END";
            String sql = "SELECT u.user_id, u.username, u.full_name, u.email, " + roleExpression +
                    " AS resolved_role, u.password_hash, u.user_status FROM users u WHERE u.username = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return LoginResult.fail("Invalid username or password.");

                    String blocked = blockedMessage(rs.getString("user_status"));
                    if (blocked != null) return LoginResult.fail(blocked);

                    String storedPassword = rs.getString("password_hash");
                    boolean validPassword;
                    if (isSha256Hex(storedPassword)) {
                        validPassword = hashPassword(password).equalsIgnoreCase(storedPassword);
                    } else {
                        // Demo data in the supplied SQL initially uses plain-text passwords.
                        // Accept it once, then upgrade that row to SHA-256 after successful login.
                        validPassword = storedPassword != null && storedPassword.equals(password);
                        if (validPassword) upgradePasswordHash(con, rs.getLong("user_id"), password);
                    }
                    if (!validPassword) return LoginResult.fail("Invalid username or password.");

                    String role = rs.getString("resolved_role");
                    if (role == null || role.equalsIgnoreCase("Unknown")) {
                        return LoginResult.fail("This account has no assigned system role. Please contact the administrator.");
                    }
                    AuthUser user = new AuthUser(rs.getLong("user_id"), rs.getString("username"),
                            rs.getString("full_name"), rs.getString("email"), role);
                    touchLastLogin(con, user.id());
                    return new LoginResult(user, null);
                }
            }
        }
    }

    private static boolean hasColumn(Connection con, String table, String column) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getColumns(con.getCatalog(), null, table, column)) {
            if (rs.next()) return true;
        }
        // Metadata matching can be case-sensitive on some MySQL/OS combinations.
        try (ResultSet rs = meta.getColumns(con.getCatalog(), null, table, "%")) {
            while (rs.next()) if (column.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) return true;
        }
        return false;
    }

    private static boolean isSha256Hex(String value) {
        if (value == null || value.length() != 64) return false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) return false;
        }
        return true;
    }

    private static void upgradePasswordHash(Connection con, long userId, String plainPassword) {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET password_hash = ? WHERE user_id = ?")) {
            ps.setString(1, hashPassword(plainPassword));
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // Login can still proceed if this older database user lacks update permission.
            System.err.println("Could not upgrade demo password to SHA-256: " + ex.getMessage());
        }
    }

    /* ---------------- Forgot password ---------------- */

    public static boolean resetPassword(String username, String email, String nic, String newPassword)
            throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE username = ? AND email = ? AND nic = ? AND user_status <> 'Suspended'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashPassword(newPassword));
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, nic);
            return ps.executeUpdate() > 0;
        }
    }

    /* ---------------- Helpers ---------------- */

    public static String hashPassword(String plain) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String blockedMessage(String status) {
        if ("Suspended".equalsIgnoreCase(status))
            return "This account has been suspended.\nPlease contact the Faculty IT Division.";
        if ("Inactive".equalsIgnoreCase(status))
            return "This account is inactive.\nPlease contact the Faculty IT Division to activate it.";
        return null;
    }

    private static void touchLastLogin(Connection con, long userId) {
        try {
            if (!hasColumn(con, "users", "last_login")) return;
        } catch (SQLException ex) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Could not update last_login: " + ex.getMessage());
        }
    }
}