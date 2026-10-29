package gui.admin.DashboardComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

public class DashboardTitleBar extends JPanel {

    public DashboardTitleBar(String fullName) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblGreeting = new JLabel("Welcome back, " + fullName);
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGreeting.setForeground(AdminTheme.DEEP_GREEN);
        lblGreeting.setBounds(0, 5, 1000, 38);
        add(lblGreeting);

        JLabel lblSubtext = new JLabel("UniMis Faculty Academic Management Information System • Administrative Overview");
        lblSubtext.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtext.setForeground(AdminTheme.MUTED);
        lblSubtext.setBounds(0, 45, 1000, 24);
        add(lblSubtext);
    }
}