package services;

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
        String sql = "SELECT user_id, full_name, email, role, password_hash, user_status " +
                "FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return LoginResult.fail("Invalid username or password.");

                String blocked = blockedMessage(rs.getString("user_status"));
                if (blocked != null) return LoginResult.fail(blocked);

                if (!hashPassword(password).equalsIgnoreCase(rs.getString("password_hash"))) {
                    return LoginResult.fail("Invalid username or password.");
                }

                AuthUser user = new AuthUser(
                        rs.getLong("user_id"), username,
                        rs.getString("full_name"), rs.getString("email"), rs.getString("role"));

                return new LoginResult(user, null);
            }
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

}