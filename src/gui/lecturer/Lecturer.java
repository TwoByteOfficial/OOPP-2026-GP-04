
package gui.lecturer;

import gui.lecturer.CourseMaterial.LecturerCourseMaterials;
import gui.lecturer.Dashboard.LecturerDashboard;
import gui.lecturer.LecturerMarksGrades.LecturerMarksGrades;
import gui.lecturer.MyCourse.MyCourses;
import gui.login.Login;
import gui.lecturer.SharedComponents.LecturerSidebar;
import gui.lecturer.SharedComponents.LecturerTopbar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Lecturer extends JFrame {

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

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
            String role) {

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

        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);
        pageContainer.setBackground(PALE_GREEN);

        // Dashboard home page
        LecturerDashboard dashboard =
                new LecturerDashboard(
                        userId,
                        getLecturerName(),
                        this::navigateTo
                );

        pageContainer.add(dashboard, "Dashboard");

        // Other lecturer pages
        pageContainer.add(new MyCourses(userId), "MyCourses");

        pageContainer.add(
                new LecturerCourseMaterials(userId),
                "CourseMaterials"
        );

       pageContainer.add(
                new LecturerMarksGrades(userId),
                "MarksGrades"
        );

        addPlaceholderPages();

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
        mainArea.setBackground(PALE_GREEN);

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


    private void addPlaceholderPages() {
        String[][] pages = {
                {"Undergraduates", "Undergraduates"},
                {"Eligibility", "Eligibility"},
                {"Attendance", "Attendance"},
                {"Notices", "Notices"},
                {"My Profile", "Profile"}
        };

        for (String[] page : pages) {
            JPanel placeholder = new JPanel(new BorderLayout());
            placeholder.setBackground(PALE_GREEN);
            placeholder.setBorder(
                    new EmptyBorder(30, 30, 30, 30)
            );

            JLabel title = new JLabel(page[0]);
            title.setFont(
                    new Font("SansSerif", Font.BOLD, 26)
            );
            title.setForeground(new Color(27, 94, 32));

            JLabel message = new JLabel(
                    "This page will be implemented separately."
            );
            message.setFont(
                    new Font("SansSerif", Font.PLAIN, 14)
            );

            JPanel content = new JPanel();
            content.setLayout(
                    new BoxLayout(content, BoxLayout.Y_AXIS)
            );
            content.setOpaque(false);

            content.add(title);
            content.add(Box.createVerticalStrut(12));
            content.add(message);

            placeholder.add(content, BorderLayout.NORTH);
            pageContainer.add(placeholder, page[1]);
        }
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
                            Login.showLogin();
                        }
                    }
                }
        );

        // Topbar notifications
        topbar.addPropertyChangeListener(
                "notificationsRequested",
                event -> {
                    if (Boolean.TRUE.equals(event.getNewValue())) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Your notifications will be displayed here.",
                                "Notifications",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
        );
    }
}
