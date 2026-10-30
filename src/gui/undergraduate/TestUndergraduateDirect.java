package gui.undergraduate;

import javax.swing.*;

/**
 * Developer utility to bypass login and instantly launch the Undergraduate Dashboard.
 * Allows selecting from different test undergraduates.
 */
public class TestUndergraduateDirect {

    // SRS එකේ සහ Database එකේ ඉන්න test undergraduates ලා
    private static final Object[][] TEST_STUDENTS = {
            {24L, "tg2074", "Piumi Senanayake", "tg2074@fot.ruh.ac.lk"},
            {25L, "tg2076", "Thilini Kumari",   "tg2076@fot.ruh.ac.lk"},
            {26L, "tg2077", "Janith Rajapaksha", "tg2077@fot.ruh.ac.lk"},
            {27L, "tg2078", "Oshadi Wijesinghe", "tg2078@fot.ruh.ac.lk"},
            {28L, "tg2080", "Menaka Liyanage",   "tg2080@fot.ruh.ac.lk"}
    };

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Test කරන්න ඕන student කෙනෙක්ව තෝරාගැනීම (default: 0 - Piumi)
            int selectedIndex = 0;

            Object[] student = TEST_STUDENTS[selectedIndex];
            long testUserId = (Long) student[0];
            String testUsername = (String) student[1];
            String testFullName = (String) student[2];
            String testEmail = (String) student[3];
            String testRole = "Undergraduate";

            System.out.println("[DEV] Launching Undergraduate Dashboard for: " + testFullName + " (" + testUsername + ")");

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