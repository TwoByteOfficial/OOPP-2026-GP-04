package gui.admin.CourseComponents;

import javax.swing.*;
import java.awt.*;

public class CourseTitleBar extends JPanel {
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);
    private JLabel lblCount;

    public CourseTitleBar() {
        setLayout(null);
        setBounds(0, 0, 1620, 90);
        setOpaque(false);

        JLabel lblTitle = new JLabel("Course & Academic Module Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(20, 15, 800, 40);
        add(lblTitle);

        lblCount = new JLabel("Manage faculty courses, credit allocations, and lecturer assignments • 0 Modules");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCount.setForeground(MUTED);
        lblCount.setBounds(20, 55, 1000, 24);
        add(lblCount);
    }

    public void updateCourseCount(int count) {
        lblCount.setText("Manage faculty courses, credit allocations, and lecturer assignments • " + count + " Active Modules");
    }
}
