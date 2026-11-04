
package gui.lecturer.MyCourse.MyCourseComponents;

import javax.swing.*;
import java.awt.*;

public class MyCourseTitleBar extends JPanel {

    private static final Color DEEP_GREEN =
            new Color(27, 94, 32);

    public MyCourseTitleBar() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JLabel title = new JLabel("My Courses");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel(
                "View your assigned courses and enrolled undergraduates."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(title);
        add(Box.createVerticalStrut(8));
        add(subtitle);
    }
}
