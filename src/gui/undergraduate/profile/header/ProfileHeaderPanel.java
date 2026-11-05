package gui.undergraduate.profile.header;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Header panel displaying undergraduate profile credentials and account management title.
 * Provides the top banner with view branding and descriptive context regarding
 * student academic records and authentication settings.
 */
public class ProfileHeaderPanel extends JPanel {

    /**
     * Constructs the profile header panel with vertical box layout and standardized typography.
     */
    public ProfileHeaderPanel() {
        // Base container layout and transparency configuration
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // -------------------------------------------------------------
        // Main View Title (Using H1 theme font and deep green accent)
        // -------------------------------------------------------------
        JLabel title = new JLabel("My Student Profile & Contact Settings");
        title.setFont(AdminTheme.FONT_H1);
        title.setForeground(AdminTheme.DEEP_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -------------------------------------------------------------
        // Subtitle Indicating Profile Scope and Setting Context
        // -------------------------------------------------------------
        JLabel subtitle = new JLabel("Official undergraduate credentials, degree enrolment records, and authentication settings.");
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