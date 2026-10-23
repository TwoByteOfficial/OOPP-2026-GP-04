package gui.undergraduate.timetable.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Metric summary strip aggregating weekly schedule statistics.
 */
public class TimetableSummaryStripPanel extends JPanel {

    public TimetableSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 75));

        add(new TimetableStatCard("22 Hours", "Weekly Workload", AdminTheme.FOREST_GREEN));
        add(new TimetableStatCard("8 Modules", "Active Courses", AdminTheme.FOREST_GREEN));
        add(new TimetableStatCard("6 Labs", "Practical Sessions", AdminTheme.MEDIUM_GREEN));
        add(new TimetableStatCard("5 Days", "Active Lecture Days", AdminTheme.DEEP_GREEN));
    }
}