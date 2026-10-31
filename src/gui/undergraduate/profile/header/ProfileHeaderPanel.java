package gui.undergraduate.profile.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying undergraduate profile credentials and account management title.
 */
public class ProfileHeaderPanel extends JPanel {

    public ProfileHeaderPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("My Student Profile & Contact Settings");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Official undergraduate credentials, degree enrolment records, and authentication settings.");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}