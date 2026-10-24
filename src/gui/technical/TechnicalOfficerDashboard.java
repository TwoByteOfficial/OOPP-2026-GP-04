
package gui.technical;


import gui.login.Login;
import gui.theme.UniMisTheme;
import gui.technical.dashboard.TODashboardPanel;
import gui.technical.attendance.AttendancePanel;
import gui.technical.dashboard.bottom.DepartmentTimetablePanel;
import gui.technical.NoticesPanel;
import gui.technical.TechnicalOfficerProfilePanel;

import javax.swing.*;
import java.awt.*;

public class TechnicalOfficerDashboard extends JFrame {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(cardLayout);
    private TOSidebarPanel sidebar;

    public TechnicalOfficerDashboard(
            long userId,
            String username,
            String fullName,
            String email,
            String role) {

        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setTitle("UniMIS - Technical Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1200, 750);
        setResizable(true);

        pageContainer.setBackground(UniMisTheme.LIGHT_BG);

        // Dashboard page
        // Sidebar
        sidebar = new TOSidebarPanel(
                fullName,
                pageName -> {
                    cardLayout.show(pageContainer, pageName);
                    sidebar.selectPage(pageName);
                },
                this::logout
        );

// Dashboard page
        TODashboardPanel dashboardPanel =
                new TODashboardPanel(
                        userId,
                        fullName,
                        role,
                        pageName -> {
                            cardLayout.show(pageContainer, pageName);
                            sidebar.selectPage(pageName);
                        }
                );

        JScrollPane dashboardScrollPane =
                new JScrollPane(dashboardPanel);

        dashboardScrollPane.setBorder(null);
        dashboardScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dashboardScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pageContainer.add(dashboardScrollPane, "Dashboard");

// Temporary pages
// Dashboard pages
        pageContainer.add(new AttendancePanel(), "Attendance");
        pageContainer.add(
                new MedicalRecordsPanel(userId),
                "Medical Records"
        );



        pageContainer.add(new DepartmentTimetablePanel(userId), "Timetables");
        pageContainer.add(new NoticesPanel(userId), "Notices");
        pageContainer.add(
                new TechnicalOfficerProfilePanel(userId),
                "My Profile"
        );





        dashboardScrollPane.setBorder(null);
        dashboardScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dashboardScrollPane.getVerticalScrollBar().setUnitIncrement(16);







        // Main window layout
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UniMisTheme.LIGHT_BG);
        root.add(sidebar, BorderLayout.WEST);
        root.add(pageContainer, BorderLayout.CENTER);

        setContentPane(root);
        setLocationRelativeTo(null);
    }

    private void addPlaceholderPage(String pageName) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UniMisTheme.LIGHT_BG);

        JLabel title = new JLabel(
                pageName + " - Under Development",
                SwingConstants.CENTER
        );

        title.setFont(UniMisTheme.FONT_H2);
        title.setForeground(UniMisTheme.TEXT);

        panel.add(title, BorderLayout.CENTER);
        pageContainer.add(panel, pageName);
    }

    private void logout() {
        dispose();
        Login.showLogin();
    }
}
