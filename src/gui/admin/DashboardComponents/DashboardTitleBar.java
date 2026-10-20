package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

/**
 * Header greeting title bar for Admin Dashboard.
 */
public class DashboardTitleBar extends JPanel {

    private final Color DEEP_GREEN  = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED       = new Color(0x4F, 0x6B, 0x52);

    public DashboardTitleBar(String fullName) {
        setLayout(null);
        setBounds(0, 0, 1620, 90);
        setOpaque(false);

        JLabel lblGreeting = new JLabel("Welcome back, " + fullName);
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 28)); // H1 kept at 28px
        lblGreeting.setForeground(DEEP_GREEN);
        lblGreeting.setBounds(20, 15, 1000, 40);
        add(lblGreeting);

        JLabel lblSubtext = new JLabel("UniMis Faculty Academic Management Information System • Administrative Overview");
        lblSubtext.setFont(new Font("Segoe UI", Font.PLAIN, 17)); // 15 -> 17
        lblSubtext.setForeground(MUTED);
        lblSubtext.setBounds(20, 55, 1000, 24);
        add(lblSubtext);
    }
}
