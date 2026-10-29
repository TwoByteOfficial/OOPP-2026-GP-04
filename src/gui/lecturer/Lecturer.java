package gui.lecturer;

import gui.lecturer.Attendance.LecturerAttendance;
import gui.lecturer.CourseMaterial.LecturerCourseMaterials;
import gui.lecturer.Dashboard.LecturerDashboard;
import gui.lecturer.Eligibility.LecturerEligibility;
import gui.lecturer.LecturerMarksGrades.LecturerMarksGrades;
import gui.lecturer.MyCourse.MyCourses;
import gui.lecturer.Notices.LecturerNotices;
import gui.lecturer.Profile.LecturerProfile;
import gui.lecturer.SharedComponents.LecturerSidebar;
import gui.lecturer.SharedComponents.LecturerTopbar;
import gui.lecturer.Undergraduate.LecturerUndergraduates;
import gui.login.Login;
import static gui.lecturer.SharedComponents.Colors.APP_BACKGROUND;

import javax.swing.*;
import java.awt.*;

public class Lecturer extends JFrame {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private final LecturerSidebar sidebar;
    private final LecturerTopbar topbar;

    private final CardLayout cardLayout;
    private final JPanel pageContainer;

    public Lecturer(long userId, String username, String fullName, String email, String role) {

        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setTitle("UniMIS - Lecturer Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));
        setSize(1440, 850);

        sidebar = new LecturerSidebar();
        topbar = new LecturerTopbar(getLecturerName());
        topbar.setHasNewNotifications(true);

        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);
        pageContainer.setBackground(APP_BACKGROUND);

        // Dashboard home page
        LecturerDashboard dashboard =
                new LecturerDashboard(
                        userId,
                        getLecturerName(),
                        this::navigateTo
                );

        pageContainer.add(dashboard, "Dashboard");

        // Lecturer Pages
        pageContainer.add(new MyCourses(userId), "MyCourses");

        pageContainer.add(
                new LecturerCourseMaterials(userId),
                "CourseMaterials"
        );

        pageContainer.add(
                new LecturerMarksGrades(userId),
                "MarksGrades"
        );

        pageContainer.add(
                new LecturerUndergraduates(userId),
                "Undergraduates"
        );

        pageContainer.add(
                new LecturerEligibility(userId),
                "Eligibility"
        );

        pageContainer.add(
                new LecturerAttendance(userId),
                "Attendance"
        );

        pageContainer.add(
                new LecturerNotices(),
                "Notices"
        );

        pageContainer.add(
                new LecturerProfile(
                        userId,
                        dashboard::refreshGreeting
                ),
                "Profile"
        );

        // Main layout
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        connectNavigation();

        // Open the dashboard first
        cardLayout.show(pageContainer, "Dashboard");

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private String getLecturerName() {
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }

        if (username != null && !username.isBlank()) {
            return username;
        }

        return "Lecturer";
    }

    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(APP_BACKGROUND);

        topbar.setPreferredSize(new Dimension(0, 100));

        mainArea.add(topbar, BorderLayout.NORTH);
        mainArea.add(pageContainer, BorderLayout.CENTER);

        return mainArea;
    }

    private void navigateTo(String pageName) {
        if (pageName == null) {
            return;
        }

        cardLayout.show(pageContainer, pageName);
    }

    private void connectNavigation() {

        // Sidebar page selection
        sidebar.addPropertyChangeListener(
                "pageSelected",
                event -> {
                    String selectedPage =
                            (String) event.getNewValue();

                    if (selectedPage != null) {
                        navigateTo(selectedPage);
                    }
                }
        );

        // Logout
        sidebar.addPropertyChangeListener(
                "logoutRequested",
                event -> {
                    if (Boolean.TRUE.equals(event.getNewValue())) {
                        int choice = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to log out?",
                                "Confirm Logout",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (choice == JOptionPane.YES_OPTION) {
                            dispose();
                            try {
                                Login.showLogin();
                            } catch (Exception e) {
                                System.exit(0);
                            }
                        }
                    }
                }
        );

        // Topbar notifications
        topbar.addPropertyChangeListener(
                "notificationsRequested",
                event -> {
                    if (Boolean.TRUE.equals(event.getNewValue())) {
                        navigateTo("Notices");
                    }
                }
        );
    }
}