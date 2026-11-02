package gui.undergraduate.dashboard.bottom;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Bottom Row combining Timetable & Notices with dynamic DB loading.
 */
public class DashboardBottomPanel extends JPanel {

    private final StudentTimetablePanel timetablePanel;
    private final LatestNoticesPanel noticesPanel;

    public DashboardBottomPanel(Consumer<String> navigationCallback) {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 16, 0));

        timetablePanel = new StudentTimetablePanel(navigationCallback);
        noticesPanel = new LatestNoticesPanel(navigationCallback);

        add(timetablePanel);
        add(noticesPanel);
    }

    public void refreshData(long studentId) {
        timetablePanel.loadTodaySchedule(studentId);
        noticesPanel.loadLatestNotices();
    }
}