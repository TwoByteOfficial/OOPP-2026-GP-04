package gui.undergraduate.courses.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying dynamic title and academic term captions.
 * Updates seamlessly when toggling between enrolled courses and course registration tabs.
 */
public class CoursesHeaderPanel extends JPanel {

    // Main header title and subtitle labels
    private final JLabel title = new JLabel("My Enrolled Courses");
    private final JLabel subtitle = new JLabel("Academic Year 2026/2027 • Semester I Active Enrollments");

    public CoursesHeaderPanel() {
        // Set transparent background and left-aligned border layout
        setOpaque(false);
        setLayout(new BorderLayout());
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Vertical text wrapper for title and subtitle
        JPanel textContainer = new JPanel();
        textContainer.setOpaque(false);
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));

        // Configure main title typography and theme color
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Configure subtitle typography with muted secondary color
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble labels with a subtle 2px vertical gap
        textContainer.add(title);
        textContainer.add(Box.createVerticalStrut(2));
        textContainer.add(subtitle);

        add(textContainer, BorderLayout.WEST);
    }

    /**
     * Dynamically updates header titles when switching between Enrolled Modules and Registration views.
     */
    public void updateHeader(String mainTitle, String subTitle) {
        title.setText(mainTitle);
        subtitle.setText(subTitle);
        revalidate();
        repaint();
    }
}