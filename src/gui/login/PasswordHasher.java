
package gui.login;

import util.DBConnection;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PasswordHasher {

    // PBKDF2 configuration
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    /*
     * Generate a secure password hash.
     *
     * Stored format:
     * PBKDF2$iterations$salt$hash
     */
    public static String hashPassword(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hash = generateHash(password.toCharArray(), salt, ITERATIONS);

        return "PBKDF2$"
                + ITERATIONS + "$"
                + toHex(salt) + "$"
                + toHex(hash);
    }

    /*
     * Verify an entered password against its stored hash.
     */
    public static boolean verifyPassword(String password, String storedHash) {

        if (password == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }

        try {

            // New PBKDF2 format
            if (storedHash.startsWith("PBKDF2$")) {

                String[] parts = storedHash.split("\\$");

                if (parts.length != 4) {
                    return false;
                }

                int iterations = Integer.parseInt(parts[1]);
                byte[] salt = fromHex(parts[2]);
                byte[] expectedHash = fromHex(parts[3]);

                if (iterations < 1 || salt.length == 0 || expectedHash.length == 0) {
                    return false;
                }

                byte[] actualHash = generateHash(
                        password.toCharArray(),
                        salt,
                        iterations
                );

                return MessageDigest.isEqual(expectedHash, actualHash);
            }

            // Legacy SHA-256 support
            if (isSHA256Hash(storedHash)) {
                String enteredHash = hashSHA256(password);
                return MessageDigest.isEqual(
                        enteredHash.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        storedHash.toLowerCase().getBytes(
                                java.nio.charset.StandardCharsets.UTF_8
                        )
                );
            }

            // Legacy plaintext support for migration only.
            return MessageDigest.isEqual(
                    password.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    storedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            System.err.println("Password verification failed: " + e.getMessage());
            return false;
        }
    }

    /*
     * Check whether a stored password needs upgrading.
     */
    public static boolean needsRehash(String storedHash) {

        return storedHash == null
                || !storedHash.startsWith("PBKDF2$");
    }

    /*
     * Upgrade a legacy password after successful login.
     */
    public static boolean upgradeLegacyPassword(long userId, String plainPassword) {

        String newHash = hashPassword(plainPassword);

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        try {

            Connection conn = DBConnection.AUTH_CON;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, newHash);
                ps.setLong(2, userId);

                return ps.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            System.err.println("Password upgrade failed: " + e.getMessage());
            return false;
        }
    }

    /*
     * Generate PBKDF2 hash.
     */
    private static byte[] generateHash(
            char[] password,
            byte[] salt,
            int iterations
    ) {

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                KEY_LENGTH
        );

        try {

            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception e) {

            throw new IllegalStateException("Unable to generate password hash.", e);

        } finally {
            spec.clearPassword();
        }
    }

    /*
     * Legacy SHA-256 method.
     */
    private static String hashSHA256(String password) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(
                password.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        return toHex(hash);
    }

    /*
     * Identify legacy SHA-256 hashes.
     */
    private static boolean isSHA256Hash(String value) {

        return value != null && value.matches("(?i)[0-9a-f]{64}");
    }

    /*
     * Convert byte array to hexadecimal.
     */
    private static String toHex(byte[] bytes) {

        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b & 0xff));
        }

        return result.toString();
    }

    /*
     * Convert hexadecimal to byte array.
     */
    private static byte[] fromHex(String hex) {

        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hexadecimal value.");
        }

        byte[] result = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i += 2) {

            result[i / 2] = (byte) Integer.parseInt(
                    hex.substring(i, i + 2),
                    16
            );
        }

        return result;
    }

    /*
     * One-time migration:
     * Convert existing plaintext passwords to PBKDF2.
     *
     * Existing SHA-256 hashes are preserved.
     */
    public static void migratePasswords() {

        Connection conn = DBConnection.AUTH_CON;

        String selectSQL = """
                SELECT user_id, username, password_hash
                FROM users
                """;

        String updateSQL = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        List<PasswordRecord> records = new ArrayList<>();

        try {

            // Read records before updating.
            try (PreparedStatement ps = conn.prepareStatement(selectSQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    records.add(new PasswordRecord(
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash")
                    ));
                }
            }

            int migrated = 0;
            int skipped = 0;

            for (PasswordRecord record : records) {

                String oldPassword = record.passwordHash;

                if (oldPassword == null || oldPassword.isEmpty()) {
                    skipped++;
                    continue;
                }

                // Already using PBKDF2
                if (oldPassword.startsWith("PBKDF2$")) {
                    skipped++;
                    continue;
                }

                // Preserve existing SHA-256 hashes.
                if (isSHA256Hash(oldPassword)) {
                    skipped++;
                    continue;
                }

                String newHash = hashPassword(oldPassword);

                try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {

                    ps.setString(1, newHash);
                    ps.setLong(2, record.userId);

                    if (ps.executeUpdate() == 1) {
                        migrated++;
                        System.out.println("Migrated: " + record.username);
                    }
                }
            }

            System.out.println("\n========== MIGRATION SUMMARY ==========");
            System.out.println("Migrated passwords : " + migrated);
            System.out.println("Skipped passwords  : " + skipped);
            System.out.println("========================================");

        } catch (SQLException e) {

            System.err.println("Password migration failed: " + e.getMessage());
        }
    }

    /*
     * Password record.
     */
    private static class PasswordRecord {

        private final long userId;
        private final String username;
        private final String passwordHash;

        private PasswordRecord(long userId, String username, String passwordHash) {
            this.userId = userId;
            this.username = username;
            this.passwordHash = passwordHash;
        }
    }

    /*
     * Test / migration entry point.
     */
    public static void main(String[] args) {

        migratePasswords();
    }
}
