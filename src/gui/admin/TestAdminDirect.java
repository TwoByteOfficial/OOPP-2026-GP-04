package gui.admin;

import javax.swing.*;

/**
 * Developer utility class to bypass the login workflow
 * and instantly launch the Administrator Dashboard.
 */
public class TestAdminDirect {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            long testUserId = 1;
            String testUsername = "dev_admin";
            String testFullName = "System Dev Administrator";
            String testEmail = "admin.dev@unimis.edu";
            String testRole = "Admin";

            System.out.println("[DEV] Bypassing login window...");
            System.out.println("[DEV] Instantiating Administrator Frame directly with live DB dashboard...");

            Administrator adminFrame = new Administrator(
                    testUserId,
                    testUsername,
                    testFullName,
                    testEmail,
                    testRole
            );

            adminFrame.setVisible(true);
        });
    }
}
