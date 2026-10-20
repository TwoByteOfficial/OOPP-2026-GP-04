package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * UserProfileTitleBar - Header title bar component for the User Profiles management section.
 */
public class UserProfileTitleBar extends JPanel {

    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);

    private final JLabel lblSubtitle;

    public UserProfileTitleBar(int totalUsers) {
        setLayout(null);
        setBounds(0, 0, 1620, 80);
        setOpaque(false);

        JLabel lblTitle = new JLabel("Faculty User Profiles Directory");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(20, 10, 800, 38);
        add(lblTitle);

        lblSubtitle = new JLabel("Registered faculty accounts (" + totalUsers + " active system profiles)");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(MUTED);
        lblSubtitle.setBounds(20, 48, 800, 24);
        add(lblSubtitle);
    }

    public void updateCount(int totalUsers) {
        lblSubtitle.setText("Registered faculty accounts (" + totalUsers + " active system profiles)");
    }
}
