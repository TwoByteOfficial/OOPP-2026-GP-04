package gui.undergraduate.dashboard;

import gui.admin.AdminTheme;
import gui.undergraduate.dashboard.actions.DashboardQuickActionsPanel;
import gui.undergraduate.dashboard.bottom.DashboardBottomPanel;
import gui.undergraduate.dashboard.header.DashboardHeaderPanel;
import gui.undergraduate.dashboard.overview.DashboardOverviewPanel;
import gui.undergraduate.dashboard.summary.DashboardSummaryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 1.1 - Undergraduate Main Dashboard Container
 * Rows 5 (Header, Summary, Quick Actions, Overview, Bottom)
 */
public class UGDashboardPanel extends JPanel {

    public UGDashboardPanel(long userId, String username, String fullName, String role, Consumer<String> navigationCallback) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);


        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(22, 28, 25, 28));

        // 1.1.1 - Header Section
        content.add(new DashboardHeaderPanel(fullName, username, role));

        // 1.1.2 - Summary Stat Cards (Enrolled Modules: 6, Attendance: 84.5%, SGPA: 3.65, CGPA: 3.58, Med: 2, 0)
        content.add(new DashboardSummaryPanel(6, 84.5, 3.65, 3.58, 2, 0));

        // 1.1.3 - Quick Actions Strip
        content.add(new DashboardQuickActionsPanel(navigationCallback));

        // 1.1.4 - Overview Section (Theory: 86.0%, Practical: 83.0%, Combined: 84.5%, Modules: 6, CA Done: 4, Med: 2, 0)
        content.add(new DashboardOverviewPanel(86.0, 83.0, 84.5, 6, 4, 2, 0));

        // 1.1.5 - Bottom Section (Timetable & Notices)
        content.add(new DashboardBottomPanel(navigationCallback));

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }
}