package gui.undergraduate.courses;

import dao.undergraduate.UndergraduateCourseDAO;
import dao.undergraduate.UndergraduateCourseDAO.EnrolledCourseDTO;
import gui.admin.AdminTheme;
import gui.undergraduate.courses.directory.CourseDirectoryPanel;
import gui.undergraduate.courses.evaluation.CourseEvaluationPanel;
import gui.undergraduate.courses.header.CoursesHeaderPanel;
import gui.undergraduate.courses.summary.CoursesSummaryStripPanel;
import gui.undergraduate.enrollment.UGEnrollmentView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 1.2: My Courses view coordinator.
 * Integrates enrolled courses inspection and dynamic course registration
 * through an ergonomic segmented pill switcher with dynamic header updates.
 */
public class UGCoursesPanel extends JPanel {

    // Current authenticated user session details
    private final long userId;
    private final String username;

    // Sub-view component references
    private final CoursesHeaderPanel headerPanel;
    private final CourseDirectoryPanel directoryPanel;
    private final CourseEvaluationPanel evaluationPanel;
    private final CoursesSummaryStripPanel summaryStripPanel;
    private final UGEnrollmentView enrollmentView;

    // Fast in-memory cache mapping courseCode -> EnrolledCourseDTO
    private final Map<String, EnrolledCourseDTO> courseMap = new HashMap<>();

    // CardLayout manager for sub-tab swapping
    private final CardLayout tabLayout = new CardLayout();
    private final JPanel tabDeck = new JPanel(tabLayout);

    // Segmented tab switch buttons
    private final JButton enrolledTabBtn = new JButton("Enrolled Modules");
    private final JButton registerTabBtn = new JButton("Course Registration");
    private String currentActiveTab = "enrolled";

    public UGCoursesPanel(long userId, String username) {
        this.userId = userId;
        this.username = username;

        // Apply standard theme padding and background color
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // -------------------------------------------------------------
        // 1. Top Section: Header + Sub-Tab Pill Switcher
        // -------------------------------------------------------------
        JPanel topWrapper = new JPanel(new BorderLayout(15, 0));
        topWrapper.setOpaque(false);

        // Header panel reference kept for dynamic tab title switching
        headerPanel = new CoursesHeaderPanel();
        topWrapper.add(headerPanel, BorderLayout.WEST);

        // Segmented pill switcher container on top right
        JPanel switchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        switchWrapper.setOpaque(false);

        // Custom rounded container for the segmented pill bar
        JPanel pillBar = new JPanel(new GridLayout(1, 2, 4, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(215, 235, 222));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        pillBar.setOpaque(false);
        pillBar.setBorder(new EmptyBorder(3, 3, 3, 3));

        // Configure button styles and tab click listeners
        styleTabPill(enrolledTabBtn);
        styleTabPill(registerTabBtn);

        enrolledTabBtn.addActionListener(e -> switchSubTab("enrolled"));
        registerTabBtn.addActionListener(e -> switchSubTab("register"));

        pillBar.add(enrolledTabBtn);
        pillBar.add(registerTabBtn);
        switchWrapper.add(pillBar);
        topWrapper.add(switchWrapper, BorderLayout.EAST);

        add(topWrapper, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Card Deck Setup (Swapping Enrolled & Registration views)
        // -------------------------------------------------------------
        tabDeck.setOpaque(false);

        // View 1: Enrolled Courses Layout
        JPanel enrolledDeck = new JPanel(new BorderLayout(0, 14));
        enrolledDeck.setOpaque(false);

        // 4-card metric strip showing credits and track breakdown
        summaryStripPanel = new CoursesSummaryStripPanel();
        enrolledDeck.add(summaryStripPanel, BorderLayout.NORTH);

        // Evaluation breakdown on right; directory table on left
        evaluationPanel = new CourseEvaluationPanel();
        directoryPanel = new CourseDirectoryPanel(courseCode -> {
            evaluationPanel.updateCourse(courseMap.get(courseCode));
        });

        // 1x2 grid layout ensuring equal width split between directory and evaluation panels
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);
        splitPanel.add(directoryPanel);
        splitPanel.add(evaluationPanel);
        enrolledDeck.add(splitPanel, BorderLayout.CENTER);

        tabDeck.add(enrolledDeck, "enrolled");

        // View 2: Course Registration / Enrollment View
        enrollmentView = new UGEnrollmentView(userId, this::loadDatabaseData);
        tabDeck.add(enrollmentView, "register");

        add(tabDeck, BorderLayout.CENTER);

        // Apply initial active styling and fetch enrolled courses from database
        updateTabStyles();
        loadDatabaseData();
    }

    /**
     * Applies shared typography, cursor, and dimension constraints to tab switch buttons.
     */
    private void styleTabPill(JButton btn) {
        btn.setFont(AdminTheme.font(Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(145, 32));
    }

    /**
     * Refreshes active and inactive pill button backgrounds and text colors.
     */
    private void updateTabStyles() {
        applyPillActive(enrolledTabBtn, "enrolled".equals(currentActiveTab));
        applyPillActive(registerTabBtn, "register".equals(currentActiveTab));
        repaint();
    }

    /**
     * Sets active white background or inactive transparent styling on a pill button.
     */
    private void applyPillActive(JButton btn, boolean isActive) {
        btn.setBackground(isActive ? Color.WHITE : new Color(0, 0, 0, 0));
        btn.setForeground(isActive ? AdminTheme.DEEP_GREEN : new Color(70, 95, 80));
        btn.setOpaque(isActive);
    }

    /**
     * Switches between Enrolled Modules and Course Registration views in CardLayout.
     * Dynamically updates the header title and refreshes view data.
     */
    private void switchSubTab(String tabKey) {
        this.currentActiveTab = tabKey;
        tabLayout.show(tabDeck, tabKey);
        updateTabStyles();

        if ("register".equals(tabKey)) {
            // Update header title and refresh course registration catalog
            headerPanel.updateHeader(
                    "Course Enrollment & Registration",
                    "Academic Year 2026/2027 • Faculty of Technology Department of ICT Courses"
            );
            enrollmentView.refreshData();
        } else {
            // Update header title and reload current active enrolled courses
            headerPanel.updateHeader(
                    "My Enrolled Courses",
                    "Academic Year 2026/2027 • Semester I Active Enrollments"
            );
            loadDatabaseData();
        }
    }

    /**
     * Queries database for enrolled courses, evaluation weight breakdowns, and calculates summary metrics.
     */
    private void loadDatabaseData() {
        List<EnrolledCourseDTO> courses = UndergraduateCourseDAO.getEnrolledCoursesWithEvaluation(userId);
        courseMap.clear();

        int totalCredits = 0;
        int coreCount = 0;
        int electiveCount = 0;

        // Populate in-memory lookup cache and tally total credits and curriculum tracks
        for (EnrolledCourseDTO c : courses) {
            courseMap.put(c.courseCode, c);
            totalCredits += c.totalCredits;
            if ("Normal".equalsIgnoreCase(c.enrollmentType)) {
                coreCount++;
            } else {
                electiveCount++;
            }
        }

        // Update top summary cards with computed metrics
        summaryStripPanel.updateMetrics(totalCredits, coreCount, electiveCount);
        // Bind course list to directory table
        directoryPanel.setCourses(courses);

        // Default-select the first course in evaluation panel if records exist
        if (!courses.isEmpty()) {
            evaluationPanel.updateCourse(courses.get(0));
        }
    }
}