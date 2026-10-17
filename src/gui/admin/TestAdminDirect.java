package gui.admin;

import javax.swing.*;

/**
 * Developer utility class to bypass the login workflow
 * and instantly launch the Administrator Dashboard.
 */
public class TestAdminDirect {

    public static void main(String[] args) {
        // Apply system look and feel so the layout aesthetics render correctly
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run on the Event Dispatch Thread (EDT) for Swing thread-safety
        SwingUtilities.invokeLater(() -> {
            // Hardcoded mock user credentials to pass authorization checks
            long testUserId = 9999L;
            String testUsername = "dev_admin";
            String testFullName = "System Administrator (Dev Mode)";
            String testEmail = "admin.dev@unimis.edu";
            String testRole = "admin"; // Must match your 'isAdmin' verification string

            System.out.println("[DEV] Bypassing login window...");
            System.out.println("[DEV] Instantiating Administrator Frame directly...");

            // Initialize your actual project frame with test parameters
            Administrator adminFrame = new Administrator(
                    testUserId,
                    testUsername,
                    testFullName,
                    testEmail,
                    testRole
            );

            // Display the dashboard window
            adminFrame.setVisible(true);
        });
    }
}
