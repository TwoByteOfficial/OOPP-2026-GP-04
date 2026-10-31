package gui.undergraduate.grades.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying Grades and GPA records title with semester progression indicator.
 */
public class GradesHeaderPanel extends JPanel {

    public GradesHeaderPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Academic Grades & GPA Performance");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Official Academic Transcript • Level II Semester I Records");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}