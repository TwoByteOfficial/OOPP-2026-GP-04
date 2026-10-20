package gui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminCourses extends JPanel {
    public AdminCourses(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);
        JLabel l = new JLabel("Courses Management — Ready for modules configuration", SwingConstants.CENTER);
        l.setFont(AdminTheme.FONT_H2);
        l.setForeground(AdminTheme.PRIMARY);
        add(l, BorderLayout.CENTER);
    }
}
