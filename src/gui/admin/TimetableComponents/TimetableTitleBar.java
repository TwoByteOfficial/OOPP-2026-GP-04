package gui.admin.TimetableComponents;

import gui.admin.AdminTheme;
import javax.swing.*;
import java.awt.*;

public class TimetableTitleBar extends JPanel {

    private JLabel lblCount;

    public TimetableTitleBar() {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Academic Timetable & Schedule Management");
        lblTitle.setFont(AdminTheme.FONT_H1);
        lblTitle.setForeground(AdminTheme.DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblCount = new JLabel("Schedule theory lectures, practical labs, venues, and assigned staff • 0 Timetable Entries");
        lblCount.setFont(AdminTheme.FONT_SMALL);
        lblCount.setForeground(AdminTheme.MUTED);
        lblCount.setBounds(0, 45, 1000, 24);
        add(lblCount);
    }

    public void updateCount(int count) {
        updateTimetableCount(count);
    }

    public void updateTimetableCount(int count) {
        lblCount.setText("Schedule theory lectures, practical labs, venues, and assigned staff • " + count + " Timetable Entries");
    }
}
