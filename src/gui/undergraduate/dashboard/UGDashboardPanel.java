package gui.undergraduate.dashboard;

import dao.undergraduate.UndergraduateProfileDAO;
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
 * Cleanly organizes metrics, charts, and shortcuts with live profile synchronization.
 */
public class UGDashboardPanel extends JPanel {

    private final long userId;
    private final String username;
    private final String fullName;
    private final String role;
    private final DashboardHeaderPanel headerPanel;

    public UGDashboardPanel(long userId, String username, String fullName, String role, Consumer<String> onNavigate) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;

        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);

        // Content panel layout with standardized padding
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Header (Welcome text and student avatar)
        headerPanel = new DashboardHeaderPanel(username, fullName, role);
        content.add(headerPanel);
        content.add(Box.createVerticalStrut(14));

        // 2. Summary Cards (4 Compact Stat Cards)
        content.add(new SummaryCardPanel());
        content.add(Box.createVerticalStrut(14));

        // 3. Quick Action Buttons
        content.add(new DashboardQuickActionsPanel(onNavigate));
        content.add(Box.createVerticalStrut(14));

        // 4. Attendance Distribution and Academic Status
        content.add(new OverviewBreakdownPanel());
        content.add(Box.createVerticalStrut(14));

        // 5. Bottom Widgets (Enrolled modules & Notices)
        content.add(new DashboardBottomPanel(onNavigate));

        // Scroll pane configured to display clean layout
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Initial load of header profile picture and names
        refreshDashboardHeader();

        // Auto-refresh header every time student navigates back to Dashboard
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshDashboardHeader();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    /**
     * Re-queries latest student profile information from the database and updates top header.
     */
    public void refreshDashboardHeader() {
        SwingUtilities.invokeLater(() -> {
            try {
                UserProfileData profile = UndergraduateProfileDAO.getProfile(userId, username);
                if (profile != null && headerPanel != null) {
                    headerPanel.setStudentInfo(username, profile.getFullName(), role, profile.getProfilePicture());
                }
            } catch (Exception ignored) {}
        });
    }
}