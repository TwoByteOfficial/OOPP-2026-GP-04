package gui.undergraduate;

import gui.admin.AdminTheme;
import gui.login.Login;
import gui.undergraduate.attendance.UGAttendancePanel;
import gui.undergraduate.courses.UGCoursesPanel;
import gui.undergraduate.dashboard.UGDashboardPanel;
import gui.undergraduate.grades.UGGradesPanel;
import gui.undergraduate.medicals.UGMedicalsPanel;
import gui.undergraduate.notices.UGNoticesPanel;
import gui.undergraduate.profile.UGProfilePanel;
import gui.undergraduate.timetable.UGTimetablePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main Undergraduate Dashboard Frame.
 * Handles primary sidebar navigation and card switching across student modules.
 * Coordinates user session attributes, instantiates student view panels, manages
 * screen transitions via {@link CardLayout}, and coordinates session logout workflows.
 */
public class UndergraduateDashboard extends JFrame {

    // Authenticated student session details
    private final long userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    // View switching layout and container
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private UGSidebarPanel sidebar;

    /**
     * Constructs the primary undergraduate dashboard window with specified session credentials.
     *
     * @param userId   Unique primary key identifier of the authenticated user
     * @param username Institutional login username (e.g., student registration index)
     * @param fullName Full legal name of the student
     * @param email    Institutional email address
     * @param role     System security role designation (e.g., "Undergraduate")
     */
    public UndergraduateDashboard(long userId, String username, String fullName, String email, String role) {
        super(Login.APP_TITLE);
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;

        // Frame window properties and boundary constraints
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 650));
        setLocationRelativeTo(null);

        initUI();
    }

    /**
     * Initializes root window layout, side navigation panel, and student module card decks.
     */
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AdminTheme.LIGHT_BG);

        // -------------------------------------------------------------
        // Sidebar Navigation Assembly
        // -------------------------------------------------------------
        sidebar = new UGSidebarPanel(this::navigateTo, this::handleLogout);
        root.add(sidebar, BorderLayout.WEST);

        // -------------------------------------------------------------
        // Central Content Card Deck Assembly
        // -------------------------------------------------------------
        contentCards.setOpaque(false);

        // Main Student Overview Dashboard
        contentCards.add(new UGDashboardPanel(userId, username, fullName, role, this::navigateTo), "dashboard");

        // Individual Academic & Profile Sub-Modules
        contentCards.add(new UGCoursesPanel(userId, username), "courses");
        contentCards.add(new UGAttendancePanel(userId, username), "attendance");
        contentCards.add(new UGGradesPanel(userId, username), "grades");
        contentCards.add(new UGMedicalsPanel(userId, username), "medicals");
        contentCards.add(new UGTimetablePanel(userId, username), "timetables");
        contentCards.add(new UGNoticesPanel(userId, username), "notices");
        contentCards.add(new UGProfilePanel(userId, username), "profile");

        root.add(contentCards, BorderLayout.CENTER);
        setContentPane(root);
    }

    /**
     * Switches the active center panel to the card matching the specified key and updates sidebar highlighting.
     *
     * @param tabKey Unique string identifier of the destination module card
     */
    public void navigateTo(String tabKey) {
        cardLayout.show(contentCards, tabKey);
        if (sidebar != null) {
            sidebar.setActiveTab(tabKey);
        }
    }

    /**
     * Prompts the user with a confirmation dialog before terminating the active session
     * and restoring the authentication login interface.
     */
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