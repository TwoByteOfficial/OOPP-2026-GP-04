
package gui.lecturer;

import dao.Lecturer.LecturerProfileDAO;

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
import java.sql.SQLException;

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

        // Dashboard
        LecturerDashboard dashboard =
                new LecturerDashboard(
                        userId,
                        getLecturerName(),
                        this::navigateTo
                );

        pageContainer.add(dashboard, "Dashboard");

        // My Courses
        pageContainer.add(
                new MyCourses(userId),
                "MyCourses"
        );

        // Course Materials
        pageContainer.add(
                new LecturerCourseMaterials(userId),
                "CourseMaterials"
        );

        // Marks & Grades
        pageContainer.add(
                new LecturerMarksGrades(userId),
                "MarksGrades"
        );

        // Undergraduates
        pageContainer.add(
                new LecturerUndergraduates(userId),
                "Undergraduates"
        );

        // Eligibility
        pageContainer.add(
                new LecturerEligibility(userId),
                "Eligibility"
        );

        // Attendance
        pageContainer.add(
                new LecturerAttendance(userId),
                "Attendance"
        );

        // Notices
        pageContainer.add(
                new LecturerNotices(),
                "Notices"
        );

        // Profile with dashboard and topbar callbacks
        LecturerProfile profile = new LecturerProfile(
                userId,
                dashboard::refreshGreeting,
                topbar::setProfilePictureUrl
        );

        pageContainer.add(profile, "Profile");

        // Main layout
        setLayout(new BorderLayout());

        add(sidebar, BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        connectNavigation();

        // Load saved profile picture from database.
        loadLecturerProfilePicture();

        // Open dashboard first.
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

        sidebar.selectPage(pageName);
        cardLayout.show(pageContainer, pageName);
    }

    private void loadLecturerProfilePicture() {

        try {

            LecturerProfileDAO dao =
                    new LecturerProfileDAO();

            Object[] profile =
                    dao.getLecturerProfile(userId);

            if (profile != null
                    && profile.length > 11
                    && profile[11] != null) {

                String pictureUrl =
                        profile[11].toString();

                topbar.setProfilePictureUrl(pictureUrl);
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to load lecturer profile picture: "
                            + e.getMessage()
            );
        }
    }

    private void connectNavigation() {

        // Sidebar navigation
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

        // Topbar circular profile picture click
        topbar.addPropertyChangeListener(
                "profileRequested",
                event -> {

                    if (Boolean.TRUE.equals(event.getNewValue())) {
                        navigateTo("Profile");
                    }
                }
        );
    }
}
