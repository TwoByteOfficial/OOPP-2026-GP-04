package gui.undergraduate.dashboard;

import dao.undergraduate.UndergraduateDashboardDAO;
import dao.undergraduate.UndergraduateDashboardDAO.DashboardMetricsDTO;
import dao.undergraduate.UndergraduateProfileDAO;
import dao.undergraduate.UndergraduateTimetableDAO;
import gui.admin.AdminTheme;
import gui.undergraduate.dashboard.actions.DashboardQuickActionsPanel;
import gui.undergraduate.dashboard.bottom.DashboardBottomPanel;
import gui.undergraduate.dashboard.header.DashboardHeaderPanel;
import gui.undergraduate.dashboard.overview.OverviewBreakdownPanel;
import gui.undergraduate.dashboard.summary.SummaryCardPanel;
import gui.undergraduate.profile.model.UserProfileData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Main Overview Dashboard tab for Undergraduate Portal.
 * Visually calibrated to achieve exact pixel-level alignment with My Courses view.
 * Coordinates user headers, metric cards, quick actions, analytics breakdown, and daily schedule widgets.
 */
public class UGDashboardPanel extends JPanel {

    // Authenticated user session state
    private final long userId;
    private final String username;
    private final String fullName;
    private final String role;

    // Sub-view component references
    private final DashboardHeaderPanel headerPanel;
    private final SummaryCardPanel summaryCardPanel;
    private final OverviewBreakdownPanel overviewBreakdownPanel;
    private final DashboardBottomPanel bottomPanel;

    public UGDashboardPanel(long userId, String username, String fullName, String role, Consumer<String> onNavigate) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;

        // Base container styling and layout configuration
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);

        // Content panel tracks viewport width strictly to prevent horizontal layout shifting
        JPanel content = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Container parent = getParent();
                if (parent instanceof JViewport) {
                    d.width = ((JViewport) parent).getWidth();
                }
                return d;
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Exact outer padding as UGCoursesPanel: Top 18px, Left 22px, Bottom 14px, Right 22px
        content.setBorder(new EmptyBorder(18, 22, 14, 22));

        // -------------------------------------------------------------
        // 1. Header (Welcome text, student identity, and profile avatar)
        // -------------------------------------------------------------
        headerPanel = new DashboardHeaderPanel(username, fullName, role);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        content.add(headerPanel);

        // Exact 16px vertical gap between Header and Summary Cards
        content.add(Box.createVerticalStrut(16));

        // -------------------------------------------------------------
        // 2. Summary Cards (4 Compact Stat Cards, locked at 72px)
        // -------------------------------------------------------------
        summaryCardPanel = new SummaryCardPanel();
        summaryCardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        content.add(summaryCardPanel);
        content.add(Box.createVerticalStrut(12));

        // -------------------------------------------------------------
        // 3. Quick Action Buttons (Direct tab navigation shortcuts)
        // -------------------------------------------------------------
        DashboardQuickActionsPanel quickActions = new DashboardQuickActionsPanel(onNavigate);
        quickActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        content.add(quickActions);
        content.add(Box.createVerticalStrut(12));

        // -------------------------------------------------------------
        // 4. Attendance Distribution and Academic Status Analytics
        // -------------------------------------------------------------
        overviewBreakdownPanel = new OverviewBreakdownPanel();
        overviewBreakdownPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(overviewBreakdownPanel);
        content.add(Box.createVerticalStrut(12));

        // -------------------------------------------------------------
        // 5. Bottom Widgets (Today's Lecture Schedule & Latest Notices)
        // -------------------------------------------------------------
        bottomPanel = new DashboardBottomPanel(onNavigate);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(bottomPanel);

        // Scroll pane wrapper with clean borders and hidden scrollbars
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // Initial data retrieval on dashboard load
        refreshDashboardData();

        // Automatically trigger data refresh whenever the panel is re-displayed/switched into view
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshDashboardData();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    /**
     * Queries database asynchronously on the Event Dispatch Thread (EDT)
     * to fetch user profile, dashboard summary metrics, schedule slots, and notices.
     */
    public void refreshDashboardData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Fetch student profile metadata and update header labels and avatar
                UserProfileData profile = UndergraduateProfileDAO.getProfile(userId, username);
                if (profile != null && headerPanel != null) {
                    headerPanel.setStudentInfo(username, profile.getFullName(), role, profile.getProfilePicture());
                }

                // Query summary KPI metrics and update top 4 stat cards
                DashboardMetricsDTO metrics = UndergraduateDashboardDAO.getDashboardMetrics(userId);
                summaryCardPanel.updateDashboardMetrics(metrics);

                // Fetch student timetable slots and update mid-section analytics bars
                int slotsCount = UndergraduateTimetableDAO.getStudentWeeklyTimetable(userId).size();
                overviewBreakdownPanel.updateMetrics(metrics, slotsCount);

                // Reload today's lecture schedule and notices in bottom panel
                bottomPanel.refreshData(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}