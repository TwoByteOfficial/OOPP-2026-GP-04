package gui.undergraduate;

import javax.swing.*;

/**
 * Developer utility to bypass login and instantly launch the Undergraduate Dashboard.
 * Allows selecting from different test undergraduates seeded in the database
 * to verify view coordinators, dashboard cards, and component data-bindings directly.
 */
public class TestUndergraduateDirect {

    // Test undergraduate account records present in the test database and SRS specification
    private static final Object[][] TEST_STUDENTS = {
            {24L, "tg2074", "Piumi Senanayake", "tg2074@fot.ruh.ac.lk"},
            {25L, "tg2076", "Thilini Kumari",   "tg2076@fot.ruh.ac.lk"},
            {26L, "tg2077", "Janith Rajapaksha", "tg2077@fot.ruh.ac.lk"},
            {27L, "tg2078", "Oshadi Wijesinghe", "tg2078@fot.ruh.ac.lk"},
            {28L, "tg2080", "Menaka Liyanage",   "tg2080@fot.ruh.ac.lk"}
    };

    /**
     * Application test entry point.
     * Sets native operating system look-and-feel and launches the dashboard for the selected student profile.
     *
     * @param args Command-line execution arguments
     */
    public static void main(String[] args) {
        // Set native OS look-and-feel for consistent desktop rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Execute GUI initialization safely on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Index selector for test student account (default: 0 - Piumi Senanayake)
            int selectedIndex = 0;

            Object[] student = TEST_STUDENTS[selectedIndex];
            long testUserId = (Long) student[0];
            String testUsername = (String) student[1];
            String testFullName = (String) student[2];
            String testEmail = (String) student[3];
            String testRole = "Undergraduate";

            System.out.println("[DEV] Launching Undergraduate Dashboard for: " + testFullName + " (" + testUsername + ")");

            // Instantiate and display main student portal view
            UndergraduateDashboard ug = new UndergraduateDashboard(
                    testUserId,
                    testUsername,
                    testFullName,
                    testEmail,
                    testRole
            );
            ug.setVisible(true);
        });
    }
}