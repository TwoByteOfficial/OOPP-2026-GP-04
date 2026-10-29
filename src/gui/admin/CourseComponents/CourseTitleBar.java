package gui.admin.CourseComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

public class CourseTitleBar extends JPanel {

    private final JLabel lblSubtitle;

    public CourseTitleBar() {
        this(0);
    }

    public CourseTitleBar(int totalCourses) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Academic Course Modules Directory");
        lblTitle.setFont(AdminTheme.FONT_H2);
        lblTitle.setForeground(AdminTheme.DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblSubtitle = new JLabel("Registered course modules (" + totalCourses + " active system modules)");
        lblSubtitle.setFont(AdminTheme.FONT_SMALL);
        lblSubtitle.setForeground(AdminTheme.MUTED);
        lblSubtitle.setBounds(0, 45, 800, 24);
        add(lblSubtitle);
    }

    public void updateCount(int totalCourses) {
        lblSubtitle.setText("Registered course modules (" + totalCourses + " active system modules)");
    }
}
