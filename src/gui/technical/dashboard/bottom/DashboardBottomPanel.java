package gui.technical.dashboard.bottom;

import gui.theme.UniMisTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Bottom container panel for the Technical Officer Dashboard.
 * Arranges the weekly Department Timetable and the Latest Notices panels
 * side-by-side using a 2-column GridLayout.
 */
public class DashboardBottomPanel extends JPanel {

    /**
     * Constructs the bottom dashboard section.
     *
     * @param userId Logged-in Technical Officer's database user ID
     */
    public DashboardBottomPanel(long userId) {

        // 1 row, 2 columns with 16px horizontal spacing
        setLayout(new GridLayout(1, 2, 16, 0));
        setOpaque(false);
        setBackground(UniMisTheme.LIGHT_BG);

        // Left column: Department Timetable schedule
        add(new DepartmentTimetablePanel(userId));

        // Right column: Latest campus notices
        add(new LatestNoticesPanel(userId));
    }
}
