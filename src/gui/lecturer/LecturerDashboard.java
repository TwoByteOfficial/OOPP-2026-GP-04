
package gui.lecturer;

import dao.Lecturer.CourseDAO;
import dao.Lecturer.DashboardStatsDAO;
import dao.Lecturer.NoticeDAO;

import gui.login.Login;
import gui.lecturer.DashboardComponents.CourseModulesTable;
import gui.lecturer.DashboardComponents.DashboardTitleBar;
import gui.lecturer.DashboardComponents.RecentNotices;
import gui.lecturer.DashboardComponents.StatCard;
import gui.lecturer.SharedComponents.LecturerSidebar;
import gui.lecturer.SharedComponents.LecturerTopbar;

import java.sql.SQLException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerDashboard extends JFrame {

    private static final Color PALE_GREEN =
            new Color(232, 245, 233);

    private static final Color BORDER_GREEN =
            new Color(200, 230, 201);

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private final LecturerSidebar sidebar;
    private final LecturerTopbar topbar;
    private final CardLayout cardLayout;
    private final JPanel pageContainer;

    private final CourseModulesTable courseModulesTable;
    private final RecentNotices recentNotices;

    private StatCard assignedCoursesCard;
    private StatCard undergraduatesCard;
    private StatCard pendingMarksCard;
    private StatCard newNoticesCard;

    public LecturerDashboard(
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
        setLocationRelativeTo(null);

        sidebar = new LecturerSidebar();
        topbar = new LecturerTopbar(getLecturerName());

        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);
        pageContainer.setBackground(PALE_GREEN);

        courseModulesTable = new CourseModulesTable();
        recentNotices = new RecentNotices();

        pageContainer.add(
                createDashboardPage(),
                "Dashboard"
        );

        addPlaceholderPages();

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        connectNavigation();

        loadAssignedCourses();
        loadDashboardStats();
        loadRecentNotices();

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

    private JPanel createDashboardPage() {
        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setBackground(PALE_GREEN);
        page.setBorder(new EmptyBorder(28, 30, 30, 30));

        DashboardTitleBar titleBar =
                new DashboardTitleBar(getLecturerName());

        // Four statistic cards
        JPanel statisticsPanel = new JPanel(
                new GridLayout(1, 4, 18, 0)
        );
        statisticsPanel.setOpaque(false);

        assignedCoursesCard =
                new StatCard("Assigned Courses", "--", "▣");

        undergraduatesCard =
                new StatCard("Undergraduates", "--", "♙");

        pendingMarksCard =
                new StatCard("Pending Marks", "--", "☷");

        newNoticesCard =
                new StatCard("New Notices", "--", "♧");

        statisticsPanel.add(assignedCoursesCard);
        statisticsPanel.add(undergraduatesCard);
        statisticsPanel.add(pendingMarksCard);
        statisticsPanel.add(newNoticesCard);

        // Course modules on the left, recent notices on the right
        JPanel lowerPanels = new JPanel(
                new GridLayout(1, 2, 22, 0)
        );
        lowerPanels.setOpaque(false);

        lowerPanels.add(courseModulesTable);
        lowerPanels.add(recentNotices);

        // Quick Actions underneath both panels
        JPanel quickActions = createQuickActions();

        JPanel middleContent = new JPanel(
                new BorderLayout(0, 24)
        );
        middleContent.setOpaque(false);

        middleContent.add(
                statisticsPanel,
                BorderLayout.NORTH
        );

        JPanel lowerContent = new JPanel(
                new BorderLayout(0, 24)
        );
        lowerContent.setOpaque(false);

        lowerContent.add(
                lowerPanels,
                BorderLayout.CENTER
        );

        lowerContent.add(
                quickActions,
                BorderLayout.SOUTH
        );

        middleContent.add(
                lowerContent,
                BorderLayout.CENTER
        );

        JPanel content = new JPanel(
                new BorderLayout(0, 24)
        );
        content.setOpaque(false);

        content.add(titleBar, BorderLayout.NORTH);
        content.add(middleContent, BorderLayout.CENTER);

        page.add(content, BorderLayout.CENTER);

        return page;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(
                new BorderLayout(0, 14)
        );
        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_GREEN, 1, true
                        ),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );

        JLabel title = new JLabel("Quick Actions");
        title.setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );
        title.setForeground(new Color(27, 94, 32));

        JPanel buttons = new JPanel(
                new GridLayout(1, 3, 16, 0)
        );
        buttons.setOpaque(false);

        buttons.add(createQuickActionButton(
                "Enter Marks", "MarksGrades"));

        buttons.add(createQuickActionButton(
                "Add Material", "CourseMaterials"));

        buttons.add(createQuickActionButton(
                "Check Eligibility", "Eligibility"));

        panel.add(title, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.CENTER);

        return panel;
    }

    private JButton createQuickActionButton(
            String label,
            String targetPage) {

        JButton button = new JButton(label);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        button.setForeground(new Color(27, 94, 32));
        button.setBackground(PALE_GREEN);
        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_GREEN, 1, true
                        ),
                        new EmptyBorder(16, 12, 16, 12)
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addActionListener(e -> {
            sidebar.selectPage(targetPage);
            cardLayout.show(pageContainer, targetPage);
        });

        return button;
    }

    private void addPlaceholderPages() {
        String[][] pages = {
                {"My Courses", "MyCourses"},
                {"Course Materials", "CourseMaterials"},
                {"Marks & Grades", "MarksGrades"},
                {"Undergraduates", "Undergraduates"},
                {"Eligibility", "Eligibility"},
                {"Attendance", "Attendance"},
                {"Notices", "Notices"},
                {"My Profile", "Profile"}
        };

        for (String[] page : pages) {
            JPanel placeholder = new JPanel(
                    new BorderLayout()
            );
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

        sidebar.addPropertyChangeListener(
                "pageSelected",
                event -> {
                    String selectedPage =
                            (String) event.getNewValue();

                    if (selectedPage != null) {
                        cardLayout.show(
                                pageContainer,
                                selectedPage
                        );
                    }
                }
        );

        sidebar.addPropertyChangeListener(
                "logoutRequested",
                event -> {
                    if (Boolean.TRUE.equals(
                            event.getNewValue())) {

                        int choice =
                                JOptionPane.showConfirmDialog(
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

        topbar.addPropertyChangeListener(
                "notificationsRequested",
                event -> {
                    if (Boolean.TRUE.equals(
                            event.getNewValue())) {

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

    private void loadAssignedCourses() {
        try {
            CourseDAO courseDAO = new CourseDAO();

            List<Object[]> courses =
                    courseDAO.getCoursesByLecturer(userId);

            courseModulesTable.setCourses(courses);

            assignedCoursesCard.setValue(
                    String.valueOf(courses.size())
            );

        } catch (SQLException exception) {
            courseModulesTable.setCourses(List.of());

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load assigned courses.\n"
                            + "Please check your database connection "
                            + "and course assignments.\n\n"
                            + "Details: " + exception.getMessage(),
                    "Course Loading Error",
                    JOptionPane.WARNING_MESSAGE
            );

            exception.printStackTrace();
        }
    }

    private void loadDashboardStats() {
        DashboardStatsDAO statsDAO =
                new DashboardStatsDAO();

        try {
            undergraduatesCard.setValue(
                    String.valueOf(
                            statsDAO.getUndergraduateCount(userId)
                    )
            );

            pendingMarksCard.setValue(
                    String.valueOf(
                            statsDAO.getPendingMarksCount(userId)
                    )
            );

            newNoticesCard.setValue(
                    String.valueOf(
                            statsDAO.getNewNoticesCount()
                    )
            );

        } catch (SQLException exception) {
            undergraduatesCard.setValue("--");
            pendingMarksCard.setValue("--");
            newNoticesCard.setValue("--");

            System.err.println(
                    "Unable to load dashboard statistics: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }

    private void loadRecentNotices() {
        try {
            NoticeDAO noticeDAO = new NoticeDAO();

            List<String[]> notices =
                    noticeDAO.getRecentNotices();

            recentNotices.showNotices(notices);

        } catch (SQLException exception) {
            recentNotices.showNotices(List.of());

            System.err.println(
                    "Unable to load recent notices: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
