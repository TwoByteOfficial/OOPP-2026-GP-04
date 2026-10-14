package gui.admin;

import gui.admin.pages.*;
import gui.login.Login;
import model.Admin;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Admin dashboard: fixed left sidebar + top bar + swappable content.
 * Replaces the old single-panel placeholder.
 */
public class AdminDashboard extends JFrame {

    private final User   currentUser;
    private final long   userId;
    private final String fullName, email, role;

    private final AdminSidebar sidebar;
    private final AdminTopBar  topBar = new AdminTopBar();
    private final JPanel       contentArea = new JPanel(new BorderLayout());

//    public AdminDashboard(long userId, String username, String fullName,
//                          String email, String role) {
//        this(new Admin(userId, username, fullName, email, role));
//    }

    public AdminDashboard(Admin admin) {
        this.currentUser = admin;
        this.userId   = admin.getUserId();
        this.fullName = admin.getFullName();
        this.email    = admin.getEmail();
        this.role     = admin.getRole();

        setTitle("UniMis - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));

        sidebar = new AdminSidebar(fullName, this::showPage, this::doLogout);

        contentArea.setBackground(AdminTheme.LIGHT);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(AdminTheme.LIGHT);
        right.add(topBar, BorderLayout.NORTH);
        right.add(contentArea, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, right);
        split.setDividerSize(0);
        split.setBorder(null);
        split.setResizeWeight(0);
        setContentPane(split);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        topBar.setUser(fullName, role, initialsOf(fullName));
        refreshNotificationCount();
        showPage("dashboard");
    }

    /* ---------- Navigation ---------- */

    public void showPage(String key) {
        contentArea.removeAll();
        JPanel page = switch (key) {
            case "users"      -> new UsersPage(this);
            case "courses"    -> new CoursesPage(this);
            //case "notices"    -> new NoticesPage(this);
            //case "timetables" -> new TimetablePage(this);
            //case "reports"    -> new ReportsPage(this);
            //case "settings"   -> new SettingsPage(this);
            default           -> new DashboardPage(this);
        };
        contentArea.add(page, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();

        String[] titles = switch (key) {
            case "users"      -> new String[]{"User Profile Management", "Create, update and maintain system users"};
            case "courses"    -> new String[]{"Course Management",       "Create and maintain course module details"};
            case "notices"    -> new String[]{"Notice Management",       "Publish announcements for the faculty"};
            case "timetables" -> new String[]{"Timetable Management",    "Create and maintain department timetables"};
            case "reports"    -> new String[]{"Academic Reports",        "Attendance, CA, eligibility, grades, SGPA and CGPA"};
            case "settings"   -> new String[]{"System Settings",         "Configure academic parameters"};
            default           -> new String[]{"Admin Dashboard",         "Overview of Faculty of Technology academic activities"};
        };
        topBar.setTitle(titles[0], titles[1]);
        sidebar.setActive(key);
        if ("notices".equals(key)) refreshNotificationCount();
    }

    /* ---------- Session ---------- */

    private void doLogout() {
        if (AdminTheme.confirm(this, "Log out of the system?")) {
            dispose();
            Login.showLogin();
        }
    }

    public void refreshNotificationCount() {
        // Simple: count of published notices. Replace with unread count later.
        try {
            int n = new dao.NoticeDAO().getAllNotices().size();
            topBar.setNotificationCount(n);
        } catch (Exception ignored) {}
    }

    /* ---------- Accessors for pages ---------- */

    public User   getUser()   { return currentUser; }
    public long   getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }

    private static String initialsOf(String name) {
        if (name == null || name.isBlank()) return "AD";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        return sb.toString();
    }
}