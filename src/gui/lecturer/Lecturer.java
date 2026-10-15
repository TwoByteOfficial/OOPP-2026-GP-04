
package gui.lecturer;

import gui.login.Login;
import gui.lecturer.SharedComponents.LecturerSidebar;
import gui.lecturer.SharedComponents.LecturerTopbar;
import gui.lecturer.SharedComponents.PagePlaceholder;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lecturer extends JFrame {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private final CardLayout cardLayout = new CardLayout();

    private final JPanel contentPanel =
            new JPanel(cardLayout);

    private final Map<String, String[]> pageInfo =
            new LinkedHashMap<>();

    private LecturerSidebar sidebar;
    private LecturerTopbar topbar;

    private final Color mintBackground =
            new Color(232, 245, 233);

    public Lecturer(
            long userId,
            String username,
            String fullName,
            String email,
            String role
    ) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setTitle("UniMIS - Lecturer Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1100, 700));
        setSize(1400, 850);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Shared components
        sidebar = new LecturerSidebar(
                this::showPage,
                this::logout
        );

        topbar = new LecturerTopbar(
                fullName,
                role
        );

        contentPanel.setBackground(mintBackground);

        // Add each page to the CardLayout.
        addPage(
                "dashboard",
                "Dashboard",
                "Overview of your teaching activities"
        );

        addPage(
                "courses",
                "My Courses",
                "View your assigned courses"
        );

        addPage(
                "materials",
                "Course Materials",
                "Manage materials for your assigned courses"
        );

        addPage(
                "marks",
                "Marks & Grades",
                "Manage student marks and grades"
        );

        addPage(
                "undergraduates",
                "Undergraduates",
                "View undergraduate information"
        );

        addPage(
                "eligibility",
                "Eligibility",
                "Review student examination eligibility"
        );

        addPage(
                "attendance",
                "Attendance",
                "Review student attendance records"
        );

        addPage(
                "notices",
                "Notices",
                "View notices relevant to you"
        );

        addPage(
                "profile",
                "My Profile",
                "View and manage your lecturer profile"
        );

        // Main area contains the topbar and current page.
        JPanel mainArea = new JPanel(
                new BorderLayout()
        );

        mainArea.add(topbar, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainArea, BorderLayout.CENTER);

        showPage("dashboard");
    }



    private void addPage(String pageKey, String title, String subtitle) {
        pageInfo.put(pageKey, new String[]{title, subtitle});

        if (pageKey.equals("dashboard")) {
            LecturerDashboard dashboard = new LecturerDashboard(fullName);
            contentPanel.add(dashboard, pageKey);

        } else if (pageKey.equals("courses")) {
            LecturerMyCourses myCourses = new LecturerMyCourses();
            contentPanel.add(myCourses, pageKey);

        } else {
            PagePlaceholder placeholder = new PagePlaceholder(
                    title + " page will be implemented next."
            );
            contentPanel.add(placeholder, pageKey);
        }
    }



    private void showPage(String pageKey) {

        String[] details = pageInfo.get(pageKey);

        if (details == null) {
            return;
        }

        cardLayout.show(contentPanel, pageKey);

        sidebar.setActivePage(pageKey);

        topbar.setPageInfo(
                details[0],
                details[1]
        );
    }

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .setFullScreenWindow(null);

            dispose();

            Login.showLogin();
        }
    }
}
