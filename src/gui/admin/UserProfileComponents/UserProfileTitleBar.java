package gui.admin.UserProfileComponents;

import javax.swing.*;
import java.awt.*;

public class UserProfileTitleBar extends JPanel {

    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);

    private final JLabel lblSubtitle;

    public UserProfileTitleBar(int totalUsers) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Faculty User Profiles Directory");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblSubtitle = new JLabel("Registered faculty accounts (" + totalUsers + " active system profiles)");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(MUTED);
        lblSubtitle.setBounds(0, 45, 800, 24);
        add(lblSubtitle);
    }

    public void updateCount(int totalUsers) {
        lblSubtitle.setText("Registered faculty accounts (" + totalUsers + " active system profiles)");
    }
}
