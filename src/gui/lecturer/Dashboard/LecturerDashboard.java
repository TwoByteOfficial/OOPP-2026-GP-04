
package gui.lecturer.Dashboard;

import dao.Lecturer.CourseDAO;
import dao.Lecturer.DashboardStatsDAO;
import dao.Lecturer.NoticeDAO;
import gui.lecturer.Dashboard.DashboardComponents.CourseModulesTable;
import gui.lecturer.Dashboard.DashboardComponents.DashboardTitleBar;
import gui.lecturer.Dashboard.DashboardComponents.RecentNotices;
import gui.lecturer.Dashboard.DashboardComponents.StatCard;
import dao.Lecturer.LecturerProfileDAO;
import static gui.lecturer.SharedComponents.Colors.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class LecturerDashboard extends JPanel {

    private final long userId;
    private final String lecturerName;
    private final Consumer<String> onNavigate;

    private final CourseModulesTable courseModulesTable;
    private final RecentNotices recentNotices;

    private StatCard assignedCoursesCard;
    private StatCard undergraduatesCard;
    private StatCard pendingMarksCard;
    private StatCard newNoticesCard;

    private DashboardTitleBar dashboardTitleBar;

    public LecturerDashboard(
            long userId,
            String lecturerName,
            Consumer<String> onNavigate) {

        this.userId = userId;
        this.lecturerName = lecturerName;
        this.onNavigate = onNavigate;

        setLayout(new BorderLayout(0, 24));
        setBackground(PALE_GREEN);
        setBorder(new EmptyBorder(28, 30, 30, 30));

        courseModulesTable = new CourseModulesTable();
        recentNotices = new RecentNotices();

        add(createDashboardContent(), BorderLayout.CENTER);

        refreshGreeting();

        loadAssignedCourses();
        loadDashboardStats();
        loadRecentNotices();
    }

    private JPanel createDashboardContent() {
        JPanel content = new JPanel(new BorderLayout(0, 24));
        content.setOpaque(false);

        dashboardTitleBar = new DashboardTitleBar(lecturerName);

        JPanel statisticsPanel = new JPanel(new GridLayout(1, 4, 18, 0));
        statisticsPanel.setOpaque(false);

        assignedCoursesCard = new StatCard("Assigned Courses", "--", "▣");
        undergraduatesCard = new StatCard("Undergraduates", "--", "♙");
        pendingMarksCard = new StatCard("Pending Marks", "--", "☷");
        newNoticesCard = new StatCard("New Notices", "--", "♧");

        statisticsPanel.add(assignedCoursesCard);
        statisticsPanel.add(undergraduatesCard);
        statisticsPanel.add(pendingMarksCard);
        statisticsPanel.add(newNoticesCard);

        JPanel lowerPanels = new JPanel(new GridLayout(1, 2, 22, 0));
        lowerPanels.setOpaque(false);

        lowerPanels.add(courseModulesTable);
        lowerPanels.add(recentNotices);

        JPanel middleContent = new JPanel(new BorderLayout(0, 24));
        middleContent.setOpaque(false);

        middleContent.add(statisticsPanel, BorderLayout.NORTH);

        JPanel lowerContent = new JPanel(new BorderLayout(0, 24));
        lowerContent.setOpaque(false);
        lowerContent.add(lowerPanels, BorderLayout.CENTER);
        lowerContent.add(createQuickActions(), BorderLayout.SOUTH);
        middleContent.add(lowerContent, BorderLayout.CENTER);
        content.add(dashboardTitleBar, BorderLayout.NORTH);
        content.add(middleContent, BorderLayout.CENTER);

        return content;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(WHITE);

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
        title.setForeground(DEEP_GREEN);

        JPanel buttons =
                new JPanel(new GridLayout(1, 3, 16, 0));
        buttons.setOpaque(false);

        buttons.add(
                createQuickActionButton(
                        "Enter Marks", "MarksGrades"
                )
        );

        buttons.add(
                createQuickActionButton(
                        "Add Material", "CourseMaterials"
                )
        );

        buttons.add(
                createQuickActionButton(
                        "Check Eligibility", "Eligibility"
                )
        );

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
        button.setForeground(DEEP_GREEN);
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

        button.addActionListener(event -> {
            if (onNavigate != null) {
                onNavigate.accept(targetPage);
            }
        });

        return button;
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
            assignedCoursesCard.setValue("--");

            System.err.println(
                    "Unable to load assigned courses: "
                            + exception.getMessage()
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

    public void refreshGreeting() {
        try {
            LecturerProfileDAO profileDAO =
                    new LecturerProfileDAO();

            Object[] profile =
                    profileDAO.getLecturerProfile(userId);

            if (profile != null && dashboardTitleBar != null) {
                String title = (String) profile[3];
                String name = (String) profile[4];

                dashboardTitleBar.updateGreeting(title, name);
            }

        } catch (SQLException exception) {
            System.err.println(
                    "Unable to refresh dashboard greeting: "
                            + exception.getMessage()
            );
        }
    }
}
