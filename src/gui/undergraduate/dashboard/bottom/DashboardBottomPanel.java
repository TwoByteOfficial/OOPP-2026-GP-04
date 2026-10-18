package gui.undergraduate.dashboard.bottom;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 1.1.5 - Bottom Row combining Timetable & Notices
 */
public class DashboardBottomPanel extends JPanel {

    public DashboardBottomPanel(Consumer<String> navigationCallback) {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 16, 0));

        // 1.1.5.1
        add(new StudentTimetablePanel(navigationCallback));

        // 1.1.5.2
        add(new LatestNoticesPanel(navigationCallback));
    }
}