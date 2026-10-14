package gui.admin;

import gui.login.Login;

import javax.swing.*;
import java.awt.*;

/**
 * Main Administrator frame.
 * Hosts the sidebar + content area and routes between admin pages.
 */
public class Administrator extends JFrame {

    /* ---- Logged-in user info ---- */
    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    /* ---- UI ---- */
    private AdminSidebar sidebar;
    private JPanel       contentArea;
    private CardLayout   contentLayout;

    /* ---- Pages (lazy references) ---- */
    private AdminDashboard    pageDashboard;
//    private AdminUserProfiles pageUsers;
//    private AdminCourses      pageCourses;
//    private AdminNotices      pageNotices;
//    private AdminTimetable    pageTimetable;
//    private AdminReports      pageReports;
//    private AdminSettings     pageSettings;

    /* ------------------------------------------------------------------ */
    /*  Constructors                                                      */
    /* ------------------------------------------------------------------ */

    /** No-arg constructor kept for compatibility. */
    public Administrator() {
        this(0L, "-", "-", "-", "admin");
    }

    /** Main constructor used by Login routing. */
    public Administrator(long userId, String username, String fullName,
                         String email, String role) {

        super(Login.APP_TITLE + "  —  Administrator");

        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        // Role-based authorization
        if (!isAdmin(role)) {
            showAccessDeniedAndLogout(role);
            return;
        }

        initUI();
    }

    /* ------------------------------------------------------------------ */
    /*  Role check                                                        */
    /* ------------------------------------------------------------------ */

    private static boolean isAdmin(String role) {
        if (role == null) return false;
        String key = role.replaceAll("[\\s_-]+", "").toLowerCase();
        return key.equals("admin") || key.equals("administrator");
    }

    private void showAccessDeniedAndLogout(String actualRole) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Access denied.\nThis dashboard is restricted to Administrators.\n" +
                            "Your role: \"" + actualRole + "\"",
                    "Unauthorised",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            Login.showLogin();
        });
    }

    /* ------------------------------------------------------------------ */
    /*  UI                                                                */
    /* ------------------------------------------------------------------ */

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(AdminTheme.LIGHT_BG);
        setLayout(new BorderLayout());

        /* Sidebar */
        sidebar = new AdminSidebar();
        sidebar.setOnNavigate(this::navigateTo);
        sidebar.setOnLogout(this::onLogout);
        add(sidebar, BorderLayout.WEST);

        /* Content Area (switchable pages) */
        contentLayout = new CardLayout();
        contentArea   = new JPanel(contentLayout);
        contentArea.setBackground(AdminTheme.LIGHT_BG);
        add(contentArea, BorderLayout.CENTER);

        buildPages();

        setMinimumSize(new Dimension(1100, 650));
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Default page
        navigateTo(AdminSidebar.PAGE_DASHBOARD);
    }

    private void buildPages() {
        pageDashboard = new AdminDashboard(userId, username, fullName, email, role);
//        pageUsers     = new AdminUserProfiles(userId, username, fullName, email, role);
//        pageCourses   = new AdminCourses(userId, username, fullName, email, role);
//        pageNotices   = new AdminNotices(userId, username, fullName, email, role);
//        pageTimetable = new AdminTimetable(userId, username, fullName, email, role);
//        pageReports   = new AdminReports(userId, username, fullName, email, role);
//        pageSettings  = new AdminSettings(userId, username, fullName, email, role);

        contentArea.add(pageDashboard, AdminSidebar.PAGE_DASHBOARD);
//        contentArea.add(pageUsers,     AdminSidebar.PAGE_USERS);
//        contentArea.add(pageCourses,   AdminSidebar.PAGE_COURSES);
//        contentArea.add(pageNotices,   AdminSidebar.PAGE_NOTICES);
//        contentArea.add(pageTimetable, AdminSidebar.PAGE_TIMETABLE);
//        contentArea.add(pageReports,   AdminSidebar.PAGE_REPORTS);
//        contentArea.add(pageSettings,  AdminSidebar.PAGE_SETTINGS);
    }

    /* ------------------------------------------------------------------ */
    /*  Navigation                                                        */
    /* ------------------------------------------------------------------ */

    private void navigateTo(String key) {
        contentLayout.show(contentArea, key);
        sidebar.setActive(key);
    }

    private void onLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        dispose();
        Login.showLogin();
    }

    /* ------------------------------------------------------------------ */
    /*  Getters (so pages can read logged-in user info if needed)         */
    /* ------------------------------------------------------------------ */

    public long   getUserId()   { return userId;   }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email;    }
    public String getRole()     { return role;     }
}