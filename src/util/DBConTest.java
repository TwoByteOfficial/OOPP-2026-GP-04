
package util;

import java.sql.*;

public class DBConTest {

    // ============================================================
    // DATABASE CONNECTION TESTER
    // ============================================================

    public static void main(String[] args) {

        System.out.println("==================================================");
        System.out.println("       UNIMIS DATABASE CONNECTION TEST");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // --------------------------------------------------------
        // 1. AUTHENTICATION ACCOUNT
        // --------------------------------------------------------

        if (testConnection("AUTHENTICATION", DBConnection.AUTH_CON)) {
            passed++;
        } else {
            failed++;
        }

        // --------------------------------------------------------
        // 2. ADMIN ACCOUNT
        // --------------------------------------------------------

        if (testConnection("ADMIN", DBConnection.ADMIN_CON)) {
            passed++;
        } else {
            failed++;
        }

        // --------------------------------------------------------
        // 3. LECTURER ACCOUNT
        // --------------------------------------------------------

        if (testConnection("LECTURER", DBConnection.LEC_CON)) {
            passed++;
        } else {
            failed++;
        }

        // --------------------------------------------------------
        // 4. TECHNICAL OFFICER ACCOUNT
        // --------------------------------------------------------

        if (testConnection("TECHNICAL OFFICER", DBConnection.TO_CON)) {
            passed++;
        } else {
            failed++;
        }

        // --------------------------------------------------------
        // 5. UNDERGRADUATE ACCOUNT
        // --------------------------------------------------------

        if (testConnection("UNDERGRADUATE", DBConnection.UG_CON)) {
            passed++;
        } else {
            failed++;
        }

        // --------------------------------------------------------
        // FINAL SUMMARY
        // --------------------------------------------------------

        System.out.println("\n==================================================");
        System.out.println("                 FINAL SUMMARY");
        System.out.println("==================================================");

        System.out.println("Total Accounts : 5");
        System.out.println("Passed         : " + passed);
        System.out.println("Failed         : " + failed);

        if (failed == 0) {
            System.out.println("\nRESULT: ALL DATABASE ACCOUNTS ARE WORKING!");
        } else {
            System.out.println("\nRESULT: SOME DATABASE ACCOUNTS FAILED!");
        }

        System.out.println("==================================================");
    }


    // ============================================================
    // TEST ONE DATABASE CONNECTION
    // ============================================================

    private static boolean testConnection(
            String role,
            Connection connection) {

        System.out.println("\n--------------------------------------------------");
        System.out.println("TESTING: " + role);
        System.out.println("--------------------------------------------------");

        try {

            // 1. Check connection
            if (connection == null || connection.isClosed()) {
                System.out.println("[FAILED] Connection is closed.");
                return false;
            }

            // 2. Check database connection
            if (!connection.isValid(5)) {
                System.out.println("[FAILED] Database is not responding.");
                return false;
            }

            // 3. Database information
            DatabaseMetaData metadata = connection.getMetaData();

            System.out.println("[SUCCESS] Database connection established.");
            System.out.println("Database   : " + connection.getCatalog());
            System.out.println("DB Product : " + metadata.getDatabaseProductName());
            System.out.println("DB Version : " + metadata.getDatabaseProductVersion());

            // 4. Identify authenticated MySQL account
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT CURRENT_USER(), USER()")) {

                if (rs.next()) {
                    System.out.println("MySQL Account : " + rs.getString(1));
                    System.out.println("Login Host    : " + rs.getString(2));
                }
            }

            // 5. Check basic database access
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT COUNT(*) FROM information_schema.tables " +
                                 "WHERE table_schema = DATABASE()")) {

                if (rs.next()) {
                    System.out.println("Database Tables : " + rs.getInt(1));
                }
            }

            // 6. Display granted privileges
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW GRANTS")) {

                System.out.println("\nGranted Privileges:");

                while (rs.next()) {
                    System.out.println("  " + rs.getString(1));
                }
            }

            System.out.println("\n[PASS] " + role + " account is working.");

            return true;

        } catch (SQLException e) {

            System.out.println("[FAILED] " + role + " account test failed.");
            System.out.println("Error: " + e.getMessage());

            return false;
        }
    }
}
