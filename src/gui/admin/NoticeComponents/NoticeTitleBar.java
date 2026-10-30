package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;
import javax.swing.*;
import java.awt.*;

public class NoticeTitleBar extends JPanel {
    private JLabel lblCount;

    public NoticeTitleBar() {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 80));
        setMinimumSize(new Dimension(1620, 80));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Notice Board & Announcement Management");
        lblTitle.setFont(AdminTheme.FONT_H1);
        lblTitle.setForeground(AdminTheme.DEEP_GREEN);
        lblTitle.setBounds(0, 5, 800, 38);
        add(lblTitle);

        lblCount = new JLabel("Create, publish, and target faculty announcements across user roles • 0 Notices Active");
        lblCount.setFont(AdminTheme.FONT_SMALL);
        lblCount.setForeground(AdminTheme.MUTED);
        lblCount.setBounds(0, 45, 1000, 24);
        add(lblCount);
    }

    public void updateCount(int count) {
        lblCount.setText("Create, publish, and target faculty announcements across user roles • " + count + " Announcements Published");
    }
}
