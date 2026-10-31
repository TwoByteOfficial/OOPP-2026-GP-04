package gui.admin.NoticeComponents;

import javax.swing.*;
import java.awt.*;

public class NoticeTitleBar extends JPanel {
    private final Color DEEP_GREEN = new Color(0x1B, 0x5E, 0x20);
    private final Color MUTED      = new Color(0x4F, 0x6B, 0x52);
    private JLabel lblCount;

    public NoticeTitleBar() {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Notice Board & Announcement Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblCount = new JLabel("Create, publish, and target faculty announcements across user roles • 0 Notices Active");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCount.setForeground(MUTED);
        lblCount.setBounds(0, 45, 1000, 24);
        add(lblCount);
    }

    public void updateCount(int count) {
        updateNoticeCount(count);
    }

    public void updateNoticeCount(int count) {
        lblCount.setText("Create, publish, and target faculty announcements across user roles • " + count + " Announcements Published");
    }
}
