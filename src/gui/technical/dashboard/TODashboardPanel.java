
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



public class TODashboardPanel extends JPanel {

    private final String fullName;
    private final String role;
    private final Consumer<String> navigationHandler;

    public TODashboardPanel(
            String fullName,
            String role,
            Consumer<String> navigationHandler) {

        this.fullName = fullName;
        this.role = role;
        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout(0, 18));
        setBackground(UniMisTheme.LIGHT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(
                new DashboardHeaderPanel(this.fullName, this.role),
                BorderLayout.NORTH
        );

        add(buildDashboardContent(), BorderLayout.CENTER);
    }

    private JPanel buildDashboardContent() {

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        DashboardSummaryPanel summaryPanel =
                new DashboardSummaryPanel();

        DashboardQuickActionsPanel quickActionsPanel =
                new DashboardQuickActionsPanel(navigationHandler);

        DashboardOverviewPanel overviewPanel =
                new DashboardOverviewPanel();

        DashboardBottomPanel bottomPanel =
                new DashboardBottomPanel();

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
