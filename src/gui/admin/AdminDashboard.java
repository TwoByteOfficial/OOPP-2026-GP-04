package gui.admin;

import gui.admin.DashboardComponents.*;
import dao.admin.AdminDashboardDAO; // Added Import
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class AdminDashboard extends JPanel {

    private DashboardTitleBar   titleBar;
    private SimpleAnalyticView  analyticsRow;
    private StatusSummary       statusPanel;
    private AttendanceSummary   attendancePanel;
    private UserSummary         userPanel;
    private NoticesSummary      noticesPanel;
    private AdminDashboardDAO   dashboardDAO; // Added Instance Node

    public AdminDashboard(long userId, String username, String fullName, String email, String role) {
        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setOpaque(false);

        // 1. Initialize DB Operations Layer safely
        dashboardDAO = new AdminDashboardDAO();
        Map<String, Integer> dbMetrics = loadLiveDatabaseMetrics();

        /* ========================================================================== */
        /*  ROW 1: TITLE BAR                                                          */
        /* ========================================================================== */
        titleBar = new DashboardTitleBar(fullName);
        titleBar.setLocation(20, 20);
        add(titleBar);

        /* ========================================================================== */
        /*  ROW 2: ANALYTICS CARDS (Start X: 20, End X: 1620)                         */
        /* ========================================================================== */
        // Pass metrics map straight into the analytical visual block layout
        analyticsRow = new SimpleAnalyticView(dbMetrics);
        analyticsRow.setLocation(20, 130);
        add(analyticsRow);

        /* ========================================================================== */
        /*  ROW 3: PROGRESS TRACKERS (Height: 260px)                                  */
        /* ========================================================================== */
        // Status Panel (Left Block: X=20, Width=780)
        statusPanel = new StatusSummary();
        statusPanel.setLocation(40, 270);
        add(statusPanel);

        // Attendance Panel (Right Block: X=820, Width=780)
        // Pass metrics map straight into quick system administrative counts row
        attendancePanel = new AttendanceSummary(dbMetrics);
        attendancePanel.setLocation(820, 270);
        add(attendancePanel);

        /* ========================================================================== */
        /*  ROW 4: DATA TABLES (Height: 410px)                                        */
        /* ========================================================================== */
        // User Profiles Summary (Left Block: X=20, Width=780)
        userPanel = new UserSummary();
        userPanel.setLocation(40, 550);
        add(userPanel);

        // Notices Summary (Right Block: X=820, Width=780)
        noticesPanel = new NoticesSummary();
        noticesPanel.setLocation(820, 550);
        add(noticesPanel);
    }

    /**
     * Executes safe transaction queries to fetch aggregate system status values.
     */
    private Map<String, Integer> loadLiveDatabaseMetrics() {
        try {
            return dashboardDAO.getDashboardSummaryCounts();
        } catch (Exception e) {
            System.err.println("[Dashboard DB Error] Failed to aggregate system summary counts.");
            e.printStackTrace();

            // Build absolute empty state mapping defaults to isolate layout runtime breakage
            Map<String, Integer> fallbacks = new HashMap<>();
            fallbacks.put("admins", 0);
            fallbacks.put("lecturers", 0);
            fallbacks.put("technical_officers", 0);
            fallbacks.put("undergraduates", 0);
            fallbacks.put("courses", 0);
            fallbacks.put("notices", 0);
            fallbacks.put("timetables", 0);
            return fallbacks;
        }
    }
}
