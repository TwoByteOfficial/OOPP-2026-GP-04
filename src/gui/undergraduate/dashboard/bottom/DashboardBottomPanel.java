package gui.undergraduate.dashboard.bottom;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Bottom Row combining Timetable & Notices with dynamic DB loading.
 * Renders the student's daily lecture schedule and latest relevant notices side by side.
 */
public class DashboardBottomPanel extends JPanel {

    // Sub-panels for today's lecture schedule and recent notices feed
    private final StudentTimetablePanel timetablePanel;
    private final LatestNoticesPanel noticesPanel;

    public DashboardBottomPanel(Consumer<String> navigationCallback) {
        // Transparent container with a 1x2 grid layout (16px horizontal gap)
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 16, 0));

        // Initialize timetable and notices panels passing the navigation callback
        timetablePanel = new StudentTimetablePanel(navigationCallback);
        noticesPanel = new LatestNoticesPanel(navigationCallback);

        // Add both component cards to the grid container
        add(timetablePanel);
        add(noticesPanel);
    }

    /**
     * Refreshes and reloads live database data for both schedule and notices components.
     */
    public void refreshData(long studentId) {
        timetablePanel.loadTodaySchedule(studentId);
        noticesPanel.loadLatestNotices();
    }
}