package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class StatusSummary extends JPanel {
    public StatusSummary() {
        setLayout(null);
        setBounds(0, 0, 510, 240);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        JLabel lblTitle = new JLabel("System Status Summary");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0x1B, 0x5E, 0x20));
        lblTitle.setBounds(24, 20, 400, 25);
        add(lblTitle);

        String[] metrics = {"Authentication Module: Operational", "Registration Framework: Online", "Grade Processing Engine: Idling"};
        for (int i = 0; i < metrics.length; i++) {
            JLabel lbl = new JLabel("• " + metrics[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lbl.setForeground(new Color(0x4F, 0x6B, 0x52));
            lbl.setBounds(24, 65 + (i * 35), 450, 25);
            add(lbl);
        }
    }
}
