package gui.undergraduate.dashboard;

import gui.admin.AdminTheme;
import gui.undergraduate.dashboard.actions.DashboardQuickActionsPanel;
import gui.undergraduate.dashboard.bottom.DashboardBottomPanel;
import gui.undergraduate.dashboard.header.DashboardHeaderPanel;
import gui.undergraduate.dashboard.overview.OverviewBreakdownPanel;
import gui.undergraduate.dashboard.summary.SummaryCardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Main Overview Dashboard tab for Undergraduate Portal.
 * Cleanly organizes metrics, charts, and shortcuts without unnecessary scrollbars.
 */
public class UGDashboardPanel extends JPanel {

    public UGDashboardPanel(long userId, String username, String fullName, String role, Consumer<String> onNavigate) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);

        // Content panel layout with standardized padding
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Header (Welcome text)
        content.add(new DashboardHeaderPanel(username, fullName, role));
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
    }
}