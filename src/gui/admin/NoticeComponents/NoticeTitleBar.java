package gui.admin.NoticeComponents;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

public class NoticeTitleBar extends JPanel {

    private final JLabel lblCount;

    public NoticeTitleBar() {
        setLayout(new BorderLayout(0, 6));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Notice Board & Announcement Management");
        lblTitle.setFont(AdminTheme.FONT_H1);
        lblTitle.setForeground(AdminTheme.DEEP_GREEN);

        lblCount = new JLabel("Create, publish, and target faculty announcements across user roles • 0 Notices Active");
        lblCount.setFont(AdminTheme.FONT_SMALL);
        lblCount.setForeground(AdminTheme.MUTED);

        add(lblTitle, BorderLayout.NORTH);
        add(lblCount, BorderLayout.CENTER);
    }

    public void updateCount(int count) {
        updateNoticeCount(count);
    }

    public void updateNoticeCount(int count) {
        lblCount.setText("Create, publish, and target faculty announcements across user roles • " + count + " Announcements Published");
    }
}
