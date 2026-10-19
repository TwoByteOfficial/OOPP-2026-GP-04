package gui.admin;

import gui.admin.DashboardComponents.*;
import dao.admin.AdminDashboardDAO;
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
    private AdminDashboardDAO   dashboardDAO;

    public AdminDashboard(long userId, String username, String fullName, String email, String role) {
        setLayout(null);
        setBounds(0, 0, 1660, 1080);
        setOpaque(false);

        dashboardDAO = new AdminDashboardDAO();
        Map<String, Integer> dbMetrics = loadLiveDatabaseMetrics();

        titleBar = new DashboardTitleBar(fullName);
        titleBar.setLocation(20, 20);
        add(titleBar);

        analyticsRow = new SimpleAnalyticView(dbMetrics);
        analyticsRow.setLocation(20, 120);
        add(analyticsRow);

        statusPanel = new StatusSummary();
        statusPanel.setLocation(20, 270);
        add(statusPanel);

        attendancePanel = new AttendanceSummary(dbMetrics);
        attendancePanel.setLocation(830, 270);
        add(attendancePanel);

        userPanel = new UserSummary();
        userPanel.setLocation(20, 550);
        add(userPanel);

        noticesPanel = new NoticesSummary();
        noticesPanel.setLocation(830, 550);
        add(noticesPanel);
    }

    private Map<String, Integer> loadLiveDatabaseMetrics() {
        try {
            return dashboardDAO.getDashboardSummaryCounts();
        } catch (Exception e) {
            System.err.println("[Dashboard DB Error] Failed to aggregate system summary counts.");
            e.printStackTrace();

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
