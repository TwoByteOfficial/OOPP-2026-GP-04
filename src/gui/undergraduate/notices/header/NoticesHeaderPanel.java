package gui.undergraduate.notices.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying faculty bulletin and circular notice instructions.
 * Provides standard title branding, departmental authority subtitle, and layout alignment
 * for the Undergraduate Notices & Academic Circulars module.
 */
public class NoticesHeaderPanel extends JPanel {

    /**
     * Constructs the notices header panel with vertical box layout and left-aligned labels.
     */
    public NoticesHeaderPanel() {
        // Base container layout configuration and transparency
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // -------------------------------------------------------------
        // Main View Title (Using theme standard H1 font and deep green)
        // -------------------------------------------------------------
        JLabel title = new JLabel("Official Notices & Academic Circulars");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Subtitle Indicating Issuing Authorities (Dean's Office, Exam Unit, Departments)
        // -------------------------------------------------------------
        JLabel subtitle = new JLabel("Official announcements published by Faculty Dean's Office, Exam Unit, and Academic Departments.");
        subtitle.setFont(AdminTheme.font(Font.PLAIN, 13));
        subtitle.setForeground(AdminTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Component Assembly with 2px Vertical Spacing
        // -------------------------------------------------------------
        add(title);
        add(Box.createVerticalStrut(2));
        add(subtitle);
    }
}