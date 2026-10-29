package gui.admin.UserProfileComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileTitleBar - Standalone component for displaying directory header title and profile counter.
 * Styled using theme colors and fonts from AdminTheme.
 */
public class UserProfileTitleBar extends JPanel {

    private final JLabel lblSubtitle;

    public UserProfileTitleBar(int totalUsers) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Faculty User Profiles Directory");
        lblTitle.setFont(AdminTheme.font(Font.BOLD, 28));
        lblTitle.setForeground(AdminTheme.DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblSubtitle = new JLabel("Registered faculty accounts (" + totalUsers + " active system profiles)");
        lblSubtitle.setFont(AdminTheme.font(Font.PLAIN, 16));
        lblSubtitle.setForeground(AdminTheme.MUTED);
        lblSubtitle.setBounds(0, 45, 800, 24);
        add(lblSubtitle);
    }

    public void updateCount(int totalUsers) {
        lblSubtitle.setText("Registered faculty accounts (" + totalUsers + " active system profiles)");
    }
}
