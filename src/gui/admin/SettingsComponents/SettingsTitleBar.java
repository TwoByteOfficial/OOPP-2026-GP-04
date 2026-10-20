package gui.admin.SettingsComponents;

import javax.swing.*;
import java.awt.*;

public class SettingsTitleBar extends JPanel {
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);

    public SettingsTitleBar() {
        setLayout(null);
        setBounds(0, 0, 1620, 90);
        setOpaque(false);

        JLabel lblTitle = new JLabel("System Administrator Settings & Account Control");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(20, 15, 800, 40);
        add(lblTitle);

        JLabel lblSub = new JLabel("Manage admin profile, contact settings, security preferences, and system parameters");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(MUTED);
        lblSub.setBounds(20, 55, 1000, 24);
        add(lblSub);
    }
}
