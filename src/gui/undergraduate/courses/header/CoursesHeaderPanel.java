package gui.undergraduate.courses.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel for My Courses module displaying page title and academic semester info.
 */
public class CoursesHeaderPanel extends JPanel {

    public CoursesHeaderPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("My Enrolled Courses");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Academic Year 2026/2027 • Semester I Active Enrollments");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}