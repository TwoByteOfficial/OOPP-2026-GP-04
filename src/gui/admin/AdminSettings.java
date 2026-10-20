package gui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {
    public AdminSettings(long userId, String username, String fullName, String email, String role) {
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT_BG);
        JLabel l = new JLabel("Admin System & Profile Settings — Operational Preferences", SwingConstants.CENTER);
        l.setFont(AdminTheme.FONT_H2);
        l.setForeground(AdminTheme.PRIMARY);
        add(l, BorderLayout.CENTER);
    }
}
