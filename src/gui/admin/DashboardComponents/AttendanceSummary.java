package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class AttendanceSummary extends JPanel {
    public AttendanceSummary() {
        setLayout(null);
        setBounds(0, 0, 510, 240);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        JLabel lblTitle = new JLabel("Faculty Attendance Analytics");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0x1B, 0x5E, 0x20));
        lblTitle.setBounds(24, 20, 400, 25);
        add(lblTitle);

        JLabel lblPerc = new JLabel("92.6%");
        lblPerc.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblPerc.setForeground(new Color(0x38, 0x8E, 0x3C));
        lblPerc.setBounds(24, 65, 200, 50);
        add(lblPerc);

        JLabel lblDesc = new JLabel("Average tracking attendance across all registered cohorts this week.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(new Color(0x4F, 0x6B, 0x52));
        lblDesc.setBounds(24, 130, 460, 45);
        add(lblDesc);
    }
}
