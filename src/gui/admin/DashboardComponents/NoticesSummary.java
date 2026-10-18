package gui.admin.DashboardComponents;

import javax.swing.*;
import java.awt.*;

public class NoticesSummary extends JPanel {
    public NoticesSummary() {
        setLayout(null);
        setBounds(0, 0, 1620, 430);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xC8, 0xE6, 0xC9), 1));

        JLabel lblTitle = new JLabel("Latest System Announcements Board");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0x1B, 0x5E, 0x20));
        lblTitle.setBounds(24, 20, 500, 25);
        add(lblTitle);

        String[][] boardData = {
                {"[SYSTEM]", "End-of-semester module enrollment portals close tonight at 23:59.", "2 hours ago"},
                {"[MAINTENANCE]", "Database index cluster structural optimization scheduled for Sunday morning.", "Yesterday"},
                {"[EXAMS]", "Exam hall seating distributions uploaded for Undergraduate Cohort 2026 Batch A.", "2 days ago"}
        };

        for (int i = 0; i < boardData.length; i++) {
            JPanel lineItem = new JPanel(null);
            lineItem.setBackground(new Color(0xE8, 0xF5, 0xE9)); // MINT accent strip
            lineItem.setBounds(24, 70 + (i * 105), 1572, 85);

            JLabel tag = new JLabel(boardData[i][0]);
            tag.setFont(new Font("Segoe UI", Font.BOLD, 14));
            tag.setForeground(new Color(0x38, 0x8E, 0x3C));
            tag.setBounds(20, 30, 150, 25);
            lineItem.add(tag);

            JLabel msg = new JLabel(boardData[i][1]);
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            msg.setForeground(new Color(0x1B, 0x5E, 0x20));
            msg.setBounds(180, 30, 1100, 25);
            lineItem.add(msg);

            JLabel time = new JLabel(boardData[i][2]);
            time.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            time.setForeground(new Color(0x4F, 0x6B, 0x52));
            time.setBounds(1450, 30, 100, 25);
            lineItem.add(time);

            add(lineItem);
        }
    }
}
