package gui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminTimetable extends JPanel {
    public AdminTimetable(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);
        JLabel l = new JLabel("Timetable page — coming soon", SwingConstants.CENTER);
        l.setFont(AdminTheme.FONT_H2);
        l.setForeground(AdminTheme.PRIMARY);
        add(l, BorderLayout.CENTER);
    }
}