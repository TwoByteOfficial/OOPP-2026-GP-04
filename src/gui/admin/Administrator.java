package gui.admin;

import gui.login.Login;
import javax.swing.*;
import java.awt.*;

public class Administrator extends JFrame {

    private final Color MINT = new Color(0xE8, 0xF5, 0xE9);

    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private AdminSidebar sidebar;
    private JPanel       contentArea;
    private CardLayout   contentLayout;

    private AdminDashboard pageDashboard;
    private AdminUserProfiles pageUsers;
    private AdminCourses pageCourses;
    private AdminNotices pageNotices;
    private AdminTimetable pageTimetable;
    private AdminSettings pageSettings;

    public Administrator() {
        this(1L, "admin_dev", "System Administrator", "admin@unimis.edu", "Admin");
    }

    public Administrator(long userId, String username, String fullName, String email, String role) {
        super("UniMis Academic Information System  —  Administrator");

        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(MINT);

        sidebar = new AdminSidebar();
        sidebar.setBounds(0, 0, 260, 1080);
        sidebar.setOnNavigate(this::navigateTo);
        sidebar.setOnLogout(this::onLogout);
        getContentPane().add(sidebar);

        contentLayout = new CardLayout();
        contentArea   = new JPanel(contentLayout);
        contentArea.setOpaque(false);
        contentArea.setBounds(260, 0, 1660, 1080);
        getContentPane().add(contentArea);

        buildPages();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);

        navigateTo(AdminSidebar.PAGE_DASHBOARD);
    }

    private void buildPages() {
        pageDashboard = new AdminDashboard(userId, username, fullName, email, role);
        contentArea.add(pageDashboard, AdminSidebar.PAGE_DASHBOARD);

        pageUsers = new AdminUserProfiles(userId, username, fullName, email, role);
        contentArea.add(pageUsers, AdminSidebar.PAGE_USERS);

        pageCourses = new AdminCourses(userId, username, fullName, email, role);
        contentArea.add(pageCourses, AdminSidebar.PAGE_COURSES);

        pageNotices = new AdminNotices(userId, username, fullName, email, role);
        contentArea.add(pageNotices, AdminSidebar.PAGE_NOTICES);

        pageTimetable = new AdminTimetable(userId, username, fullName, email, role);
        contentArea.add(pageTimetable, AdminSidebar.PAGE_TIMETABLE);

        pageSettings = new AdminSettings(userId, username, fullName, email, role);
        contentArea.add(pageSettings, AdminSidebar.PAGE_SETTINGS);
    }

    private void navigateTo(String key) {
        contentLayout.show(contentArea, key);
        sidebar.setActive(key);
        getContentPane().repaint();
    }

    private void onLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to log out of the Administrator panel?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            try {
                Login.showLogin();
            } catch (Exception e) {
                System.out.println("[Logout] Session terminated.");
            }
        }
    }
}