package gui.undergraduate.notices.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying faculty bulletin and circular notice instructions.
 */
public class NoticesHeaderPanel extends JPanel {

    public NoticesHeaderPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Official Notices & Academic Circulars");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Official announcements published by Faculty Dean's Office, Exam Unit, and Academic Departments.");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}