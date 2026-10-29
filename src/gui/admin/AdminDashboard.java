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
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));

        dashboardDAO = new AdminDashboardDAO();
        Map<String, Integer> dbMetrics = loadLiveDatabaseMetrics();

        titleBar = new DashboardTitleBar(fullName);
        add(titleBar, BorderLayout.NORTH);

        JPanel body = new JPanel(null);
        body.setOpaque(false);

        analyticsRow = new SimpleAnalyticView(dbMetrics);
        analyticsRow.setBounds(0, 0, 1620, 130);
        body.add(analyticsRow);

        //--------------------------------------------------------

        statusPanel = new StatusSummary();
        statusPanel.setBounds(0, 125, 778, 260);
        body.add(statusPanel);

        attendancePanel = new AttendanceSummary(dbMetrics);
        attendancePanel.setBounds(798, 125, 778, 260);
        body.add(attendancePanel);

        userPanel = new UserSummary();
        userPanel.setBounds(0, 400, 778, 450);
        body.add(userPanel);

        noticesPanel = new NoticesSummary();
        noticesPanel.setBounds(798, 400, 778, 450);
        body.add(noticesPanel);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.getViewport().setBackground(AdminTheme.LIGHT_BG);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
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