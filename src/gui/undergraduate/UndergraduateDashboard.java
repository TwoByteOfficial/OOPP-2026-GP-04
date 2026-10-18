package gui.undergraduate;

import gui.admin.AdminTheme;
import gui.login.Login;
import gui.undergraduate.dashboard.UGDashboardPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 1 - Undergraduate Main Dashboard Frame
 * UniMis Management Information System
 */
public class UndergraduateDashboard extends JFrame {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private UGSidebarPanel sidebar;

    public UndergraduateDashboard(long userId, String username, String fullName, String email, String role) {
        super(Login.APP_TITLE);
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 650));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AdminTheme.LIGHT_BG);

        // Sidebar Navigation
        sidebar = new UGSidebarPanel(this::navigateTo, this::handleLogout);
        root.add(sidebar, BorderLayout.WEST);

        // Content Area (CardLayout)
        contentCards.setOpaque(false);

        // 1.1 - Dashboard View එකතු කිරීම
        contentCards.add(new UGDashboardPanel(userId, username, fullName, role, this::navigateTo), "dashboard");

        // අනෙක් tabs සඳහා placeholders
        contentCards.add(createPlaceholderPanel("My Courses & Evaluation Criteria"), "courses");
        contentCards.add(createPlaceholderPanel("Attendance & Eligibility Details"), "attendance");
        contentCards.add(createPlaceholderPanel("Grades, SGPA & CGPA Records"), "grades");
        contentCards.add(createPlaceholderPanel("Medical Records Submission & Status"), "medicals");
        contentCards.add(createPlaceholderPanel("Weekly Lecture & Lab Timetable"), "timetable");
        contentCards.add(createPlaceholderPanel("Academic Notices & Announcements"), "notices");
        contentCards.add(createPlaceholderPanel("Student Profile Management"), "profile");

        root.add(contentCards, BorderLayout.CENTER);
        setContentPane(root);
    }

    public void navigateTo(String tabKey) {
        cardLayout.show(contentCards, tabKey);
        sidebar.setActiveTab(tabKey);
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AdminTheme.LIGHT_BG);

        JPanel card = AdminTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLbl = AdminTheme.heading(title);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = AdminTheme.muted("This module is currently linked and ready for implementation.");
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(descLbl);

        p.add(card);
        return p;
    }

    private void handleLogout() {
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of UniMis?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opt == JOptionPane.YES_OPTION) {
            dispose();
            Login.showLogin();
        }
    }
}