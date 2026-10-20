package gui.admin.TimetableComponents;

import javax.swing.*;
import java.awt.*;

public class TimetableTitleBar extends JPanel {
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);
    private JLabel lblCount;

    public TimetableTitleBar() {
        setLayout(null);
        setBounds(0, 0, 1620, 90);
        setOpaque(false);

        JLabel lblTitle = new JLabel("Academic Timetable & Schedule Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(20, 15, 800, 40);
        add(lblTitle);

        lblCount = new JLabel("Schedule theory lectures, practical labs, venues, and assigned staff • 0 Timetable Entries");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCount.setForeground(MUTED);
        lblCount.setBounds(20, 55, 1000, 24);
        add(lblCount);
    }

    public void updateTimetableCount(int count) {
        lblCount.setText("Schedule theory lectures, practical labs, venues, and assigned staff • " + count + " Entries Scheduled");
    }
}
