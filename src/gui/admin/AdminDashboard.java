package gui.admin;

import gui.admin.DashboardComponents.*;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard page.
 * Composes all dashboard components (title bar + summary widgets).
 * Currently shows simple logged-in-admin test data so sub-components
 * can be developed/tested one by one.
 */
public class AdminDashboard extends JPanel {

    private final long   userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;

    public AdminDashboard(long userId, String username, String fullName,
                          String email, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);

        /* Top: title bar (page header with greeting) */
        add(new DashboardTitleBar(userId, username, fullName, email, role),
                BorderLayout.NORTH);

        /* Center: body with all summary widgets */
        JScrollPane scroll = new JScrollPane(buildBody(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(AdminTheme.LIGHT_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(AdminTheme.LIGHT_BG);
        body.setBorder(AdminTheme.emptyBorder(25, 35, 25, 35));

        /* Row 1: Four summary cards */
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 18, 18));
        statsGrid.setOpaque(false);
        statsGrid.add(new UserSummary(userId, username, fullName, email, role));
        statsGrid.add(new NoticesSummary(userId, username, fullName, email, role));
        statsGrid.add(new AttendanceSummary(userId, username, fullName, email, role));
        statsGrid.add(new StatusSummary(userId, username, fullName, email, role));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(statsGrid);

        body.add(Box.createVerticalStrut(22));

        /* Row 2: Analytic/overview panel */
        SimpleAnalyticView analytics =
                new SimpleAnalyticView(userId, username, fullName, email, role);
        analytics.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(analytics);

        body.add(Box.createVerticalGlue());
        return body;
    }
}