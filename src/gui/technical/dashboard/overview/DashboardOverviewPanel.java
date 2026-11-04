package gui.technical.dashboard.overview;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Mid-section overview panel on the Technical Officer Dashboard.
 * Arranges the Attendance Overview stats and the Pending Medical Status panel side-by-side
 * in a 2-column GridLayout.
 */
public class DashboardOverviewPanel extends JPanel {

    /**
     * Constructs the overview grid container.
     *
     * @param userId            Database user ID of the Technical Officer
     * @param navigationHandler Callback to navigate to related dashboard views
     */
    public DashboardOverviewPanel(
            long userId,
            Consumer<String> navigationHandler) {

        // 1 row, 2 columns with 16px horizontal gap
        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        // Left column: Attendance breakdown (total, present, absent, medical, late)
        add(new AttendanceOverviewPanel(userId));

        // Right column: Pending medical certificate summary and review shortcut
        add(new MedicalStatusPanel(
                userId,
                navigationHandler
        ));
    }
}
