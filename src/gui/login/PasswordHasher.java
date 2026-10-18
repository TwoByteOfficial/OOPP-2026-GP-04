package gui.login;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBConnection;

/**
 * One-off utility to convert the plaintext passwords in the users table
 * into SHA-256 hex hashes, matching the scheme used by doLogin().
 *
 * Run main() once. It is safe to re-run: rows that already look like a
 * 64-char hex SHA-256 hash are skipped.
 */
public class PasswordHasher {

    private PasswordHasher() { /* utility class */ }

    /* ---------------------------------------------------------------
       Hash function — MUST stay identical to the one used in doLogin()
       --------------------------------------------------------------- */
    public static String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));   // lowercase hex
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JVM spec; should never happen.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /* ---------------------------------------------------------------
       Verify (kept here so both hashing and checking live together)
       --------------------------------------------------------------- */
    public static boolean verifyPassword(String plain, String storedHash) {
        if (plain == null || storedHash == null) return false;
        return hashPassword(plain).equalsIgnoreCase(storedHash);
    }

    /* ---------------------------------------------------------------
       Main — read every user, hash the plaintext, update the row
       --------------------------------------------------------------- */
    public static void main(String[] args) {

        final String SELECT_SQL =
                "SELECT username, password_hash FROM users";
        final String UPDATE_SQL =
                "UPDATE users SET password_hash = ? WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement selectPs = con.prepareStatement(SELECT_SQL);
             PreparedStatement updatePs = con.prepareStatement(UPDATE_SQL);
             ResultSet rs = selectPs.executeQuery()) {

            int updated = 0;

            while (rs.next()) {
                String username = rs.getString("username");
                String current  = rs.getString("password_hash");

                // Skip rows that are already hashed
                if (current == null || isAlreadyHashed(current)) {
                    continue;
                }

                String hashed = hashPassword(current);

                updatePs.setString(1, hashed);
                updatePs.setString(2, username);
                updatePs.addBatch();

                System.out.printf("  %-12s -> %s%n", username, hashed);
                updated++;
            }

            if (updated > 0) {
                int[] rows = updatePs.executeBatch();
                System.out.println("\nDone. Updated " + rows.length + " user(s).");
            } else {
                System.out.println("Nothing to do — all rows are already hashed.");
            }

        } catch (SQLException ex) {
            System.err.println("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** A SHA-256 hex digest is exactly 64 hex characters. */
    private static boolean isAlreadyHashed(String value) {
        if (value.length() != 64) return false;
        for (int i = 0; i < 64; i++) {
            char c = value.charAt(i);
            boolean hex = (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F');
            if (!hex) return false;
        }
        return true;
    }
}
