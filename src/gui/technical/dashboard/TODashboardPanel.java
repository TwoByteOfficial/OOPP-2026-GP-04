package gui.technical.dashboard;

import java.util.function.Consumer;
import gui.theme.UniMisTheme;
import gui.technical.dashboard.header.DashboardHeaderPanel;
import gui.technical.dashboard.summary.DashboardSummaryPanel;
import gui.technical.dashboard.actions.DashboardQuickActionsPanel;
import gui.technical.dashboard.overview.DashboardOverviewPanel;
import gui.technical.dashboard.bottom.DashboardBottomPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main overview panel for the Technical Officer.
 * Assembles all dashboard sections vertically inside a scrollable view:
 * 1. Header Panel: Greeting with officer's name and role badge
 * 2. Summary Panel: Metric cards (Undergraduates, Pending Medicals, Attendance, Today's Sessions)
 * 3. Quick Actions Panel: Direct shortcut buttons (Mark Attendance, Review Medicals, etc.)
 * 4. Overview Panel: Visual attendance chart breakdown and pending medical list
 * 5. Bottom Panel: Weekly timetable preview and latest published campus notices
 */
public class TODashboardPanel extends JPanel {

    // Logged-in Technical Officer's information
    private final String fullName;
    private final String role;
    private final Consumer<String> navigationHandler;
    private final long userId;

    /**
     * Constructs the Technical Officer Dashboard landing page.
     *
     * @param userId            Database user ID of the officer
     * @param fullName          Full name of the officer
     * @param role              Role title ("Technical Officer")
     * @param navigationHandler Callback to switch to other sidebar tabs
     */
    public TODashboardPanel(
            long userId,
            String fullName,
            String role,
            Consumer<String> navigationHandler) {

        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout(0, 18));
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Welcome header banner
        add(
                new DashboardHeaderPanel(this.fullName, this.role),
                BorderLayout.NORTH
        );

        // Core dashboard body
        add(buildDashboardContent(), BorderLayout.CENTER);
    }

    /**
     * Assembles the stacked vertical sections of the dashboard.
     *
     * @return JPanel containing summary cards, quick actions, charts, timetable, and notices
     */
    private JPanel buildDashboardContent() {

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // 1. KPI summary cards
        DashboardSummaryPanel summaryPanel = new DashboardSummaryPanel(userId);

        // 2. Action buttons
        DashboardQuickActionsPanel quickActionsPanel = new DashboardQuickActionsPanel(navigationHandler);

        // 3. Analytics & pending medical items
        DashboardOverviewPanel overviewPanel = new DashboardOverviewPanel(userId, navigationHandler);

        // 4. Timetable & latest notices
        DashboardBottomPanel bottomPanel = new DashboardBottomPanel(userId);

        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        overviewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(summaryPanel);
        content.add(Box.createVerticalStrut(18));

        content.add(quickActionsPanel);
        content.add(Box.createVerticalStrut(18));

        content.add(overviewPanel);
        content.add(Box.createVerticalStrut(18));

        content.add(bottomPanel);

        return content;
    }
}
