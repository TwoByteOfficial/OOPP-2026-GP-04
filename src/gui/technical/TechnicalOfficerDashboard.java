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

/**
 * Main dashboard application frame for Technical Officers.
 * Uses a CardLayout to switch between different functional panels:
 * - Dashboard (Statistics, timetable overview, notices, quick actions)
 * - Attendance (Marking and viewing student attendance)
 * - Medical Records (Reviewing and approving/rejecting medical submissions)
 * - Timetables (Weekly department lecture and lab schedule)
 * - Notices (Viewing published notices)
 * - My Profile (Viewing and updating officer contact details)
 */
public class TechnicalOfficerDashboard extends JFrame {

    // Logged-in Technical Officer's account details
    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    // CardLayout and container panel for switching views smoothly
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(cardLayout);

    // Left navigation sidebar
    private TOSidebarPanel sidebar;

    /**
     * Initializes the Technical Officer Dashboard window with user credentials and views.
     *
     * @param userId   Logged-in user's database ID
     * @param username Login username
     * @param fullName Full name of the Technical Officer
     * @param email    Email address
     * @param role     User role ("Technical Officer")
     */
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

        // Window configuration
        setTitle("UniMIS - Technical Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1200, 750);
        setResizable(true);

        pageContainer.setBackground(UniMisTheme.LIGHT_BG);

        // Sidebar navigation: clicking a menu item flips the CardLayout and updates sidebar selection
        sidebar = new TOSidebarPanel(
                fullName,
                pageName -> {
                    cardLayout.show(pageContainer, pageName);
                    sidebar.selectPage(pageName);
                },
                this::logout
        );

        // Main dashboard home view (wrapped in a smooth scroll pane)
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

        JScrollPane dashboardScrollPane = new JScrollPane(dashboardPanel);
        dashboardScrollPane.setBorder(null);
        dashboardScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dashboardScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Register each page in the CardLayout container
        pageContainer.add(dashboardScrollPane, "Dashboard");
        pageContainer.add(new AttendancePanel(userId), "Attendance");
        pageContainer.add(new MedicalRecordsPanel(userId), "Medical Records");
        pageContainer.add(new DepartmentTimetablePanel(userId), "Timetables");
        pageContainer.add(new NoticesPanel(userId), "Notices");
        pageContainer.add(new TechnicalOfficerProfilePanel(userId), "My Profile");

        // Assemble root layout: sidebar on left (WEST), content pages in center
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UniMisTheme.LIGHT_BG);
        root.add(sidebar, BorderLayout.WEST);
        root.add(pageContainer, BorderLayout.CENTER);

        setContentPane(root);
        setLocationRelativeTo(null);
    }

    /**
     * Prompts the Technical Officer with a confirmation dialog before logging out.
     * If confirmed, closes this dashboard and displays the login screen.
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        dispose();
        Login.showLogin();
    }
}
