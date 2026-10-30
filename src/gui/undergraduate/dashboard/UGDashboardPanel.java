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
 * Uses a width-tracking viewport panel to prevent right-edge clipping.
 */
public class UGDashboardPanel extends JPanel {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String role;
    private final DashboardHeaderPanel headerPanel;
    private final SummaryCardPanel summaryCardPanel;
    private final OverviewBreakdownPanel overviewBreakdownPanel;
    private final DashboardBottomPanel bottomPanel;

    public UGDashboardPanel(long userId, String username, String fullName, String role, Consumer<String> onNavigate) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;

        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);

        // Content panel tracks viewport width strictly
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
        content.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Header (Welcome text and student avatar)
        headerPanel = new DashboardHeaderPanel(username, fullName, role);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(headerPanel);
        content.add(Box.createVerticalStrut(14));

        // 2. Summary Cards (4 Compact Stat Cards)
        summaryCardPanel = new SummaryCardPanel();
        summaryCardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(summaryCardPanel);
        content.add(Box.createVerticalStrut(14));

        // 3. Quick Action Buttons
        DashboardQuickActionsPanel quickActions = new DashboardQuickActionsPanel(onNavigate);
        quickActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(quickActions);
        content.add(Box.createVerticalStrut(14));

        // 4. Attendance Distribution and Academic Status
        overviewBreakdownPanel = new OverviewBreakdownPanel();
        overviewBreakdownPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(overviewBreakdownPanel);
        content.add(Box.createVerticalStrut(14));

        // 5. Bottom Widgets (Enrolled modules & Notices)
        bottomPanel = new DashboardBottomPanel(onNavigate);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(bottomPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        refreshDashboardData();

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

    public void refreshDashboardData() {
        SwingUtilities.invokeLater(() -> {
            try {
                UserProfileData profile = UndergraduateProfileDAO.getProfile(userId, username);
                if (profile != null && headerPanel != null) {
                    headerPanel.setStudentInfo(username, profile.getFullName(), role, profile.getProfilePicture());
                }

                DashboardMetricsDTO metrics = UndergraduateDashboardDAO.getDashboardMetrics(userId);
                summaryCardPanel.updateDashboardMetrics(metrics);

                int slotsCount = UndergraduateTimetableDAO.getStudentWeeklyTimetable(userId).size();
                overviewBreakdownPanel.updateMetrics(metrics, slotsCount);

                bottomPanel.refreshData(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}