package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

/**
 * Header title bar for User Profiles Management page.
 */
public class UserProfileTitleBar extends JPanel {

    private final Color DEEP_GREEN  = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED       = new Color(0x4F, 0x6B, 0x52);

    private JLabel lblUserCount;

    public UserProfileTitleBar(int totalUsersCount) {
        setLayout(null);
        setBounds(0, 0, 1620, 85);
        setOpaque(false);

        JLabel lblTitle = new JLabel("Faculty User Profiles Directory");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28)); // H1 kept at 28px
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(20, 12, 800, 38);
        add(lblTitle);

        lblUserCount = new JLabel("System Directory • " + totalUsersCount + " Total Registered User Profiles");
        lblUserCount.setFont(new Font("Segoe UI", Font.PLAIN, 17)); // 15 -> 17
        lblUserCount.setForeground(MUTED);
        lblUserCount.setBounds(20, 50, 800, 24);
        add(lblUserCount);
    }

    public void updateUserCount(int count) {
        lblUserCount.setText("System Directory • " + count + " Total Registered User Profiles");
    }
}
