package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class UserSummary extends JPanel {
    public UserSummary() {
        setLayout(null);
        setBounds(0, 0, 560, 240);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        JLabel lblTitle = new JLabel("User Account Registry Breakdown");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0x1B, 0x5E, 0x20));
        lblTitle.setBounds(24, 20, 400, 25);
        add(lblTitle);

        String[][] users = {{"Undergraduates", "1,150 Accounts"}, {"Lecturers", "68 Accounts"}, {"Technical Staff", "22 Accounts"}};
        for (int i = 0; i < users.length; i++) {
            JLabel lblRole = new JLabel(users[i][0]);
            lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblRole.setForeground(new Color(0x4F, 0x6B, 0x52));
            lblRole.setBounds(24, 65 + (i * 40), 200, 25);

            JLabel lblCount = new JLabel(users[i][1]);
            lblCount.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblCount.setForeground(new Color(0x1B, 0x5E, 0x20));
            lblCount.setBounds(400, 65 + (i * 40), 130, 25);

            add(lblRole);
            add(lblCount);
        }
    }
}
